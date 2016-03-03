package org.jarvis.core.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jarvis.core.model.bean.websocket.WebsocketDataBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * web socket handler
 */

@WebSocket
public class StreamWebSocketHandler {
	
	protected Logger logger = LoggerFactory.getLogger(StreamWebSocketHandler.class);
	
	static Map<Session, String> sessionMap = new HashMap<>();
	static int nextSessionNumber = 1; //Assign to session for next connecting session
	static protected ObjectMapper mapper = new ObjectMapper();
	
	{
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}

    /**
     * broadcast object (with its identifier to client)
     * @param sender
     * client identifier
     * @param instance
     * object instance (on server side)
     * @param data 
     * object itself
     */
    public static void broadcast(String sender, String instance, Object data) {
    	sessionMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(mapper.writeValueAsString(new WebsocketDataBean(instance, data)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
	 * handle new session
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
     * @param user
     * @param message
     */
    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	logger.info("onMessage {} {}", sessionMap.get(user), message);
    }

}
