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

import java.io.InputStream;
import java.io.Writer;

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

	@Autowired
	Environment env;

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
		        InputStream inputStream = getClass().getResourceAsStream(path);
		        if (inputStream != null) {
		        	if(path.endsWith(".html")) {
		        		response.type("text/html; charset=utf-8");
		        	}
		        	if(path.endsWith(".css")) {
		        		response.type("text/css; charset=utf-8");
		        	}
		        	if(path.endsWith(".js")) {
		        		response.type("application/javascript; charset=utf-8");
		        	}
		        	if(path.endsWith(".json")) {
		        		response.type("text/html; charset=utf-8");
		        	}
		        	if(path.endsWith(".ico")) {
		        		response.type("image/x-icon");
		        	}
		            response.status(200);
		            Writer writer = response.raw().getWriter();
		            IOUtils.copy(inputStream, writer);
		            writer.close();
		            inputStream.close();
		        } else {
		        	/**
		        	 * resources is not found
		        	 */
		        	response.status(404);
		        }
	        	return "";
		    }
		});
	}
}
