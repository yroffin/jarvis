package org.jarvis.core.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jarvis.core.exception.TechnicalException;
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
	private final static Map<Session, String> sessionMap = new ConcurrentHashMap<>();
	protected static int nextSessionNumber = 1; // Assign to session for next connecting session

	/**
	 * handle new session
	 * 
	 * @param user
	 * @throws TechnicalException 
	 */
	@OnWebSocketConnect
	public static void onConnect(Session user) throws TechnicalException {
		try {
			String sessionId = "websocket-session-" + nextSessionNumber++;
			getSessionmap().put(user, sessionId);
			logger.info("onConnect {}", getSessionmap().get(user));
		} catch(Exception e) {
			logger.error("onConnect {}", e);
			throw new TechnicalException(e);
		}
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
		getSessionmap().remove(user);
		logger.info("onClose {} {} {}", getSessionmap().get(user), statusCode, reason);
	}

	/**
	 * on new message
	 * 
	 * @param user
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		logger.info("onMessage {} {}", getSessionmap().get(user), message);
	}

	public static Map<Session, String> getSessionmap() {
		return sessionMap;
	}

}
