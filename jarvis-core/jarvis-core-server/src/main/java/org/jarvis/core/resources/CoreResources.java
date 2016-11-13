/**
 *  Copyright 2015 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.core.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.services.cache.CoreEhcacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * core resources (ui, ...)
 */
@Component
public class CoreResources {
	protected Logger logger = LoggerFactory.getLogger(CoreResources.class);

	@Autowired
	Environment env;

	@Autowired
	CoreEhcacheManager coreEhcacheManager;

	/**
	 * work dir
	 */
	private File resources = null;
	
	/**
	 * create resource from classpath
	 * @param sourcePath
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private File copyFromClasspath(final String sourcePath) throws URISyntaxException, IOException {
		/**
		 * create empty work dir
		 */
		File workDir = File.createTempFile("jarvis", "resources");
		workDir.delete();
		workDir.mkdirs();

		InputStream list = this.getClass().getClassLoader().getResourceAsStream(sourcePath);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(list));
        String line;
        while ((line = reader.readLine()) != null) {
        	/**
        	 * decode line
        	 */
            String directory = line.split(";")[0];
            String file = line.split(";")[1];
            
            /**
             * create directory
             */
            File created = new File(workDir.getAbsolutePath() + '/' + directory);
            if(!created.exists()) {
            	created.mkdirs();
            }
            
            /**
             * then copy file
             */
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream(directory + "/" + file);
            if(resource == null) {
            	logger.error("No such resource {}", directory + "/" + file);
            } else {
	            Files.copy(resource, Paths.get(created.getAbsolutePath() + "/" + file),
						StandardCopyOption.REPLACE_EXISTING);
            	logger.info("Resource {} copied", directory + "/" + file);
	            resource.close();
            }
        }
        reader.close();
        list.close();
        
        return workDir;
	}

	/**
	 * mount local resource
	 */
	public void mountLocal() {
		logger.info("Mount local files on {}", "public");
		spark.Spark.staticFiles.location("/public");
		spark.Spark.staticFiles.externalLocation("public");
		spark.Spark.staticFiles.expireTime(600);
	}

	/**
	 * deltree
	 * @param dir
	 * @throws Exception
	 */
	private static void deltree(File dir) throws Exception {

        String[] list = dir.list();
        for (int i = 0; i < list.length; i++) {
            String s = list[i];
            File f = new File(dir, s);
            if (f.isDirectory()) {
            	deltree(f);
            } else {
                if (!f.delete()) {
                    throw new TechnicalException("Unable to delete file "
                                             + f.getAbsolutePath());
                }
            }
        }
        if (!dir.delete()) {
            throw new TechnicalException("Unable to delete directory "
                                     + dir.getAbsolutePath());
        }
    }

	/**
	 * mount local resource
	 */
	public void mountExternal() {
		/**
		 * mount resources
		 */
		spark.Spark.staticFiles.location("/public");
		try {
			resources = copyFromClasspath("public/ui.txt");
			if(resources == null) {
				throw new TechnicalException("No static files");
			}
			spark.Spark.staticFiles.externalLocation(resources.getAbsolutePath() + "/public");
		} catch (URISyntaxException | IOException e) {
			logger.error("While creating resources files {}", e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	logger.warn("Delete resource {}", resources.getAbsolutePath());
	        	try {
	        		deltree(resources);
				} catch (Exception e) {
		        	logger.warn("While deleting {} {}", resources.getAbsolutePath(), e);
				}
	        }
	    });

		// just 1 second expire time
		// TODO : configure it in properties
		spark.Spark.staticFiles.expireTime(1);
	}
}
