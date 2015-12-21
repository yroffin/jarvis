package org.jarvis.core.resources.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public abstract class ApiMapper {
	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
	}
}
