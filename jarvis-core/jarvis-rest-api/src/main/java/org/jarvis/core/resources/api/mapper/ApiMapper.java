/**
 *  Copyright 2017 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
	 * models
	 */
	public static final String MODEL_RESOURCE = "models";
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
