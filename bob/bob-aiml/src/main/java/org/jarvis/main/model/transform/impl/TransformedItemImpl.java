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

package org.jarvis.main.model.transform.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.transform.ITransformedItem;

public class TransformedItemImpl implements ITransformedItem {

	private final List<List<String>>	elements	= new ArrayList<List<String>>();
	private List<String>				current		= null;

	@Override
	public void add(String s) {
		current.add(s);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public String get(int index) {
		StringBuilder sb = new StringBuilder();
		for (String e : elements.get(index)) {
			if (sb.length() == 0) sb.append(e);
			else sb.append(" " + e);
		}
		return sb.toString();
	}

	@Override
	public void add() {
		current = new ArrayList<String>();
		elements.add(current);
	}
}
