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
	 * Aiml engine
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Thread jarvisAimlEngine() throws Exception {
		/**
		 * get instance properties
		 */
		GlobalConfig globalProperties = GlobalConfig.instance();

		Thread jarvisAimlEngine = new Thread(new JarvisAimlEngine(
				"jarvis-aiml-engine-v1.0b", globalProperties.getJarvisHost(),
				globalProperties.getJarvisLsnPort(),
				globalProperties.getJarvisVoice()));
		return jarvisAimlEngine;
	}

	/**
	 * Remote exec
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Thread jarvisRemoteExec() throws Exception {
		/**
		 * get instance properties
		 */
		GlobalConfig globalProperties = GlobalConfig.instance();

		Thread jarvisRemoteExec = new Thread(new JarvisRemoteExec(
				"jarvis-remote-engine-v1.0b", globalProperties.getJarvisHost(),
				globalProperties.getJarvisLsnPort()));
		return jarvisRemoteExec;
	}

	/**
	 * voice engine
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Thread jarvisVoiceEngine() throws Exception {
		/**
		 * get instance properties
		 */
		GlobalConfig globalProperties = GlobalConfig.instance();

		Thread jarvisVoiceEngine = new Thread(new JarvisVoiceEngine(
				"jarvis-voice-engine-v1.0b", globalProperties.getJarvisHost(),
				globalProperties.getJarvisLsnPort()));
		return jarvisVoiceEngine;
	}

	/**
	 * standard main procedure
	 * 
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String argv[]) throws Exception {
		/**
		 * aiml renderer
		 */
		Thread jarvisAimlEngine = jarvisAimlEngine();
		/**
		 * remote exec
		 */
		Thread jarvisRemoteExec = jarvisRemoteExec();
		/**
		 * voice only renderer
		 */
		Thread jarvisVoiceEngine = jarvisVoiceEngine();

		jarvisAimlEngine.start();
		jarvisRemoteExec.start();
		jarvisVoiceEngine.start();
		jarvisAimlEngine.join();
		jarvisRemoteExec.join();
		jarvisVoiceEngine.join();
	}
}
