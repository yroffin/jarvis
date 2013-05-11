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

package org.jarvis.main.model.transform;

import java.util.List;

public interface ITransformedItem {

	public void add(String upperCase);

	public int size();

	public String get(int index);

	public List<String> getElements();

	/**
	 * compute scoring
	 * 
	 * @param compare
	 * @return
	 */
	public int score(ITransformedItem compare);

	/**
	 * build star value
	 * 
	 * @param compare
	 * @param sb
	 * @return
	 */
	public StringBuilder star(ITransformedItem compare, StringBuilder sb);

}
