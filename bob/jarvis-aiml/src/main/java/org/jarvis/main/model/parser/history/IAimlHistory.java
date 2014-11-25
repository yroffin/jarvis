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
package org.jarvis.main.model.parser.history;

import org.jarvis.main.model.transform.ITransformedItem;

public interface IAimlHistory {

	public void setInput(String input);

	public String getInput();

	public void setAnswer(String answer);

	public void setThink(String think);

	public void setScript(String script);

	public void setJavascript(String javascript);

	public String getAnswer();

	public String getThink();

	public String getScript();

	public String getJavascript();

	public void setTransformedAnswer(ITransformedItem answer);

	public ITransformedItem getTransformedAnswer();

	public void setTransformedPattern(ITransformedItem pattern);

	public ITransformedItem getTransformedPattern();

}
