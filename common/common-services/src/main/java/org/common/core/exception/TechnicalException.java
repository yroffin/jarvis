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

package org.common.core.exception;

/**
 * technical errors
 */
public class TechnicalException extends RuntimeException {

	/**
	 * @param e
	 */
	public TechnicalException(InstantiationException e) {
		super(e);
	}

	/**
	 * @param e
	 */
	public TechnicalException(IllegalAccessException e) {
		super(e);
	}

	/**
	 * @param e
	 */
	public TechnicalException(Exception e) {
		super(e);
		setStackTrace(e.getStackTrace());
	}

	/**
	 * @param message
	 */
	public TechnicalException(String message) {
		super(message);
	}

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 8605302097434223980L;

}
