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

package org.jarvis.neo4j.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.type.GenericMap;
import org.jarvis.neo4j.client.model.CypherError;
import org.jarvis.neo4j.client.model.CypherResults;
import org.jarvis.neo4j.client.model.CypherRow;
import org.jarvis.neo4j.client.model.CypherRows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * cypher query
 */
public class CypherRestClient extends AbstractJerseyClient {
	protected Logger logger = LoggerFactory.getLogger(CypherRestClient.class);

	/**
	 * @param baseurl
	 * @param user 
	 * @param password 
	 */
	public CypherRestClient(String baseurl, String user, String password) {
		super(baseurl, user, password);
	}

	private Map<?, ?> getEntity(String entity) {
		Map<?,?> body = null;
		try {
			body = mapper.readValue(entity, Map.class);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return body;
	}

	/**
	 * @param statement
	 * @param isNode 
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> query(String statement, boolean isNode) {
		StringBuilder query = new StringBuilder();
		query.append("{\"statements\":[{\"statement\":\""+statement+"\"}]})");

		String entity = client.target(baseurl)
	            .path("db/data/transaction/commit")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity(query.toString(),MediaType.APPLICATION_JSON),String.class);
		
		return decodeResult(entity, isNode);
	}

	/**
	 * @param statement
	 * @param isNode 
	 * @return List<Map<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryIdWithEntity(String statement, boolean isNode) {
		StringBuilder query = new StringBuilder();
		query.append("{\"statements\":[{\"statement\":\""+statement+"\"}]})");

		String entity = client.target(baseurl)
	            .path("db/data/transaction/commit")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity(query.toString(),MediaType.APPLICATION_JSON),String.class);

		return decodeResult(entity, isNode);
	}

	/**
	 * @param entity
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> decodeResult(String entity, boolean isNode) {
		CypherResults cypherResults = null;
		try {
			cypherResults = mapper.readValue(entity,CypherResults.class);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	
		List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
		
		if(cypherResults.getResults().size() == 0) {
			for(CypherError error : cypherResults.getErrors()) {
				logger.error("Cypher error {}", error.getCode(), error.getMessage());
			}
			throw new TechnicalException("Cypher error");
		}

		/**
		 * iterate on data
		 */
		for(CypherRows element : cypherResults.getResults().get(0).getData()) {
			Map<String,Object> rows = new HashMap<String,Object>();
			int index = 0;
			for(CypherRow el : element.getRow()) {
				if(el.isInteger()) {
					rows.put(cypherResults.getResults().get(0).getColumns().get(index++), el.getInteger());
				}
				if(el.isObject()) {
					if(isNode) {
						rows.put(cypherResults.getResults().get(0).getColumns().get(index++), new Node((el.getObject())));
					} else {
						rows.put(cypherResults.getResults().get(0).getColumns().get(index++), new Relation((el.getObject())));
					}
				}
			}
			list.add(rows);
		}
		
		return list;
	}

	/**
	 * @return List<Node>
	 * @throws TechnicalHttpException
	 */
	public Map<String, Map<String, GenericMap>> findAllNodes() throws TechnicalHttpException {
		
		Map<String, Map<String, GenericMap>> snapshots = new TreeMap<String, Map<String, GenericMap>>();
		
		List<String> relations = new ArrayList<String>();
		relations.add("HREF");
		relations.add("HREF_IF");
		relations.add("HREF_THEN");
		relations.add("HREF_ELSE");
		for(String relation : relations) {
			Map<String, GenericMap> beans = new TreeMap<String, GenericMap>();
			snapshots.put(relation, beans);
			Entities links = matchRelationEntity(relation, false);
			for(Map<String, Object> item : links.elements) {
				Relation rel = (Relation) item.get("r");
				GenericMap genericMap = new GenericMap();
				for(Entry<String, Object> field : rel.fields.entrySet()) {
					genericMap.put(field.getKey(), field.getValue());
				}
				genericMap.put("__from", item.get("id(n)"));
				genericMap.put("__to", item.get("id(m)"));
				beans.put(rel.getId(), genericMap);
			}
		}

		List<String> resources = new ArrayList<String>();
		resources.add("BlockBean");
		resources.add("CommandBean");
		resources.add("IotBean");
		resources.add("ScenarioBean");
		resources.add("ScriptPluginBean");
		resources.add("TriggerBean");
		resources.add("ViewBean");
		
		/**
		 * iterate on all resources
		 */
		for(String resource : resources) {
			Map<String, GenericMap> beans = new TreeMap<String, GenericMap>();
			snapshots.put(resource, beans);
			Entities result = matchIdWithEntity("/* find entities */ MATCH (node:" + resource + ") return id(node),node", "node", true);
			for(Map<String, Object> item : result.elements) {
				Node node = (Node) item.get("node");
				GenericMap genericMap = new GenericMap();
				for(Entry<String, Object> field : node.fields.entrySet()) {
					genericMap.put(field.getKey(), field.getValue());
				}
				beans.put(node.getId(), genericMap);
			}
		}

		return snapshots;
	}

	/**
	 * @param toCreate 
	 * @return new node
	 * @throws TechnicalHttpException 
	 */
	@SuppressWarnings("unchecked")
	public Node createNode(Node toCreate) throws TechnicalHttpException {
		Response entity;
		try {
			entity = client.target(baseurl)
			        .path("db/data/node")
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .post(Entity.entity(mapper.writeValueAsString(toCreate.getAllProperties()),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		
		if(entity.getStatus() == 201) {
			Map<?, ?> body = getEntity(entity.readEntity(String.class));
	
			Node node = new Node(toCreate.getAllProperties());
			node.setId((String) (((Map<String, Object>) body.get("metadata")).get("id")+""));
	
			return node;
		} else {
			throw new TechnicalHttpException(entity.getStatus(), "db/data/node");
		}
	}

	/**
	 * @param update 
	 * @return new node
	 * @throws TechnicalNotFoundException 
	 */
	public Node updateRelationship(Node update) throws TechnicalNotFoundException {
		Response entity;
		try {
			entity = client.target(baseurl)
			        .path("db/data/relationship/"+update.getId()+"/properties")
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .put(Entity.entity(mapper.writeValueAsString(update.getAllProperties()),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		
		if(entity.getStatus() == 204) {
			return update;
		} else {
			throw new TechnicalNotFoundException(update.getId());
		}
	}

	/**
	 * @param update 
	 * @return new node
	 * @throws TechnicalNotFoundException 
	 */
	public Node updateNode(Node update) throws TechnicalNotFoundException {
		Response entity;
		try {
			entity = client.target(baseurl)
			        .path("db/data/node/"+update.getId()+"/properties")
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .put(Entity.entity(mapper.writeValueAsString(update.getAllProperties()),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		
		if(entity.getStatus() == 204) {
			return update;
		} else {
			throw new TechnicalNotFoundException(update.getId());
		}
	}

	/**
	 * @param label
	 * @param toCreate 
	 * @return Node
	 * @throws TechnicalHttpException 
	 * @throws TechnicalNotFoundException 
	 */
	public Node createNode(String label, Node toCreate) throws TechnicalHttpException, TechnicalNotFoundException {
		/**
		 * create node
		 */
		Node node = createNode(toCreate);

		Response entity;
		entity = client.target(baseurl)
	            .path("db/data/node/"+node.id+"/labels")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity("\""+label+"\"",MediaType.APPLICATION_JSON));

		if(entity.getStatus() == 204) {
			return node;
		} else {
			throw new TechnicalNotFoundException(node.getId());
		}
	}

	/**
	 * @return Transaction
	 */
	public Transaction beginTx() {
		return new Transaction();
	}

	/**
	 * @param query
	 * @param isNode 
	 * @return Result
	 */
	public Result execute(String query, boolean isNode) {
		logger.error(query);
		Result result = new Result();
		for(Map<String, Object> map : query(query, isNode)) {
			result.add(map);
		}
		return result;
	}

	/**
	 * @param query
	 * @param entity
	 * @param isNode 
	 * @return Result
	 */
	public Entities matchIdWithEntity(String query, String entity, boolean isNode) {
		logger.error(query);
		Entities result = new Entities();
		for(Map<String, Object> map : queryIdWithEntity(query, isNode)) {
			Node n = (Node) map.get(entity);
			n.setId((String) (map.get("id("+entity+")")+""));
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
		logger.error(query);
		Entities result = new Entities();
		for(Map<String, Object> map : queryIdWithEntity(query, isNode)) {
			Node n = (Node) map.get(first);
			n.setId((String) (map.get("id("+first+")")+""));
			Node m = (Node) map.get(second);
			m.setId((String) (map.get("id("+second+")")+""));
			result.add(map);
		}
		return result;
	}

	/**
	 * @param label 
	 * @param isNode 
	 * @return Result
	 */
	public Entities matchRelationEntity(String label, boolean isNode) {
		Entities result = new Entities();
		for(Map<String, Object> map : queryIdWithEntity("MATCH (n)-[r:"+label+"]->(m) RETURN id(r),id(n),id(m),r", isNode)) {
			Relation r = (Relation) map.get("r");
			r.setId((String) (map.get("id(r)")+""));
			r.setFrom((String) (map.get("id(n)")+""));
			r.setTo((String) (map.get("id(m)")+""));
			result.add(map);
		}
		return result;
	}

}
