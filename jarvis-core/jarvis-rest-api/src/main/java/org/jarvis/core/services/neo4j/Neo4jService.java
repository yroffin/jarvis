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

package org.jarvis.core.services.neo4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalHttpException;
import org.common.core.exception.TechnicalNotFoundException;
import org.common.neo4j.client.Entities;
import org.common.neo4j.client.Node;
import org.common.neo4j.client.Transaction;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.type.CommandType;
import org.jarvis.core.type.NotificationType;
import org.jarvis.core.type.ParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 *  Neo4j wrapper
 * @param <S> 
 */
public class Neo4jService<S extends GenericBean> {
	protected Logger logger = LoggerFactory.getLogger(Neo4jService.class);

	/**
	 * Neo4j service
	 */
	ApiNeo4Service apiNeo4Service;

	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	protected Neo4jService(ApiNeo4Service apiNeo4Service) {
		this.apiNeo4Service = apiNeo4Service;
		mapperFactory = new DefaultMapperFactory.Builder()
				.mapNulls(false)
				.build();
				
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		
		/**
		 * custom mapper for CommandType
		 */
		mapperFactory
			.classMap(Object.class, CommandType.class)
			.byDefault()
			.customize(
				   new CustomMapper<Object, CommandType>() {
					   @Override
					   public void mapAtoB(Object a, CommandType b, MappingContext context) {
					         b = CommandType.valueOf((String) a);
					   }
			       }
			)
			.register();
		
		/**
		 * custom mapper for NotificationType
		 */
		mapperFactory
			.classMap(Object.class, NotificationType.class)
			.byDefault()
			.customize(
					   new CustomMapper<Object, NotificationType>() {
						   @Override
						   public void mapAtoB(Object a, NotificationType b, MappingContext context) {
						         b = NotificationType.valueOf((String) a);
						   }
				       }
				)
			.register();
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}

	/**
	 * tranform type
	 * @param field
	 * @return
	 */
	private ParamType getType(Field field) {
		if(field.getType() == org.joda.time.DateTime.class) {
			return ParamType.DATETIME;
		}
		if(field.getType() == java.lang.String.class) {
			return ParamType.STRING;
		}
		if(field.getType() == org.jarvis.core.type.ParamType.class) {
			return ParamType.PARAM;
		}
		if(field.getType() == org.jarvis.core.type.CommandType.class) {
			return ParamType.COMMAND;
		}
		if(field.getType() == org.jarvis.core.type.NotificationType.class) {
			return ParamType.NOTIFICATION;
		}
		return ParamType.STRING;
	}

	/**
	 * constructor
	 * @param source
	 * @return Node
	 * @throws TechnicalHttpException 
	 * @throws TechnicalNotFoundException 
	 */
	private Node toNode(S source) throws TechnicalHttpException, TechnicalNotFoundException {
		@SuppressWarnings("unchecked")
		Map<String, Object> target = (Map<String, Object>) mapperFactory.getMapperFacade().map(source, Map.class);
		Node toCreate = new Node(target);
		return apiNeo4Service.createNode(source.getClass().getSimpleName(), toCreate);
	}

	/**
	 * create new resource
	 * @param source
	 * @return
	 */
	protected S create(S source) {
		try (Transaction tx = apiNeo4Service.beginTx()) {
			Node node = toNode(source);
			source.id = node.getId(); 
			tx.success();
		} catch (Exception e) {
			throw new TechnicalException(e);
		}
		return source;
	}

	/**
	 * find all node by label
	 * @param klass
	 * @return List<T>
	 */
	public List<S> findAll(Class<S> klass) {
		String classname = klass.getSimpleName();
		List<S> resultset = new ArrayList<S>();
		/**
		 * cypher query
		 */
		try (Transaction ignored = apiNeo4Service.beginTx();
				Entities result = apiNeo4Service.matchIdWithEntity("MATCH (node:"+classname+") RETURN id(node), node", "node", null, true)) {
			while (result.hasNext()) {
				resultset.add(instance(klass, result.next(), "node"));
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
		return resultset;
	}

	/**
	 * find all node by attribute value
	 * @param klass
	 * @param field 
	 * @param value 
	 * @return List<T>
	 */
	public List<S> findByAttribute(Class<S> klass, String field, String value) {
		String classname = klass.getSimpleName();
		List<S> resultset = new ArrayList<S>();
		/**
		 * cypher query
		 */
		try (Transaction ignored = apiNeo4Service.beginTx();
				Entities result = apiNeo4Service.matchIdWithEntity("MATCH (node:"+classname+") WHERE node."+field+" = '"+value+"' RETURN id(node), node", "node", null, true)) {
			while (result.hasNext()) {
				resultset.add(instance(klass, result.next(), "node"));
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
		return resultset;
	}

	/**
	 * get by id
	 * @param klass
	 * @param id
	 * @return T
	 * @throws TechnicalNotFoundException
	 */
	public S getById(Class<S> klass, String id) throws TechnicalNotFoundException {
		/**
		 * cypher query
		 */
		try (Transaction ignored = apiNeo4Service.beginTx();
				Entities result = apiNeo4Service.cypherOne(klass.getSimpleName(), id, "node")) {
			if (result.hasNext()) {
				/**
				 * iterate on nodes
				 */
				return instance(klass, result.next(), "node");
			} else {
				throw new TechnicalNotFoundException(id);
			}
		} catch (InstantiationException e) {
			logger.error("Instanciation Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Illegal Access Exception", e);
			throw new TechnicalException(e);
		} catch (TechnicalNotFoundException e) {
			logger.error("Not found Exception", e);
			throw e;
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * update entity
	 * @param klass
	 * @param instance
	 * @param id
	 * @return T
	 * @throws TechnicalNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public S update(Class<S> klass, S instance, String id) throws TechnicalNotFoundException {
		/**
		 * cypher query
		 */
		try (Transaction tx = apiNeo4Service.beginTx()) {
			Node node = new Node(mapperFactory.getMapperFacade().map(instance, Map.class));
			node.setId(id);
			apiNeo4Service.updateNode(node);
			tx.success();
			return instance;
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * remove node
	 * @param klass
	 * @param id
	 * @return T
	 * @throws TechnicalNotFoundException
	 */
	public S remove(Class<S> klass, String id) throws TechnicalNotFoundException {
		String classname = klass.getSimpleName();
		/**
		 * cypher query
		 */
		try (Transaction delete = apiNeo4Service.beginTx();
				Entities result = apiNeo4Service.matchIdWithEntity("MATCH (node:"+classname+") WHERE id(node) = "+id+" RETURN id(node),node", "node", null, true)) {
			if (result.hasNext()) {
				/**
				 * delete node
				 */
				S deleted = instance(klass, result.next(), "node");
				apiNeo4Service.execute("MATCH (node:"+classname+") WHERE id(node) = "+id+" DETACH DELETE node", true);
				delete.success();
				return deleted;
			} else {
				throw new TechnicalNotFoundException(id);
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * build instance with node
	 * @param klass
	 * @param row
	 * @param label 
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private S instance(Class<S> klass, Map<String, Object> row, String label) throws InstantiationException, IllegalAccessException {
		/**
		 * create new instance
		 */
		S target = klass.newInstance();

		for (Entry<String, Object> column : row.entrySet()) {
			if(!column.getKey().equals(label)) continue;
			Node node = (Node) column.getValue();
			Map<String, Object> maps = node.getAllProperties();
			/**
			 * iterate on fields to set values
			 */
			ReflectionUtils.doWithFields(klass, new FieldCallback() {
				@Override
				public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
					field.setAccessible(true);
					if(maps.containsKey(field.getName())) {
						switch(getType(field)) {
						case DATETIME:
							field.set(target, new org.joda.time.DateTime(node.getProperty(field.getName())));
							break;
						case STRING:
						case INT:
						case FLOAT:
							field.set(target, node.getProperty(field.getName()));
							break;
						case PARAM:
							field.set(target, ParamType.valueOf(node.getProperty(field.getName()).toString()));
							break;
						case COMMAND:
							field.set(target, CommandType.valueOf(node.getProperty(field.getName()).toString().toUpperCase()));
							break;
						case NOTIFICATION:
							field.set(target, NotificationType.valueOf(node.getProperty(field.getName()).toString().toUpperCase()));
							break;
						default:
							break;
						}
					}
				}
			});
			((GenericBean) target).id = node.getId()+"";
		}
		
		return target;
	}
}
