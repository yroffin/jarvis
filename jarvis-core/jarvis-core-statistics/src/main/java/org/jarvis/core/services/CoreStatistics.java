/**
 *  Copyright 2015 Yannick Roffin
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

package org.jarvis.core.services;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.common.core.exception.TechnicalException;
import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.bean.device.EventBean;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.device.EventRest;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * main daemon
 */
@Component
public class CoreStatistics {

	protected static Logger logger = LoggerFactory.getLogger(CoreStatistics.class);
	protected MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
	protected ObjectMapper mapper = new ObjectMapper();

	MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection<Document> statistics;

	@Autowired
	Environment env;

	/**
	 * start component
	 */
	@PostConstruct
	public void init() {
		// mongodb connection
		this.mongo = new MongoClient(env.getProperty("jarvis.mongodb.host"), 27017);
		this.db = mongo.getDatabase("logger");
		this.statistics = db.getCollection("statistics");

		// Orika
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
	}

	/**
	 * @param bean
	 */
	public void write(EventBean bean) {
		try {
			EventRest d = mapperFactory.getMapperFacade().map(bean, EventRest.class);
			GenericMap statistics = new GenericMap();
			statistics.put("entity", d);
			write(statistics, d.getClass().getPackage().getName() + "." + d.getClass().getSimpleName(), d.id);
		} catch (Exception e) {
			/**
			 * ignore any statistics report
			 */
		}
	}

	/**
	 * @param bean
	 * @param result
	 * @param args
	 */
	public void write(CommandBean bean, GenericMap args, GenericMap result) {
		try {
			CommandRest d = mapperFactory.getMapperFacade().map(bean, CommandRest.class);
			GenericMap statistics = new GenericMap();
			statistics.put("entity", d.name);
			statistics.put("args", args);
			statistics.put("result", result);
			write(statistics, d.getClass().getPackage().getName() + "." + d.getClass().getSimpleName(), d.id);
		} catch (Exception e) {
			/**
			 * ignore any statistics report
			 */
		}
	}

	/**
	 * @param bean
	 */
	public void write(GenericBean bean) {
		GenericEntity d = mapperFactory.getMapperFacade().map(bean, GenericEntity.class);
		GenericMap statistics = new GenericMap();
		statistics.put("entity", d);
		write(statistics, d.getClass().getPackage().getName() + "." + d.getClass().getSimpleName(), d.id);
	}

	/**
	 * @param d
	 */
	public void write(GenericEntity d) {
		GenericMap statistics = new GenericMap();
		statistics.put("entity", d);
		write(statistics, d.getClass().getPackage().getName() + "." + d.getClass().getSimpleName(), d.id);
	}

	/**
	 * write this object to statistics
	 * 
	 * @param value
	 * @param index
	 * @param basename
	 */
	public void write(GenericMap value, String index, String basename) {
		/**
		 * compute statistics
		 */
		value.put("timestamp", new DateTime().toString());
		sendEntity(value, index, basename);
	}

	/**
	 * send entity
	 * @param value
	 * @param index
	 * @param basename
	 */
	private void sendEntity(Object value, String index, String basename) {
		Document parsed;
		try {
			parsed = Document.parse(mapper.writeValueAsString(value));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		statistics.insertOne(parsed);
	}

}
