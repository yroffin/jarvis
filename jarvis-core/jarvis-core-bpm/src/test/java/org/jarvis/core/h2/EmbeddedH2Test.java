package org.jarvis.core.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Cf. https://www.mkyong.com/spring/spring-embedded-database-examples
 */
public class EmbeddedH2Test {
	protected static Logger logger = LoggerFactory.getLogger(EmbeddedH2Test.class);
	
	private EmbeddedDatabase db;

	/**
	 * setup
	 */
	@Before
	public void setUp() {
		// db = new EmbeddedDatabaseBuilder().addDefaultScripts().build();
		db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("camunda").build();
	}

	/**
	 * simple test
	 * @throws InterruptedException 
	 * @throws SQLException 
	 */
	@Test
	public void testFindByname() throws InterruptedException, SQLException {
		Connection connection = db.getConnection();
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		logger.info(databaseMetaData.toString());
	}

}
