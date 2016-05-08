package org.jarvis.main.engine;

import java.util.List;

import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlResult;

/**
 * core tx
 */
public interface IAimlCoreTransaction {

	/**
	 * put a local message to this transaction
	 * 
	 * @param message
	 */
	void message(String message);

	/**
	 * retrieve messages
	 * 
	 * @return List<String>
	 */
	List<String> getMessages();

	/**
	 * push this category in current transaction
	 * 
	 * @param category
	 */
	void push(IAimlCategory category);

	/**
	 * close current category
	 * 
	 * @param result
	 */
	void pop(IAimlResult result);

	/**
	 * retrieve categories
	 * 
	 * @return List<ICategoryStack>
	 */
	List<ICategoryStack> getCategories();
}
