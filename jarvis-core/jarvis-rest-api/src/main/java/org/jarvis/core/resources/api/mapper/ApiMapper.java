package org.jarvis.core.resources.api.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
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
	protected static final String CONNECTOR_RESOURCE = "connectors";
	protected static final String IOT_RESOURCE = "iots";
	protected static final String EVENT_RESOURCE = "events";
	protected static final String TRIGGER_RESOURCE = "triggers";
	protected static final String SNAPSHOT_RESOURCE = "snapshots";
	protected static final String CRON_RESOURCE = "crons";

	protected static final String IOT = ":iot";
	protected static final String CONNEXION = ":connexion";
	protected static final String COMMAND = ":command";
	protected static final String PLUGIN = ":plugin";
	protected static final String TRIGGER = ":trigger";
	protected static final String ID = ":id";
	protected static final String BLOCK = ":block";
	protected static final String PARAM = ":param";
	protected static final String ASYNC = ":async";
	
	protected static final String INSTANCE = "instance";
	protected static final String HREF = "HREF";
	protected static final String HREF_IF = "HREF_IF";
	protected static final String HREF_THEN = "HREF_THEN";
	protected static final String HREF_ELSE = "HREF_ELSE";
	protected static final String SORTKEY = "order";
	protected static final String TASK = "task";

	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	protected void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}

	/**
	 * mount resource
	 */
	public abstract void mount();
}
