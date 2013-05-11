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

import org.jarvis.main.engine.AimlScoreTest;
import org.jarvis.main.model.transform.ITransformedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformedItemImpl implements ITransformedItem {

	private final Logger		logger		= LoggerFactory
													.getLogger(AimlScoreTest.class);
	private final List<String>	elements	= new ArrayList<String>();

	public TransformedItemImpl() {

	}

	public TransformedItemImpl(String initial) {
		for (String e : initial.split(" ")) {
			elements.add(e);
		}
	}

	@Override
	public void add(String s) {
		elements.add(s);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public String get(int index) {
		return elements.get(index);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String e : elements) {
			if (sb.length() == 0) sb.append(e);
			else sb.append(" " + e);
		}
		return sb.toString();
	}

	/**
	 * intersect
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	private List<Integer> intersection(List<String> list1, List<String> list2) {
		List<Integer> list = new ArrayList<Integer>();

		for (String t : list1) {
			if (list2.contains(t)) {
				list.add(list2.indexOf(t));
			} else {
				list.add(-1);
			}
		}

		return list;
	}

	@Override
	public List<String> getElements() {
		return elements;
	}

	@Override
	public int score(ITransformedItem compare) {
		/**
		 * empty input are ignored
		 */
		if (size() == 0) return -1;

		/**
		 * build intersection between the two lists
		 */
		List<Integer> list = intersection(elements, compare.getElements());
		if (list.size() == 0) return -1;

		int score = 0;

		/**
		 * now in compare list ... each unmatched items must be a STAR
		 */
		int index = 0;
		for (; index < size(); index++) {
			/**
			 * find first matched element
			 */
			for (; index < size() && list.get(index) >= 0; score++, index++)
				;
			/**
			 * current must be a STAR
			 */
			if (index < size()
					&& "*".compareTo(compare.getElements().get(index)) != 0) return -1;
			score++;
			for (; index < size() && list.get(index) < 0; index++)
				;
			score++;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Pattern: " + compare + " Score: " + score);
		}
		return score;
	}

	@Override
	public StringBuilder star(ITransformedItem compare, StringBuilder sb) {
		/**
		 * empty input are ignored
		 */
		if (size() == 0) return null;

		/**
		 * build intersection between the two lists
		 */
		List<Integer> list = intersection(elements, compare.getElements());
		if (list.size() == 0) return null;

		int index = 0;
		for (int star : list) {
			if (star == -1) {
				if (sb.length() > 0) sb.append(" ");
				sb.append(elements.get(index).toLowerCase());
			}
			index++;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Input:" + elements);
			logger.debug("Pattern: " + compare + " Star: " + sb.toString());
		}
		return sb;
	}
}
