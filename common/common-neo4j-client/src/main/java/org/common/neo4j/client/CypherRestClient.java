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

package org.common.neo4j.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalHttpException;
import org.common.core.exception.TechnicalNotFoundException;
import org.common.core.type.GenericMap;
import org.common.jersey.AbstractJerseyClient;
import org.common.neo4j.model.CypherError;
import org.common.neo4j.model.CypherResults;
import org.common.neo4j.model.CypherRow;
import org.common.neo4j.model.CypherRows;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * cypher query and rest API neo4j
 */
public class CypherRestClient extends AbstractJerseyClient {
	protected Logger logger = LoggerFactory.getLogger(CypherRestClient.class);

	/**
	 * constructor
	 * 
	 * @param baseurl
	 * @param user
	 * @param password
	 * @param connect 
	 * @param read 
	 */
	public CypherRestClient(String baseurl, String user, String password, String connect, String read) {
		/**
		 * initialize
		 */
		initialize(baseurl, user, password, connect, read);
	}

	/**
	 * default cypher low level call
	 * 
	 * @param statement
	 *            the cypher statement
	 * @param isNode
	 *            if we want to retrieve a node (else a relationship)
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> query(String statement, boolean isNode) {
		DateTime dt = DateTime.now();
		String entity = "";
		try {
			/**
			 * build call
			 */
			entity = client.target(baseurl).path("db/data/transaction/commit").request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).acceptEncoding("charset=UTF-8")
					.post(Entity.entity("{\"statements\":[{\"statement\":\"" + statement + "\"}]})",
							MediaType.APPLICATION_JSON), String.class);
		} catch (Exception e) {
			logger.error("While query exec on NEO4J: {} - {} ms", statement,
					DateTime.now().getMillis() - dt.getMillis());
			throw new TechnicalException(e);
		}

		/**
		 * decode result
		 */
		return decodeResult(entity, isNode);
	}

	/**
	 * decode cypher result
	 * 
	 * @param entity
	 *            the cypher result
	 * @param isNode
	 *            if we want to decode a node (or a relationship)
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> decodeResult(String entity, boolean isNode) {
		CypherResults cypherResults = null;
		try {
			cypherResults = mapper.readValue(entity, CypherResults.class);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (cypherResults.getResults().size() == 0) {
			for (CypherError error : cypherResults.getErrors()) {
				logger.error("Cypher error {}", error.getCode(), error.getMessage());
			}
			throw new TechnicalException("Cypher error");
		}

		/**
		 * iterate on data
		 */
		for (CypherRows element : cypherResults.getResults().get(0).getData()) {
			Map<String, Object> rows = new HashMap<String, Object>();
			int index = 0;
			for (CypherRow el : element.getRow()) {
				if (el.isInteger()) {
					rows.put(cypherResults.getResults().get(0).getColumns().get(index++), el.getInteger());
				}
				if (el.isObject()) {
					if (isNode) {
						rows.put(cypherResults.getResults().get(0).getColumns().get(index++),
								new Node((el.getObject())));
					} else {
						rows.put(cypherResults.getResults().get(0).getColumns().get(index++),
								new Relation((el.getObject())));
					}
				}
			}
			list.add(rows);
		}

		return list;
	}

	private static List<String> relations = new ArrayList<String>();
	private static List<String> resources = new ArrayList<String>();

	{
		/**
		 * relationship types
		 */
		relations.add("HREF");
		relations.add("HREF_IF");
		relations.add("HREF_THEN");
		relations.add("HREF_ELSE");

		/**
		 * resources
		 */
		resources.add("ProcessBean");
		resources.add("CommandBean");
		resources.add("EventBean");
		resources.add("DeviceBean");
		resources.add("ScriptPluginBean");
		resources.add("TriggerBean");
		resources.add("ViewBean");
		resources.add("ConnectorBean");
		resources.add("CronBean");
		resources.add("ConfigBean");
		resources.add("PropertyBean");
	}

	/**
	 * find all node
	 * 
	 * @return List<Node> all node and relation in database (exception snapshot
	 *         nodes)
	 * @throws TechnicalHttpException
	 */
	public Map<String, Map<String, GenericMap>> findAllNodes() throws TechnicalHttpException {

		Map<String, Map<String, GenericMap>> snapshots = new TreeMap<String, Map<String, GenericMap>>();

		/**
		 * dump all relation
		 */
		for (String relation : relations) {
			Map<String, GenericMap> beans = new TreeMap<String, GenericMap>();
			snapshots.put(relation, beans);
			Entities links = matchRelationEntity(relation, false);
			for (Map<String, Object> item : links.elements) {
				Relation rel = (Relation) item.get("r");
				GenericMap genericMap = new GenericMap();
				for (Entry<String, Object> field : rel.fields.entrySet()) {
					genericMap.put(field.getKey(), field.getValue());
				}
				genericMap.put("__from", item.get("id(n)"));
				genericMap.put("__to", item.get("id(m)"));
				beans.put(rel.getId(), genericMap);
			}
		}

		/**
		 * iterate on all resources
		 */
		for (String resource : resources) {
			Map<String, GenericMap> beans = new TreeMap<String, GenericMap>();
			snapshots.put(resource, beans);
			Entities result = matchIdWithEntity(
					"/* find entities */ MATCH (node:" + resource + ") return id(node),node", "node", true);
			for (Map<String, Object> item : result.elements) {
				Node node = (Node) item.get("node");
				GenericMap genericMap = new GenericMap();
				for (Entry<String, Object> field : node.fields.entrySet()) {
					genericMap.put(field.getKey(), field.getValue());
				}
				beans.put(node.getId(), genericMap);
			}
		}
		return snapshots;
	}

	/**
	 * restore from a json dump (stored as a GenericMap)
	 * 
	 * @param repository
	 *            a GenericMap storing all resources
	 */
	public void restore(GenericMap repository) {
		/**
		 * internal index for old id and new id
		 */
		Map<String, String> index = new HashMap<String, String>();

		/**
		 * recreate resources
		 */
		for (String resource : resources) {
			/**
			 * remove all resources (before replacement)
			 */
			execute("MATCH (node:" + resource + ") DETACH DELETE node", true);
			@SuppressWarnings("unchecked")
			Map<String, LinkedHashMap<String, Object>> elements = (Map<String, LinkedHashMap<String, Object>>) repository
					.get(resource);
			/**
			 * continue if no element
			 */
			if (elements == null) {
				continue;
			}
			for (Entry<String, LinkedHashMap<String, Object>> raw : elements.entrySet()) {
				LinkedHashMap<String, Object> node = raw.getValue();
				Node toCreate = new Node(node);
				try {
					Node created = createNode(resource, toCreate);
					index.put((String) raw.getKey(), created.getId());
					logger.info("Index {} => {}", (String) raw.getKey(), created.getId());
				} catch (TechnicalHttpException | TechnicalNotFoundException e) {
					throw new TechnicalException(e);
				}
			}
		}

		/**
		 * recreate relations
		 */
		for (String relation : relations) {
			@SuppressWarnings("unchecked")
			Map<String, LinkedHashMap<String, Object>> elements = (Map<String, LinkedHashMap<String, Object>>) repository
					.get(relation);
			for (Entry<String, LinkedHashMap<String, Object>> element : elements.entrySet()) {
				String from = index.get((String) (element.getValue().get("__from") + ""));
				String to = index.get((String) (element.getValue().get("__to") + ""));
				if (from != null && to != null) {
					Node properties = new Node();
					for (Entry<String, Object> field : element.getValue().entrySet()) {
						/**
						 * ignore fields starting with __
						 */
						if (field.getKey().startsWith("__"))
							continue;
						properties.setProperty(field.getKey(), field.getValue());
					}
					logger.info("Restore {} => {} with {}", from, to, properties.toString());
					try {
						createRelationship(from, to, relation, properties);
					} catch (TechnicalHttpException e) {
						throw new TechnicalException(e);
					}
				}
			}
		}
	}

	/**
	 * create a new node
	 * 
	 * @param toCreate
	 *            the node to create
	 * @return Node
	 * @throws TechnicalHttpException
	 */
	@SuppressWarnings("unchecked")
	public Node createNode(Node toCreate) throws TechnicalHttpException {
		DateTime dt = DateTime.now();
		Response entity;
		try {
			entity = client.target(baseurl).path("db/data/node").request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).acceptEncoding("charset=UTF-8").post(Entity.entity(
							mapper.writeValueAsString(toCreate.getAllProperties()), MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("While create node on NEO4J: {} - {} ms", toCreate.toString(),
					DateTime.now().getMillis() - dt.getMillis());
			throw new TechnicalException(e);
		}

		if (entity.getStatus() == 201) {
			Map<?, ?> body = null;
			try {
				body = mapper.readValue(entity.readEntity(String.class), Map.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}

			Node node = new Node(toCreate.getAllProperties());
			node.setId((String) (((Map<String, Object>) body.get("metadata")).get("id") + ""));

			return node;
		} else {
			throw new TechnicalHttpException(entity.getStatus(), "db/data/node");
		}
	}

	/**
	 * update an existing node
	 * 
	 * @param update
	 * @return Node
	 * @throws TechnicalNotFoundException
	 */
	public Node updateNode(Node update) throws TechnicalNotFoundException {
		DateTime dt = DateTime.now();
		Response entity;
		try {
			entity = client.target(baseurl).path("db/data/node/" + update.getId() + "/properties")
					.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.acceptEncoding("charset=UTF-8").put(Entity
							.entity(mapper.writeValueAsString(update.getAllProperties()), MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("While update node on NEO4J: {} - {} ms", update.toString(),
					DateTime.now().getMillis() - dt.getMillis());
			throw new TechnicalException(e);
		}

		if (entity.getStatus() == 204) {
			return update;
		} else {
			throw new TechnicalNotFoundException(update.getId());
		}
	}

	/**
	 * create a new node with its label
	 * 
	 * @param label
	 *            label for this node
	 * @param toCreate
	 *            node to create
	 * @return Node
	 * @throws TechnicalHttpException
	 * @throws TechnicalNotFoundException
	 */
	public Node createNode(String label, Node toCreate) throws TechnicalHttpException, TechnicalNotFoundException {
		/**
		 * create node
		 */
		Node node = createNode(toCreate);

		DateTime dt = DateTime.now();
		Response entity;
		try {
			entity = client.target(baseurl).path("db/data/node/" + node.id + "/labels")
					.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.acceptEncoding("charset=UTF-8")
					.post(Entity.entity("\"" + label + "\"", MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			logger.error("While create node with label {} on NEO4J: {} - {} ms", label, toCreate.toString(),
					DateTime.now().getMillis() - dt.getMillis());
			throw new TechnicalException(e);
		}

		if (entity.getStatus() == 204) {
			return node;
		} else {
			throw new TechnicalNotFoundException(node.getId());
		}
	}

	/**
	 * create a relationship
	 * 
	 * @param from
	 * @param to
	 * @param label
	 * @param properties
	 * @throws TechnicalHttpException
	 */
	public void createRelationship(String from, String to, String label, Node properties)
			throws TechnicalHttpException {
		/**
		 * payload
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("    \"to\":\"" + baseurl + "/db/data/node/" + to + "\",\n");
		sb.append("    \"type\":\"" + label + "\",\n");
		sb.append("    \"data\":\n");
		try {
			sb.append(mapper.writeValueAsString(properties.getAllProperties()) + "\n");
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		sb.append("\n");
		sb.append("}\n");

		/**
		 * call entity
		 */
		DateTime dt = DateTime.now();
		Response entity;
		try {
			entity = client.target(baseurl).path("db/data/node/" + from + "/relationships")
					.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.acceptEncoding("charset=UTF-8").post(Entity.entity(sb.toString(), MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			logger.error("While create link {}/{} - {} on NEO4J: {} - {} ms", from, to, label, properties.toString(),
					DateTime.now().getMillis() - dt.getMillis());
			throw new TechnicalException(e);
		}

		/**
		 * check status
		 */
		if (entity.getStatus() == 201) {
			return;
		} else {
			throw new TechnicalHttpException(entity.getStatus(), "db/data/node/" + from + "/relationships");
		}
	}

	/**
	 * update a relatioship
	 * 
	 * @param update
	 *            the relationship to update
	 * @return new node
	 * @throws TechnicalNotFoundException
	 */
	public Node updateRelationship(Node update) throws TechnicalNotFoundException {
		DateTime dt = DateTime.now();
		Response entity;
		try {
			entity = client.target(baseurl).path("db/data/relationship/" + update.getId() + "/properties")
					.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.acceptEncoding("charset=UTF-8").put(Entity
							.entity(mapper.writeValueAsString(update.getAllProperties()), MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		} catch (Exception e) {
			logger.error("While update link on NEO4J: {} - {} ms", update.toString(),
					DateTime.now().getMillis() - dt.getMillis());
			throw new TechnicalException(e);
		}

		if (entity.getStatus() == 204) {
			return update;
		} else {
			throw new TechnicalNotFoundException(update.getId());
		}
	}

	/**
	 * @return Transaction
	 */
	public Transaction beginTx() {
		return new Transaction();
	}

	/**
	 * execute a cypher query and result a result
	 * 
	 * @param query
	 *            the cypher wuery
	 * @param isNode
	 *            node result or relationship
	 * @return Result the result
	 */
	public Entities execute(String query, boolean isNode) {
		logger.trace(query);
		Entities result = new Entities();
		for (Map<String, Object> map : query(query, isNode)) {
			result.add(map);
		}
		return result;
	}

	/**
	 * result entities with id updated
	 * 
	 * @param query
	 * @param entity
	 * @param isNode
	 * @return Result
	 */
	public Entities matchIdWithEntity(String query, String entity, boolean isNode) {
		logger.trace(query);
		Entities result = new Entities();
		for (Map<String, Object> map : query(query, isNode)) {
			Node n = (Node) map.get(entity);
			n.setId((String) (map.get("id(" + entity + ")") + ""));
			result.add(map);
		}
		return result;
	}

	/**
	 * @param query
	 * @param first
	 * @param second
	 * @param isNode
	 * @return Result
	 */
	public Entities matchIdWithEntity(String query, String first, String second, boolean isNode) {
		logger.trace(query);
		Entities result = new Entities();
		for (Map<String, Object> map : query(query, isNode)) {
			Node n = (Node) map.get(first);
			n.setId((String) (map.get("id(" + first + ")") + ""));
			Node m = (Node) map.get(second);
			m.setId((String) (map.get("id(" + second + ")") + ""));
			result.add(map);
		}
		return result;
	}

	/**
	 * find all relation ship on entity
	 * 
	 * @param label
	 *            entity label
	 * @param isNode
	 *            node or relation
	 * @return Entities
	 */
	public Entities matchRelationEntity(String label, boolean isNode) {
		Entities result = new Entities();
		for (Map<String, Object> map : query("MATCH (n)-[r:" + label + "]->(m) RETURN id(r),id(n),id(m),r", isNode)) {
			Relation r = (Relation) map.get("r");
			r.setId((String) (map.get("id(r)") + ""));
			r.setFrom((String) (map.get("id(n)") + ""));
			r.setTo((String) (map.get("id(m)") + ""));
			result.add(map);
		}
		return result;
	}
}
