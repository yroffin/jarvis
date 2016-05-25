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

package org.jarvis.core.model.bean.websocket;

/**
 * simple shell for object stored on web socket channel
 */
public class WebsocketDataBean {
	/**
	 * classname
	 */
	private String classname;
	/**
	 * instance
	 */
	private String instance;
	/**
	 * data
	 */
	private Object data;
	
	/**
	 * simple constructor
	 * @param instance
	 * @param data
	 */
	public WebsocketDataBean(String instance, Object data) {
		super();
		this.classname = data.getClass().getSimpleName();
		this.instance = instance;
		this.data = data;
	}

	/**
	 * @return String
	 */
	public String getClassname() {
		return classname;
	}
	
	/**
	 * @return String
	 */
	public String getInstance() {
		return instance;
	}
	
	/**
	 * @return Object
	 */
	public Object getData() {
		return data;
	}
}
