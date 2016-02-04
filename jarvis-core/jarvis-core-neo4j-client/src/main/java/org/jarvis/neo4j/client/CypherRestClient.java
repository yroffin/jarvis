package org.jarvis.neo4j.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
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
	 * @return List<Map<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> query(String statement) {
		StringBuilder query = new StringBuilder();
		query.append("{\"statements\":[{\"statement\":\""+statement+"\"}]})");

		String entity = client.target(baseurl)
	            .path("db/data/transaction/commit")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity(query.toString(),MediaType.APPLICATION_JSON),String.class);
		
		Map<?,?> body = getEntity(entity);
		
		List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
		
		List<?> result = (List<?>) body.get("results");
		List<String> columns = (List<String>) ((Map<?,?>) result.get(0)).get("columns");
		List<Map<String, Object>> data = (List<Map<String, Object>>) ((Map<?,?>) result.get(0)).get("data");
		for(Map<String,Object> element : data) {
			Map<String,Object> rows = new HashMap<String,Object>();
			for(int index = 0;index < columns.size();index++) {
				Object el = ((List<Object>) element.get("row")).get(index);
				if(el instanceof Map<?,?>) {
					rows.put(columns.get(index), new Node((Map<String,Object>) el));
				}
				if(el instanceof Integer) {
					rows.put(columns.get(index), (Integer) el);
				}
			}
			list.add(rows);
		}
		
		return list;
	}

	/**
	 * @param statement
	 * @return List<Map<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryIdWithEntity(String statement) {
		StringBuilder query = new StringBuilder();
		query.append("{\"statements\":[{\"statement\":\""+statement+"\"}]})");

		String entity = client.target(baseurl)
	            .path("db/data/transaction/commit")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity(query.toString(),MediaType.APPLICATION_JSON),String.class);
		
		Map<?,?> body = getEntity(entity);
		
		List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
		
		List<?> result = (List<?>) body.get("results");
		List<String> columns = (List<String>) ((Map<?,?>) result.get(0)).get("columns");
		List<Map<String, Object>> data = (List<Map<String, Object>>) ((Map<?,?>) result.get(0)).get("data");
		for(Map<String,Object> element : data) {
			Map<String,Object> rows = new HashMap<String,Object>();
			for(int index = 0;index < columns.size();index++) {
				Object el = ((List<Object>) element.get("row")).get(index);
				if(el instanceof Map<?,?>) {
					rows.put(columns.get(index), new Node((Map<String,Object>) el));
				}
				if(el instanceof Integer) {
					rows.put(columns.get(index), (Integer) el);
				}
			}
			list.add(rows);
		}
		
		return list;
	}

	/**
	 * @return new node
	 */
	public Node createNode() {
		String entity = client.target(baseurl)
	            .path("db/data/node")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity("{}",MediaType.APPLICATION_JSON),String.class);
		
		Map<?,?> body = getEntity(entity);

		@SuppressWarnings("unchecked")
		Node node = new Node((String) (((Map<String, Object>) body.get("metadata")).get("id")+""));

		return node;
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
	 * @return Node
	 */
	public Node createNode(String label) {
		/**
		 * create node
		 */
		Node node = createNode();

		client.target(baseurl)
	            .path("db/data/node/"+node.id+"/labels")
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .post(Entity.entity("\""+label+"\"",MediaType.APPLICATION_JSON),String.class);

		return node;
	}

	/**
	 * @return Transaction
	 */
	public Transaction beginTx() {
		return new Transaction();
	}

	/**
	 * @param query
	 * @return Result
	 */
	public Result execute(String query) {
		logger.error(query);
		Result result = new Result();
		for(Map<String, Object> map : query(query)) {
			result.add(map);
		}
		return result;
	}

	/**
	 * @param query
	 * @param entity 
	 * @return Result
	 */
	public Entities matchIdWithEntity(String query, String entity) {
		logger.error(query);
		Entities result = new Entities();
		for(Map<String, Object> map : queryIdWithEntity(query)) {
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
	 * @return Result
	 */
	public Entities matchIdWithEntity(String query, String first, String second) {
		logger.error(query);
		Entities result = new Entities();
		for(Map<String, Object> map : queryIdWithEntity(query)) {
			Node n = (Node) map.get(first);
			n.setId((String) (map.get("id("+first+")")+""));
			Node m = (Node) map.get(second);
			m.setId((String) (map.get("id("+second+")")+""));
			result.add(map);
		}
		return result;
	}
}
