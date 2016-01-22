package org.jarvis.core.resources.api.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * api mapper
 */
public abstract class ApiMapper {

	protected static final String SCRIPT_RESOURCE = "plugins/scripts";
	protected static final String COMMAND_RESOURCE = "commands";
	protected static final String SCENARIO_RESOURCE = "scenarios";
	protected static final String BLOCK_RESOURCE = "blocks";
	protected static final String VIEW_RESOURCE = "views";
	protected static final String IOT_RESOURCE = "iots";
	protected static final String EVENT_RESOURCE = "events";

	protected static final String IOT = ":iot";
	protected static final String COMMAND = ":command";
	protected static final String PLUGIN = ":plugin";
	protected static final String ID = ":id";
	protected static final String BLOCK = ":block";
	protected static final String PARAM = ":param";
	
	protected static final String INSTANCE = "instance";
	protected static final String HREF = "HREF";
	protected static final String SORTKEY = "order";
	protected static final String TASK = "task";

	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	protected void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
