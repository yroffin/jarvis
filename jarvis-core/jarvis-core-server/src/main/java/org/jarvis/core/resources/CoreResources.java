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

import spark.utils.IOUtils;

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

	ResourceData getData(String key) throws IOException {
		if (coreEhcacheManager.contains(key)) {
			logger.debug("Cached data {}", key);
			return coreEhcacheManager.get(key);
		} else {
			logger.debug("Un-cached data {}", key);
			InputStream inputStream = getClass().getResourceAsStream(key);
			if (inputStream != null) {
				ResourceData value = null;
				if (key.endsWith(".html")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("text/html; charset=utf-8");
				}
				if (key.endsWith(".markdown")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("text/html; charset=utf-8");
				}
				if (key.endsWith(".css")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("text/css; charset=utf-8");
				}
				if (key.endsWith(".js")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("application/javascript; charset=utf-8");
				}
				if (key.endsWith(".json")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("text/html; charset=utf-8");
				}
				if (key.endsWith(".svg")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("text/html; charset=utf-8");
				}
				if (key.endsWith(".ico")) {
					value = new ResourceData(IOUtils.toString(inputStream));
					value.setContentType("image/x-icon");
				}
				if (key.endsWith(".png")) {
					value = new ResourceData(IOUtils.toByteArray(inputStream));
					value.setContentType("image/x-png");
				}
				if (key.endsWith(".jpg")) {
					value = new ResourceData(IOUtils.toByteArray(inputStream));
					value.setContentType("image/x-jpg");
				}
				inputStream.close();
				return coreEhcacheManager.put(key, value);
			} else {
				return null;
			}
		}
	}

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
            Files.copy(resource, Paths.get(created.getAbsolutePath() + "/" + file),
					StandardCopyOption.REPLACE_EXISTING);
            
            resource.close();
        }
        reader.close();
        list.close();
        
        return workDir;
	}

	/**
	 * work dir
	 */
	public static File resources = null;
	
	/**
	 * mount local resource
	 */
	public void mountLocal() {
		spark.Spark.staticFileLocation("public");
		spark.Spark.staticFiles.expireTime(600);
	}
	
	/**
	 * mount local resource
	 */
	public void mountExternal() {
		/**
		 * mount resources
		 */
		try {
			resources = copyFromClasspath("public/ui.txt");
			if(resources == null) {
				throw new TechnicalException("No static files");
			}
			spark.Spark.externalStaticFileLocation(resources.getAbsolutePath() + "/public");
		} catch (URISyntaxException | IOException e) {
			logger.error("While creatting resources files {}", e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			private void deltree(File dir) throws Exception {

		        // check to make sure that the given dir isn't a symlink
		        // the comparison of absolute path and canonical path
		        // catches this

		        //        if (dir.getCanonicalPath().equals(dir.getAbsolutePath())) {
		        // (costin) It will not work if /home/costin is symlink to
		        // /da0/home/costin ( taz for example )
		        String[] list = dir.list();
		        for (int i = 0; i < list.length; i++) {
		            String s = list[i];
		            File f = new File(dir, s);
		            if (f.isDirectory()) {
		            	deltree(f);
		            } else {
		                if (!f.delete()) {
		                    throw new Exception("Unable to delete file "
		                                             + f.getAbsolutePath());
		                }
		            }
		        }
		        if (!dir.delete()) {
		            throw new Exception("Unable to delete directory "
		                                     + dir.getAbsolutePath());
		        }
		    }
			
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

		spark.Spark.staticFiles.expireTime(600);
	}
}
