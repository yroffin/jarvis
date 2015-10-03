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

package org.jarvis.rest.services.impl;

import org.jarvis.rest.services.JarvisConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource("classpath:connector.properties")
public abstract class JarvisRestClientImpl implements JarvisConnector {

	private String id;
	private String name;
	private boolean isRenderer;
	private boolean isSensor;
	private boolean canAnswer;

	@Autowired
	protected Environment env;

	public void init(String id, String name) {
		this.setId(id);
		this.setName(name);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isRenderer() {
		return isRenderer;
	}

	public void setRenderer(boolean isRenderer) {
		this.isRenderer = isRenderer;
	}

	@Override
	public boolean isSensor() {
		return isSensor;
	}

	public void setSensor(boolean isSensor) {
		this.isSensor = isSensor;
	}

	@Override
	public boolean canAnswer() {
		return canAnswer;
	}

	public void setCanAnswer(boolean canAnswer) {
		this.canAnswer = canAnswer;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
