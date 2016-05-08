package org.jarvis.main.engine.transform;

import org.jarvis.main.model.parser.IAimlCategory;

/**
 * evaluate score
 */
public interface IAimlScore extends Comparable<IAimlScore> {
	/**
	 * the score value
	 * @return int
	 */
	public int getKey();
	/**
	 * associated category
	 * @return IAimlCategory
	 */
	public IAimlCategory getValue();
}
