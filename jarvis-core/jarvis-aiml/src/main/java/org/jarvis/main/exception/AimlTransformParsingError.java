/**
 *  Copyright 2012 Yannick Roffin
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

package org.jarvis.main.exception;

import java.io.IOException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;

public class AimlTransformParsingError extends Exception {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 6352462504214710201L;

	public AimlTransformParsingError(IOException e) {
		super(e);
	}

	public AimlTransformParsingError(RecognitionException e) {
		super(e);
	}

	public AimlTransformParsingError(ParserRuleContext context) {
		super(context.toString());
	}

}
