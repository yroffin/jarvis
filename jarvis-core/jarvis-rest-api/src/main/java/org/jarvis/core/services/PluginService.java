package org.jarvis.core.services;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.type.GenericMap;

/**
 * default plugin service interface
 */
public interface PluginService {

	/**
	 * execute service and return object
	 * @param command
	 * @param args
	 * @return GenericMap
	 * @throws TechnicalException
	 */
	GenericMap asObject(GenericMap command, GenericMap args) throws TechnicalException;

	/**
	 * execute service and return boolean
	 * @param command
	 * @param args
	 * @return boolean
	 * @throws TechnicalException
	 */
	boolean asBoolean(GenericMap command, GenericMap args) throws TechnicalException;

}
