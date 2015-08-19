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

public abstract class JarvisRestClientImpl {

	private String name;
	private boolean isRenderer;
	private boolean isSensor;
	private boolean canAnswer;

	public JarvisRestClientImpl(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRenderer() {
		return isRenderer;
	}

	public void setRenderer(boolean isRenderer) {
		this.isRenderer = isRenderer;
	}

	public boolean isSensor() {
		return isSensor;
	}

	public void setSensor(boolean isSensor) {
		this.isSensor = isSensor;
	}

	public boolean canAnswer() {
		return canAnswer;
	}

	public void setCanAnswer(boolean canAnswer) {
		this.canAnswer = canAnswer;
	}

}
