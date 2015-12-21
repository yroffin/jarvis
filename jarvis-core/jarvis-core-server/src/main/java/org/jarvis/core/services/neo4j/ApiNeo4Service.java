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

import java.io.File;
import java.net.MalformedURLException;

import javax.annotation.PostConstruct;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.logging.slf4j.Slf4jLogProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:server.properties")
public class ApiNeo4Service  {
	protected Logger logger = LoggerFactory.getLogger(ApiNeo4Service.class);

	@Autowired
	Environment env;

	/**
	 * static graph db handle
	 */
	static GraphDatabaseService graphDb = null;
	
	/**
	 * spring init
	 * @throws IllegalArgumentException
	 * @throws MalformedURLException
	 */
	@PostConstruct
	public void init() throws IllegalArgumentException, MalformedURLException {
		File dbFile = new File(env.getProperty("jarvis.neo4j.dir"));
		graphDb = new GraphDatabaseFactory()
				.setUserLogProvider( new Slf4jLogProvider() )
			    .newEmbeddedDatabaseBuilder( dbFile )
			    .loadPropertiesFromFile( "neo4j.properties" )
			    .newGraphDatabase();
		
		// Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}

	/**
	 * create node
	 * @return Node
	 */
	public Node createNode() {
		return graphDb.createNode();
	}

	/**
	 * begin a new transaction
	 * @return Transaction
	 */
	public Transaction beginTx() {
		return graphDb.beginTx();
	}

	/**
	 * execute cypher query
	 * @param query
	 * @return Result
	 */
	public Result execute(String query) {
		logger.error(query);
		return graphDb.execute(query);
	}

	/**
	 * create node with label
	 * @param label
	 * @return
	 */
	public Node createNode(Label label) {
		return graphDb.createNode(label);
	}

	/**
	 * cypher query to find one element
	 * @param label
	 * @param id
	 * @return
	 */
	public Result cypherOne(String label, String id) {
		Result result = execute("MATCH (node:"+label+") WHERE id(node) = "+id+" RETURN node");
		return result;
	}

	/**
	 * find all relationship
	 * @param leftLabel
	 * @param leftId
	 * @param rightLabel
	 * @param rightId
	 * @return
	 */
	public Result cypherAllLink(String leftLabel, String leftId, String rightLabel) {
		Result result = execute("MATCH (left:"+leftLabel+")-[r:RELTYPE]->(right:"+rightLabel+") WHERE id(left) = "+leftId+" RETURN right");
		return result;
	}

	/**
	 * find all relationship
	 * @param leftLabel
	 * @param leftId
	 * @param rightLabel
	 * @param rightId
	 * @return
	 */
	public Result cypherAddLink(String leftLabel, String leftId, String rightLabel, String rightId) {
		Result result = execute("MATCH (left:"+leftLabel+"),(right:"+rightLabel+") WHERE id(left) = "+leftId+" AND id(right) = "+rightId+" CREATE (left)-[r:RELTYPE]->(right) RETURN id(r)");
		return result;
	}
}