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

public class JarvisDatagram {
	@JsonProperty("code")
	String code;

	@JsonProperty("welcome")
	public JarvisDatagramEvent welcome;

	@JsonProperty("bye")
	public JarvisDatagramEvent bye;

	@JsonProperty("ack")
	public JarvisDatagramEvent ack;

	@JsonProperty("request")
	public JarvisDatagramEvent request;

	@JsonProperty("event")
	public JarvisDatagramEvent event;

	/**
	 * for internal use
	 */
	@JsonProperty("list")
	JarvisDatagramList list;

	@JsonProperty("session")
	public JarvisDatagramSession session;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "JarvisDatagram [code=" + code + ", welcome=" + welcome
				+ ", bye=" + bye + ", ack=" + ack + ", request=" + request
				+ ", event=" + event + ", list=" + list + ", session="
				+ session + "]";
	}
}
