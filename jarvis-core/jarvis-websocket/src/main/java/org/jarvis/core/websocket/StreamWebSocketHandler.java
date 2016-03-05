package org.jarvis.core.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * web socket handler
 */

@WebSocket
public class StreamWebSocketHandler {

	protected static Logger logger = LoggerFactory.getLogger(StreamWebSocketHandler.class);

	/**
	 * internal sessions map
	 */
	public static Map<Session, String> sessionMap = new ConcurrentHashMap<>();
	static int nextSessionNumber = 1; // Assign to session for next connecting session

	/**
	 * handle new session
	 * 
	 * @param user
	 * @throws Exception
	 */
	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		String sessionId = "websocket-session-" + nextSessionNumber++;
		sessionMap.put(user, sessionId);
		logger.info("onConnect {}", sessionMap.get(user));
	}

	/**
	 * close session
	 * 
	 * @param user
	 * @param statusCode
	 * @param reason
	 */
	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		sessionMap.remove(user);
		logger.info("onClose {} {} {}", sessionMap.get(user), statusCode, reason);
	}

	/**
	 * on new message
	 * 
	 * @param user
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		logger.info("onMessage {} {}", sessionMap.get(user), message);
	}

}
