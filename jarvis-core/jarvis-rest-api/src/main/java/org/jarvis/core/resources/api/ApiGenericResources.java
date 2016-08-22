package org.jarvis.core.resources.api;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;

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
	public abstract GenericValue doRealTask(GenericMap args, TaskType taskType);

}
