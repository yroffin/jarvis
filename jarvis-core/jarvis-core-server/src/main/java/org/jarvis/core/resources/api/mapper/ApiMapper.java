package org.jarvis.core.resources.api.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * api mapper
 */
public abstract class ApiMapper {

	protected static final String IOT = ":iot";
	protected static final String COMMAND = ":command";
	protected static final String PLUGIN = ":plugin";
	protected static final String ID = ":id";
	protected static final String BLOCK = ":block";
	protected static final String PARAM = ":param";
	protected static final String INSTANCE = "instance";
	protected static final String HREF = "HREF";

	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	protected void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
	}
}
