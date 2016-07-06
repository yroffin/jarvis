package org.jarvis.main.engine;

import org.jarvis.main.model.parser.IAimlResult;

/**
 * category
 */
public interface ICategoryStack {

	/**
	 * retrieve level
	 * @return int
	 */
	int getLevel();

	/**
	 * category
	 * @return String
	 */
	String getCategory();

	/**
	 * result
	 * @return IAimlResult
	 */
	IAimlResult getResult();

}
