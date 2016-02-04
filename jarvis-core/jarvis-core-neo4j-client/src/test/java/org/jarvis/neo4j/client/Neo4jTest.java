package org.jarvis.neo4j.client;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class Neo4jTest {

	@Test
	public void test() {
		CypherRestClient cypherRestClient = new CypherRestClient("http://localhost:7474","neo4j","123456");
		Result res = cypherRestClient.execute("MATCH (n) RETURN n LIMIT 100");
		System.err.println("Result:"+res);
		Node create = cypherRestClient.createNode();
		System.err.println("Node:"+create);
		Node createLabel = cypherRestClient.createNode("TEST");
		System.err.println("Node:"+createLabel);
		int i = 0;
	}

	@Test
	public void testCypherFindLink() {
		String leftLabel = "BlockBean";
		String rightLabel = "ScriptPluginBean";
		String relId = "116";
		
		CypherRestClient cypherRestClient = new CypherRestClient("http://localhost:7474","neo4j","123456");
		Result result = cypherRestClient.execute("MATCH (left:"+leftLabel+")-[r]->(right:"+rightLabel+") WHERE id(r) = "+relId+" RETURN id(r),r");

		if(result.hasNext()) {
			Map<String, Object> rows = result.next();
			/**
			 * set relationship properties
			 */
			String id = (String) (rows.get("id(r)")+"");
			Node r = (Node) rows.get("r");
			int i = 0;
		}
		int i = 0;
	}

}
