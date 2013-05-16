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

package org.jarvis.main.model.impl.transform;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.transform.ITransformedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformedItemImpl implements ITransformedItem {

	private static final String	STAR		= "*";
	private static final String	UNDERSCORE	= "_";

	private final Logger		logger		= LoggerFactory
													.getLogger(TransformedItemImpl.class);
	private final List<String>	elements	= new ArrayList<String>();
	private final List<String>	raws		= new ArrayList<String>();
	private static boolean		debugScore	= false;

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
	public void addRaw(String value) {
		raws.add(value);
	}

	@Override
	public String getRaw() {
		StringBuilder sb = new StringBuilder();
		for (String value : raws) {
			if (sb.length() > 1) sb.append(" ");
			sb.append(value);
		}
		return sb.toString();
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
	private List<Integer> minus(List<String> left, List<String> right) {
		List<Integer> list = new ArrayList<Integer>();

		int star = 0;
		for (String t : left) {
			if (right.contains(t)) {
				list.add(left.indexOf(t));
				star = 0;
			} else {
				star++;
				if (star == 1) list.add(-1);
			}
		}

		return list;
	}

	/**
	 * intersect
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	private List<Integer> intersection(List<String> left, List<String> right) {
		List<Integer> list = new ArrayList<Integer>();

		int star = 0;
		for (String t : left) {
			if (right.contains(t)) {
				list.add(left.indexOf(t));
				star = 0;
			} else {
				star++;
				list.add(-star);
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
		if (compare.getElements().size() == 0) return -1;

		/**
		 * build intersection between the two lists
		 */
		List<Integer> minus = minus(elements, compare.getElements());
		if (minus.size() == 0) return -1;
		if (compare.getElements().size() != minus.size()) return -1;

		int score = 0;

		/**
		 * now in compare list ... each unmatched items must be a STAR
		 */
		List<String> compared = compare.getElements();

		if (logger.isDebugEnabled() && debugScore) {
			logger.debug("Elements: " + elements);
			logger.debug("Compared: " + compared);
			logger.debug("Intersect: " + minus);
		}

		for (int ii = 0; ii < minus.size(); ii++) {
			if (minus.get(ii) < 0) {
				/**
				 * compared must be STAR or UNDERSCORE
				 */
				if (!(compared.get(ii).compareTo(STAR) == 0 || compared.get(ii)
						.compareTo(UNDERSCORE) == 0)) return -1;
				score++;
			} else {
				/**
				 * must be equal
				 */
				if (compared.get(ii).compareTo(get(minus.get(ii))) != 0) return -1;
				score++;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Elements: " + elements + " Pattern: " + compared + " Score: " + score);
		}
		return score;
	}

	@Override
	public List<String> star(ITransformedItem compare, List<String> sb) {
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
		StringBuilder current = new StringBuilder();
		for (int star : list) {
			if (star < 0) {
				if (current.length() > 0) current.append(" ");
				current.append(elements.get(index).toLowerCase());
			}
			if (index > 0 && star >= 0 && current.length() > 0) {
				/**
				 * flush
				 */
				sb.add(current.toString());
				current.setLength(0);
			}
			index++;
		}

		/**
		 * flush
		 */
		if (current.length() > 0) {
			sb.add(current.toString());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Input:" + elements);
			logger.debug("Pattern: " + compare + " Star/Underscore: "
					+ sb.toString());
		}
		return sb;
	}
}
