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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jarvis.core.AbstractJerseyClient;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.bean.iot.EventBean;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.iot.EventRest;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.type.GenericMap;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * main daemon
 */
@Component
public class CoreStatistics extends AbstractJerseyClient {
	
	protected static Logger logger = LoggerFactory.getLogger(CoreStatistics.class);
	protected MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
	
	@Autowired
	Environment env;

	/**
	 * start component
	 */
	@PostConstruct
	public void init() {
		/**
		 * initialize
		 */
		initialize(
				env.getProperty("jarvis.elasticsearch.url"),
				env.getProperty("jarvis.elasticsearch.user"),
				env.getProperty("jarvis.elasticsearch.password"),
				env.getProperty("jarvis.elasticsearch.timeout.connect","2"),
				env.getProperty("jarvis.elasticsearch.timeout.read","2"));
		
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
		} catch(Exception e) {
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
			write(statistics, d.getClass().getPackage().getName()+ "." + d.getClass().getSimpleName(), d.id);
		} catch(Exception e) {
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
	 * @param value
	 * @param index
	 * @param basename
	 */
	public void write(GenericMap value, String index, String basename) {
		/**
		 * null url abort statistic store
		 */
		if(baseurl == null) {
			logger.warn("[STATISTICS] no store for {}", value);
		}
		
		/**
		 * compute statistics
		 */
		value.put("timestamp", new DateTime());

		sendEntity(value,index,basename);
	}

	/**
	 * send entity
	 * @param value
	 * @param index
	 * @param basename
	 */
	private void sendEntity(Object value, String index, String basename) {
		/**
		 * build response
		 */
		Response entity;
		try {
			entity = client.target(baseurl)
			        .path("jarvis-" + index.toLowerCase() + "/" + basename)
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .post(Entity.entity(mapper.writeValueAsString(value),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		
		/**
		 * verify result
		 */
		if(entity.getStatus() != 201) {
			logger.warn("While sending stats {} {} {}", entity.getStatus(), index,  basename);
			try {
				logger.warn("Entity {}", entity.toString());
				logger.warn("Body {}", mapper.writeValueAsString(value));
				logger.warn("Response {}", entity.readEntity(String.class));
			} catch (JsonProcessingException e) {
				throw new TechnicalException(entity.getStatus() + ":" + index + "/" + value.getClass().getSimpleName());
			}
			throw new TechnicalException(entity.getStatus() + ":" + index + "/" + value.getClass().getSimpleName());
		}
	}

}
