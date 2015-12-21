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
import org.jarvis.core.type.ParamType;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 *  Neo4j wrapper
 */
public class Neo4jService<T> {
	protected Logger logger = LoggerFactory.getLogger(Neo4jService.class);

	/**
	 * Neo4j service
	 */
	ApiNeo4Service apiNeo4Service;

	public void setApiNeo4Service(ApiNeo4Service apiNeo4Service) {
		this.apiNeo4Service = apiNeo4Service;
	}

	/**
	 * tranform type
	 * @param field
	 * @return
	 */
	private ParamType getType(Field field) {
		if(field.getType() == java.lang.String.class) {
			return ParamType.STRING;
		}
		if(field.getType() == org.jarvis.core.type.ParamType.class) {
			return ParamType.TYPE;
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
						case STRING:
						case INT:
						case FLOAT:
							node.setProperty(field.getName(), value);
							break;
						case TYPE:
							node.setProperty(field.getName(), ((ParamType) value).name());
							break;
					default:
						break;
					}
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
		Label label = DynamicLabel.label(source.getClass().getSimpleName());
		Node node = apiNeo4Service.createNode(label);
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
		}
		return source;
	}

	/**
	 * find all node by label
	 * @param klass
	 * @return
	 */
	public List<T> findAll(Class<T> klass) {
		String classname = klass.getSimpleName();
		List<T> resultset = new ArrayList<T>();
		/**
		 * cypher query
		 */
		try (Transaction ignored = apiNeo4Service.beginTx();
				Result result = apiNeo4Service.execute("MATCH (node:"+classname+") RETURN node")) {
			while (result.hasNext()) {
				resultset.add(instance(klass, result.next()));
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
		return resultset;
	}

	/**
	 * get by id
	 * @param klass
	 * @param id
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	public T getById(Class<T> klass, String id) throws TechnicalNotFoundException {
		/**
		 * cypher query
		 */
		try (Transaction ignored = apiNeo4Service.beginTx();
				Result result = apiNeo4Service.cypherOne(klass.getSimpleName(), id)) {
			if (result.hasNext()) {
				/**
				 * iterate on nodes
				 */
				return instance(klass, result.next());
			} else {
				throw new TechnicalNotFoundException();
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * update entity
	 * @param klass
	 * @param instance
	 * @param id
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	public T update(Class<T> klass, T instance, String id) throws TechnicalNotFoundException {
		String classname = klass.getSimpleName();
		/**
		 * cypher query
		 */
		try (Transaction tx = apiNeo4Service.beginTx();
				Result result = apiNeo4Service.execute("MATCH (node:"+classname+") WHERE id(node) = "+id+" RETURN node")) {
			if (result.hasNext()) {
				/**
				 * iterate on nodes
				 */
				update(klass, instance, result.next());
				tx.success();
				return instance;
			} else {
				throw new TechnicalNotFoundException();
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * remove node
	 * @param klass
	 * @param id
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	public T remove(Class<T> klass, String id) throws TechnicalNotFoundException {
		String classname = klass.getSimpleName();
		/**
		 * cypher query
		 */
		try (Transaction delete = apiNeo4Service.beginTx();
				Result result = apiNeo4Service.execute("MATCH (node:"+classname+") WHERE id(node) = "+id+" RETURN node")) {
			if (result.hasNext()) {
				/**
				 * delete node
				 */
				T deleted = instance(klass, result.next());
				Result deleteNode = apiNeo4Service.execute("MATCH (node:"+classname+") WHERE id(node) = "+id+" DETACH DELETE node");
				if(deleteNode.hasNext()) {
					throw new TechnicalNotFoundException();
				}
				delete.success();
				return deleted;
			} else {
				throw new TechnicalNotFoundException();
			}
		} catch (InstantiationException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		} catch (IllegalAccessException e) {
			logger.error("Exception", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * build instance with node
	 * @param klass
	 * @param row
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private T instance(Class<T> klass, Map<String, Object> row) throws InstantiationException, IllegalAccessException {
		/**
		 * create new instance
		 */
		T target = klass.newInstance();

		for (Entry<String, Object> column : row.entrySet()) {
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
						case STRING:
						case INT:
						case FLOAT:
							field.set(target, node.getProperty(field.getName()));
							break;
						case TYPE:
							field.set(target, ParamType.valueOf(node.getProperty(field.getName()).toString()));
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

	/**
	 * build instance with node
	 * @param klass
	 * @param row
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private T update(Class<T> klass, T source, Map<String, Object> row) throws InstantiationException, IllegalAccessException {
		for (Entry<String, Object> column : row.entrySet()) {
			Node node = (Node) column.getValue();
			toNode(node, source);
		}
		return source;
	}
}
