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

package org.jarvis.core.services.neo4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.type.CommandType;
import org.jarvis.core.type.ParamType;
import org.jarvis.neo4j.client.Entities;
import org.jarvis.neo4j.client.Node;
import org.jarvis.neo4j.client.Transaction;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 *  Neo4j wrapper
 * @param <T> 
 */
public class Neo4jService<T> {
	protected Logger logger = LoggerFactory.getLogger(Neo4jService.class);

	/**
	 * Neo4j service
	 */
	ApiNeo4Service apiNeo4Service;

	protected ObjectMapper mapper = new ObjectMapper();
	protected MapperFactory mapperFactory = null;

	protected Neo4jService() {
		mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).build();
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}

	/**
	 * @param apiNeo4Service
	 */
	public void setApiNeo4Service(ApiNeo4Service apiNeo4Service) {
		this.apiNeo4Service = apiNeo4Service;
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
		return ParamType.STRING;
	}

	/**
	 * constructor
	 * @param source
	 * @return Node
	 */
	private Node toNode(Node node, T source) {
		ReflectionUtils.doWithFields(source.getClass(), new FieldCallback() {
			@Override
			public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
				field.setAccessible(true);
				Object value = field.get(source);
				if (value != null) {
					switch(getType(field)) {
						case DATETIME:
							DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
							String str = fmt.print(((org.joda.time.DateTime) value));
							node.setProperty(field.getName(), str);
							break;
						case STRING:
						case INT:
						case FLOAT:
							node.setProperty(field.getName(), value);
							break;
						case PARAM:
							node.setProperty(field.getName(), ((ParamType) value).name());
							break;
						case COMMAND:
							node.setProperty(field.getName(), ((CommandType) value).name());
							break;
					default:
						break;
					}
				} else {
					node.removeProperty(field.getName());
				}
			}
		});
		/**
		 * fix id
		 */
		((GenericBean) source).id = node.getId()+"";
		return node;
	}

	/**
	 * constructor
	 * @param source
	 * @return Node
	 */
	private Node toNode(T source) {
		Node node = apiNeo4Service.createNode(source.getClass().getSimpleName());
		return toNode(node, source);
	}

	/**
	 * create new resource
	 * @param source
	 * @return
	 */
	protected T create(T source) {
		try (Transaction tx = apiNeo4Service.beginTx()) {
			toNode(source);
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
	public List<T> findAll(Class<T> klass) {
		String classname = klass.getSimpleName();
		List<T> resultset = new ArrayList<T>();
		/**
		 * cypher query
		 */
		try (Transaction ignored = apiNeo4Service.beginTx();
				Entities result = apiNeo4Service.matchIdWithEntity("MATCH (node:"+classname+") RETURN id(node), node", "node", null)) {
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
	public T getById(Class<T> klass, String id) throws TechnicalNotFoundException {
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
	 * update entity
	 * @param klass
	 * @param instance
	 * @param id
	 * @return T
	 * @throws TechnicalNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public T update(Class<T> klass, T instance, String id) throws TechnicalNotFoundException {
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
	public T remove(Class<T> klass, String id) throws TechnicalNotFoundException {
		String classname = klass.getSimpleName();
		/**
		 * cypher query
		 */
		try (Transaction delete = apiNeo4Service.beginTx();
				Entities result = apiNeo4Service.matchIdWithEntity("MATCH (node:"+classname+") WHERE id(node) = "+id+" RETURN id(node),node", "node", null)) {
			if (result.hasNext()) {
				/**
				 * delete node
				 */
				T deleted = instance(klass, result.next(), "node");
				Entities deleteNode = apiNeo4Service.matchIdWithEntity("MATCH (node:"+classname+") WHERE id(node) = "+id+" DETACH DELETE node RETURN id(node), node", "node", null);
				if(deleteNode.hasNext()) {
					throw new TechnicalNotFoundException(id);
				}
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
	private T instance(Class<T> klass, Map<String, Object> row, String label) throws InstantiationException, IllegalAccessException {
		/**
		 * create new instance
		 */
		T target = klass.newInstance();

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
