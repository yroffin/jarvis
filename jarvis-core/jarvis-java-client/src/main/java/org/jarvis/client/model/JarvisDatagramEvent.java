/**
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jarvis.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JarvisDatagramEvent {
	@JsonProperty("data")
	private String data;

	@JsonProperty("script")
	private String script;

	@JsonProperty("from")
	private String from;

	@JsonProperty("target")
	private String target;

	public String getFrom() {
		return from;
	}

	public String getTarget() {
		return target;
	}

	public String getData() {
		return data;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "JarvisDatagramEvent [data=" + data + ", script=" + script
				+ ", from=" + from + ", target=" + target + "]";
	}
}
