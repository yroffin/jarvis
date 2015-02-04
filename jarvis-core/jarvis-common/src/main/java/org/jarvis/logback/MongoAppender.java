/*
 * Copyright 2013 Yann Le Tallec.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jarvis.logback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

/**
 * A logback appender that uses Mongo to log messages.
 * <p>
 * Typical configuration:
 * 
 * <pre>
 * {@code
 * <appender name=\"TEST\" class=\"com.assylias.logging.MongoAppender\">
 *     <host>192.168.1.1</host>
 *     <port>27017</port>
 *     <db>log</db>
 *     <collection>test</collection>
 * </appender>
 * }
 * </pre>
 * 
 * The log messages have the following JSON format (the {@code marker},
 * {@code exception} and {@code stacktrace} fields are optional):
 * 
 * <pre>
 * {@code
 * { "_id" : ObjectId("514b2d529234d98131221578"),
 *   "logger" : "com.assylias.logging.MongoAppenderTest",
 *   "timestamp" : ISODate("2013-03-21T15:54:58.357Z"),
 *   "level" : "ERROR",
 *   "marker" : "Marker",
 *   "thread" : "TestNG",
 *   "message" : "An error occurend in the test",
 *   "exception" : "java.lang.RuntimeException: java.lang.Exception",
 *   "stacktrace" : [ "at com.assylias.logging.MongoAppenderTest.testCausedBy(MongoAppenderTest.java:129) ~[test-classes/:na]",
 *                    "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.7.0_17]",
 *                    "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57) ~[na:1.7.0_17]",
 *                    "Caused by: java.lang.Exception: null",
 *                    "at com.assylias.logging.MongoAppenderTest.testCausedBy(MongoAppenderTest.java:126) ~[test-classes/:na]",
 *                    "... 20 common frames omitted" ],
 * } }
 * </pre>
 * 
 * If an error occurs while logging, the message might also contain a
 * {@code logging_error} field:
 * 
 * <pre>
 * {@code
 *    "logging_error" : "Could not log all the event information: com.mongodb.MongoInterruptedException: A driver operation has been interrupted
 *                       at com.mongodb.DBPortPool.get(DBPortPool.java:216)
 *                       at com.mongodb.DBTCPConnector$MyPort.get(DBTCPConnector.java:440)
 *                       ..."
 * }
 * </pre>
 */
public class MongoAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
	protected Logger logger = LoggerFactory.getLogger(MongoAppender.class);

	private String host;
	private int port;
	private String db;
	private String collection;
	private DBCollection logCollection;

	public MongoAppender() {
	}

	@Override
	public void start() {
		try {
			connect();
			super.start();
		} catch (UnknownHostException e) {
			addError("Can't connect to mongo: host=" + host + ", port=" + port,
					e);
		} catch (MongoException e) {
			addError("Can't connect to mongo: host=" + host + ", port=" + port,
					e);
		}
	}

	private void connect() throws UnknownHostException {
		MongoClient client = new MongoClient(host, port);
		DB mongoDb = client.getDB(db == null ? "log" : db);
		logCollection = mongoDb.getCollection(collection == null ? "log"
				: collection);
	}

	@Override
	protected void append(ILoggingEvent evt) {
		if (evt == null)
			return; // just in case

		DBObject log = getBasicLog(evt);
		try {
			logException(evt.getThrowableProxy(), log);
			logCollection.insert(log);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			try {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				log.put("logging_error",
						"Could not log all the event information: "
								+ sw.toString());
				log.put("level", "ERROR");
				logCollection.insert(log);
			} catch (Exception e2) { // really not working
				addError("Could not insert log to mongo: " + evt, e2);
				System.err.println(e2.getMessage());
			}
		}
	}

	private DBObject getBasicLog(ILoggingEvent evt) {
		DBObject log = new BasicDBObject();
		log.put("logger", evt.getLoggerName());
		log.put("timestamp", new Date(evt.getTimeStamp()));
		log.put("level", String.valueOf(evt.getLevel())); // in case getLevel
															// returns null
		Marker m = evt.getMarker();
		if (m != null) {
			log.put("marker", m.getName());
		}
		log.put("thread", evt.getThreadName());
		log.put("message", evt.getFormattedMessage());
		return log;
	}

	private void logException(IThrowableProxy tp, DBObject log) {
		if (tp == null)
			return;
		String tpAsString = ThrowableProxyUtil.asString(tp); // the stack trace
																// basically
		List<String> stackTrace = Arrays.asList(tpAsString.replace("\t", "")
				.split(CoreConstants.LINE_SEPARATOR));
		if (stackTrace.size() > 0) {
			log.put("exception", stackTrace.get(0));
		}
		if (stackTrace.size() > 1) {
			log.put("stacktrace", stackTrace.subList(1, stackTrace.size()));
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}
}