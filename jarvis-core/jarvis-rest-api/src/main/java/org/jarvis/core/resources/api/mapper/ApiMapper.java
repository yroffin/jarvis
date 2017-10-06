package org.jarvis.core.resources.api.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * api mapper
 */
public abstract class ApiMapper {

	/**
	 * plugins script
	 */
	public static final String SCRIPT_RESOURCE = "plugins/scripts";
	/**
	 * commands
	 */
	public static final String COMMAND_RESOURCE = "commands";
	/**
	 * processes
	 */
	public static final String PROCESS_RESOURCE = "processes";
	/**
	 * views
	 */
	public static final String VIEW_RESOURCE = "views";
	/**
	 * configurations
	 */
	public static final String CONFIG_RESOURCE = "configurations";
	/**
	 * properties
	 */
	public static final String PROPERTY_RESOURCE = "properties";
	/**
	 * connectors
	 */
	public static final String CONNECTOR_RESOURCE = "connectors";
	/**
	 * datasources
	 */
	public static final String DATASOURCE_RESOURCE = "datasources";
	/**
	 * measures
	 */
	public static final String MEASURE_RESOURCE = "measures";
	/**
	 * devices
	 */
	public static final String DEVICE_RESOURCE = "devices";
	/**
	 * events
	 */
	public static final String EVENT_RESOURCE = "events";
	/**
	 * triggers
	 */
	public static final String TRIGGER_RESOURCE = "triggers";
	/**
	 * snapshots
	 */
	public static final String SNAPSHOT_RESOURCE = "snapshots";
	/**
	 * crons
	 */
	public static final String CRON_RESOURCE = "crons";
	/**
	 * notifications
	 */
	public static final String NOTIFICATION_RESOURCE = "notifications";

	protected static final String DEVICE = ":device";
	protected static final String CONNEXION = ":connexion";
	protected static final String COMMAND = ":command";
	protected static final String NOTIFICATION = ":notification";
	protected static final String PLUGIN = ":plugin";
	protected static final String TRIGGER = ":trigger";
	protected static final String CRON = ":cron";
	protected static final String CONNECTOR = ":connector";
	protected static final String MEASURE = ":measure";
	protected static final String ID = ":id";
	protected static final String BLOCK = ":block";
	protected static final String PROCESS = ":process";
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
	protected MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

	protected void init() {
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.registerModule(new JodaModule());
	}
}
