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

package org.jarvis.rest.services;

import java.util.Map;

import org.jarvis.rest.services.impl.JarvisModuleException;

/**
 * jarvis connector
 */
public interface JarvisConnector {

	/**
	 * message handler
	 * @param message
	 * @return Map
	 * @throws JarvisModuleException
	 */
	Map<String, Object> onNewMessage(Map<String, Object> message) throws JarvisModuleException;

	/**
	 * @return id
	 */
	public String getId();

	/**
	 * @return name
	 */
	public String getName();

	/**
	 * @return if can render
	 */
	public boolean isRenderer();

	/**
	 * @return if is sensor
	 */
	public boolean isSensor();

	/**
	 * @return if can answer to query
	 */
	public boolean canAnswer();
}
