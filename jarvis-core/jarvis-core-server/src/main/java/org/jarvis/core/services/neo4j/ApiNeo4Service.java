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

import java.net.MalformedURLException;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.neo4j.client.CypherRestClient;
import org.jarvis.neo4j.client.Entities;
import org.jarvis.neo4j.client.Node;
import org.jarvis.neo4j.client.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Neo4j driver
 */
@Component
@PropertySource("classpath:server.properties")
public class ApiNeo4Service  {
	protected Logger logger = LoggerFactory.getLogger(ApiNeo4Service.class);

	@Autowired
	Environment env;

	/**
	 * static graph db handle
	 */
	CypherRestClient graphDb = null;
	
	/**
	 * spring init
	 * @throws IllegalArgumentException
	 * @throws MalformedURLException
	 */
	@PostConstruct
	public void init() throws IllegalArgumentException, MalformedURLException {
		graphDb = new CypherRestClient(
				env.getProperty("jarvis.neo4j.url"),
				env.getProperty("jarvis.neo4j.user"),
				env.getProperty("jarvis.neo4j.password")
		);
	}

	/**
	 * create node
	 * @param toCreate 
	 * @return Node
	 * @throws TechnicalHttpException 
	 */
	public Node createNode(Node toCreate) throws TechnicalHttpException {
		return graphDb.createNode(toCreate);
	}

	/**
	 * @param update
	 * @return Node
	 * @throws TechnicalNotFoundException 
	 */
	public Node updateRelationship(Node update) throws TechnicalNotFoundException {
		return graphDb.updateRelationship(update);
	}

	/**
	 * @param update
	 * @return Node
	 * @throws TechnicalNotFoundException 
	 */
	public Node updateNode(Node update) throws TechnicalNotFoundException {
		return graphDb.updateNode(update);
	}

	/**
	 * begin a new transaction
	 * @return Transaction
	 */
	public Transaction beginTx() {
		return graphDb.beginTx();
	}

	/**
	 * @param query
	 */
	public void execute(String query) {
		graphDb.execute(query);
	}
	
	/**
	 * execute cypher query
	 * @param query
	 * @param first 
	 * @param second 
	 * @return Result
	 */
	public Entities matchIdWithEntity(String query, String first, String second) {
		Entities entities = null;
		if(second == null) {
			entities = graphDb.matchIdWithEntity(query, first);
		} else {
			entities = graphDb.matchIdWithEntity(query, first, second);
		}
		return entities;
	}

	/**
	 * create node with label
	 * @param label
	 * @param toCreate 
	 * @return Node
	 * @throws TechnicalHttpException 
	 * @throws TechnicalNotFoundException 
	 */
	public Node createNode(String label, Node toCreate) throws TechnicalHttpException, TechnicalNotFoundException {
		return graphDb.createNode(label, toCreate);
	}

	/**
	 * cypher query to find one element
	 * @param label
	 * @param id
	 * @param entity 
	 * @return Result
	 */
	public Entities cypherOne(String label, String id, String entity) {
		Entities result = matchIdWithEntity("/* find one node */ MATCH ("+entity+":"+label+") WHERE id("+entity+") = "+id+" RETURN id("+entity+"),"+entity, entity, null);
		return result;
	}

	/**
	 * find all relationship
	 * @param leftLabel
	 * @param leftId
	 * @param rightLabel
	 * @param relType 
	 * @param relation 
	 * @param entity 
	 * @return Result
	 */
	public Entities cypherAllLink(String leftLabel, String leftId, String rightLabel, String relType, String relation, String entity) {
		Entities result = matchIdWithEntity("/* all links filtered by relation type */ MATCH (left:"+leftLabel+")-["+relation+":"+relType+"]->("+entity+":"+rightLabel+") WHERE id(left) = "+leftId+" RETURN id("+relation+"),"+relation+",id("+entity+"),"+entity, relation, entity);
		return result;
	}

	/**
	 * add new relationship
	 * @param leftLabel
	 * @param leftId
	 * @param rightLabel
	 * @param rightId
	 * @param relType 
	 * @param relation 
	 * @return Result
	 */
	public Entities cypherAddLink(String leftLabel, String leftId, String rightLabel, String rightId, String relType, String relation) {
		Entities result = matchIdWithEntity("/* add a new relation */ MATCH (left:"+leftLabel+"),(right:"+rightLabel+") WHERE id(left) = "+leftId+" AND id(right) = "+rightId+" CREATE (left)-["+relation+":"+relType+"]->(right) RETURN id("+relation+"),"+relation, relation, null);
		return result;
	}

	/**
	 * find a relationship
	 * @param leftLabel
	 * @param rightLabel
	 * @param relId 
	 * @param relation 
	 * @return Result
	 */
	public Entities cypherFindLink(String leftLabel, String rightLabel, String relId, String relation) {
		Entities result = matchIdWithEntity("/* find links */ MATCH (left:"+leftLabel+")-["+relation+"]->(right:"+rightLabel+") WHERE id("+relation+") = "+relId+" RETURN id("+relation+"),"+relation, relation, null);
		return result;
	}

	/**
	 * delete relationship
	 * @param leftLabel
	 * @param leftId
	 * @param rightLabel
	 * @param rightId
	 * @param relType
	 * @param instance
	 * @param relation 
	 */
	public void cypherDeleteLink(String leftLabel, String leftId, String rightLabel, String rightId, String relType, String instance, String relation) {
		execute("/* delete link */ MATCH (left:"+leftLabel+")-["+relation+":"+relType+"]->(right:"+rightLabel+") WHERE id(left) = "+leftId+" AND id(right) = "+rightId+" AND id("+relation+") = "+instance+" DELETE "+relation);
	}
}
