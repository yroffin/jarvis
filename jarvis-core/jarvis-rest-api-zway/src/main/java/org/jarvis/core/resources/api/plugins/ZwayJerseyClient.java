package org.jarvis.core.resources.api.plugins;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.ws.rs.core.MediaType;

import org.jarvis.core.AbstractJerseyClient;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.rest.plugin.ZwayRest;

/**
 * zway jersey client
 */
public class ZwayJerseyClient extends AbstractJerseyClient {
	
	/**
	 * zway client
	 * @param url
	 * @param user
	 * @param password
	 * @param r 
	 * @param c 
	 */
	public ZwayJerseyClient(String url, String user, String password, String r, String c) {
		/**
		 * initialize
		 */
		initialize(
				url,
				user,
				password,
				r,
				c);
	}

	/**
	 * get zway api
	 * @param device 
	 * @param instance 
	 * @param commandClasses 
	 * @param data 
	 * @return String
	 */
	public ZwayRest call(String device, String instance, String commandClasses, String data) {
		/**
		 * build response
		 */
		javax.ws.rs.core.Response entity;
		entity = this.target()
		        .path("/ZWaveAPI/Run/devices["+device+"].instances["+instance+"].commandClasses["+commandClasses+"].data["+data+"]")
		        .request(MediaType.APPLICATION_JSON)
		        .accept(MediaType.APPLICATION_JSON)
		        .acceptEncoding("charset=UTF-8")
		        .get();
		
		/**
		 * verify result
		 */
		if(entity.getStatus() == 200) {
			String raw = entity.readEntity(String.class);
			LinkedHashMap<?, ?> result = null;
			ZwayRest obj = new ZwayRest();
			try {
				result = mapper.readValue(raw, LinkedHashMap.class);
				for(Object key : result.keySet()) {
					obj.put((String) key, result.get(key));
				}
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			return obj;
		} else {
			throw new TechnicalException(entity.getStatus() + ":/json");
		}
	}
}
