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

package org.jarvis.core.services.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessExec {

	/**
	 * command run
	 * 
	 * @param command
	 * @param pwd
	 * @param ee
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static CaptureResult exec(String command, String pwd,
			Map<String, String> ee) throws IOException, InterruptedException {
		List<String> commands = new ArrayList<String>();
		for (String cmd : command.split(" ")) {
			commands.add(cmd);
		}
		ProcessBuilder processus = new ProcessBuilder(commands);
		processus.redirectErrorStream(true);

		Map<String, String> env = processus.environment();
		for (String key : ee.keySet()) {
			env.put(key, ee.get(key));
		}
		processus.directory(new File(pwd));

		return new CaptureResult(processus.start());
	}

	/**
	 * simple exec
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static CaptureResult exec(String command) throws IOException,
			InterruptedException {
		File tmp = File.createTempFile("tmp", "tmp");
		String pwd = tmp.getParent();
		tmp.delete();
		return ProcessExec.exec(command, pwd, new HashMap<String, String>());
	}

	/**
	 * run command
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String execute(String command) throws IOException,
			InterruptedException {
		CaptureResult capture = ProcessExec.exec(command);
		return capture.getJson();
	}
}
