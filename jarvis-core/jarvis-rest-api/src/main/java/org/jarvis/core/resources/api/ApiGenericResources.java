package org.jarvis.core.resources.api;

import org.common.core.exception.TechnicalException;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.common.core.type.GenericMap;

/**
 * default api resources
 *
 */
public abstract class ApiGenericResources extends ApiMapper {

	/**
	 * execute real task on all resources, all task must be overridden in each
	 * resources
	 * 
	 * @param args
	 * @param taskType
	 * @return String
	 * @throws TechnicalException 
	 */
	public abstract GenericValue doRealTask(GenericMap args, String taskType);

}
