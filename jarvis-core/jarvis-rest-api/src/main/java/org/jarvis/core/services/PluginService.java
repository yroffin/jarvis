package org.jarvis.core.services;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.resources.api.plugins.PayloadBean;
import org.jarvis.core.type.GenericMap;

/**
 * default plugin service interface
 */
public abstract class PluginService {

	/**
	 * execute service and return object
	 * @param element
	 * @param args
	 * @return GenericMap
	 * @throws TechnicalException
	 */
	public GenericMap asObject(GenericMap element, GenericMap args) throws TechnicalException {
		return element;	
	}

	/**
	 * execute service and return object
	 * @param element
	 * @param args
	 * @return GenericMap
	 * @throws TechnicalException
	 */
	public GenericMap asObject(GenericMap element, PayloadBean args) throws TechnicalException {
		return element;	
	}

	/**
	 * execute service and return boolean
	 * @param command
	 * @param args
	 * @return boolean
	 * @throws TechnicalException
	 */
	public boolean asBoolean(GenericMap command, GenericMap args) throws TechnicalException {
		return false;
	}

}
