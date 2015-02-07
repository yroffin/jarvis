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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JarvisDatagramExec {
	@JsonProperty("result")
	public int result;

	@JsonProperty("stdout")
	public List<String> stdout;

	@JsonProperty("stderr")
	public List<String> stderr;

	@Override
	public String toString() {
		return "JarvisDatagramExec [result=" + result + ", stdout=" + stdout
				+ ", stderr=" + stderr + "]";
	}
}
