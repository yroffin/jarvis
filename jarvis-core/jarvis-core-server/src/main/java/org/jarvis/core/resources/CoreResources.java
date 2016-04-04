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

import static spark.Spark.get;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;

import org.jarvis.core.services.cache.CoreEhcacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;
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
	    if(coreEhcacheManager.contain(key)) {
	    	logger.debug("Cached data {}", key);
	    	return coreEhcacheManager.get(key);
	    } else {
	        InputStream inputStream = getClass().getResourceAsStream(key);
	        if (inputStream != null) {
		        ResourceData value = null;
	        	if(key.endsWith(".html")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("text/html; charset=utf-8");
	        	}
	        	if(key.endsWith(".markdown")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("text/html; charset=utf-8");
	        	}
	        	if(key.endsWith(".css")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("text/css; charset=utf-8");
	        	}
	        	if(key.endsWith(".js")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("application/javascript; charset=utf-8");
	        	}
	        	if(key.endsWith(".json")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("text/html; charset=utf-8");
	        	}
	        	if(key.endsWith(".svg")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("text/html; charset=utf-8");
	        	}
	        	if(key.endsWith(".ico")) {
			        value = new ResourceData(IOUtils.toString(inputStream));
	        		value.setContentType("image/x-icon");
	        	}
	        	if(key.endsWith(".png")) {
			        value = new ResourceData(IOUtils.toByteArray(inputStream));
	        		value.setContentType("image/x-png");
	        	}
	        	if(key.endsWith(".jpg")) {
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
	 * mount local resource
	 */
	public void mount() {
		/**
		 * mount resources
		 */
		get("/ui/*", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		        String path = request.pathInfo().replaceFirst("/ui/", "/public/");
		        ResourceData value = getData(path);
		        if(value != null) {
	        		response.type(value.getContentType());
		            response.status(200);
		            if(value.isOctetStream()) {
		            	final ServletOutputStream os = response.raw().getOutputStream();
		            	os.write(value.getByte());
		                os.close();
			        	return null;
		            } else {
		            	return value.getString();
		            }
		        } else {
		        	/**
		        	 * resources is not found
		        	 */
		        	response.status(404);
		        	return "";
		        }
		    }
		});
	}
}
