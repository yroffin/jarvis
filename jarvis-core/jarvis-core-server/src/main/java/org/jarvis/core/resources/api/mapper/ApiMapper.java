package org.jarvis.core.resources.api.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * api mapper
 */
public abstract class ApiMapper {
	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	protected void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
	}
}
