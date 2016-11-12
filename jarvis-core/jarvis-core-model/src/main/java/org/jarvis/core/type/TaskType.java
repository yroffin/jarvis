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

package org.jarvis.core.type;

/**
 * Task type
 */
public enum TaskType {
	/**
	 * test
	 */
	TEST,
	/**
	 * execute
	 */
	EXECUTE,
	/**
	 * render
	 */
	RENDER,
	/**
	 * execute task activity
	 */
	ACTIVITY,
	/**
	 * execute task svg
	 */
	SVG,
	/**
	 * download
	 */
	DOWNLOAD,
	/**
	 * upload
	 */
	UPLOAD,
	/**
	 * restore
	 */
	RESTORE,
	/**
	 * register
	 */
	REGISTER,
	/**
	 * ping
	 */
	PING,
	/**
	 * toggle
	 */
	TOGGLE
}
