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

package org.jarvis.main.core.main;

import org.jarvis.config.GlobalConfig;

public class JarvisClient {
	/**
	 * standard main procedure
	 * 
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String argv[]) throws Exception {
		/**
		 * get instance properties
		 */
		GlobalConfig globalProperties = GlobalConfig.instance();

		/**
		 * aiml renderer
		 */
		Thread jarvisAimlEngine = new Thread(new JarvisAimlEngine(
				"jarvis-aiml-engine", globalProperties.getJarvisHost(),
				globalProperties.getJarvisLsnPort()));
		/**
		 * remote exec
		 */
		Thread jarvisRemoteExec = new Thread(new JarvisRemoteExec(
				"jarvis-remote-engine", globalProperties.getJarvisHost(),
				globalProperties.getJarvisLsnPort()));
		/**
		 * voice only renderer
		 */
		Thread jarvisVoiceEngine = new Thread(new JarvisVoiceEngine(
				"jarvis-voice-engine", globalProperties.getJarvisHost(),
				globalProperties.getJarvisLsnPort()));
		jarvisAimlEngine.start();
		jarvisRemoteExec.start();
		jarvisVoiceEngine.start();
		jarvisAimlEngine.join();
		jarvisRemoteExec.join();
		jarvisVoiceEngine.join();
	}
}
