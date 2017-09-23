package org.jarvis.core.services.helper;

import java.net.URISyntaxException;

import org.common.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.resources.api.connectors.ApiConnectorResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import groovyx.net.http.RESTClient;

/**
 * groovy script helper
 */
@Component
public class PluginConnectorHelper {
	@Autowired
	ApiConnectorResources apiConnectorResources;
	
	/**
	 * find connector by its name
	 * @param name
	 * name of this connector
	 * @return RESTClient
	 * @throws URISyntaxException
	 * @throws TechnicalNotFoundException
	 */
	public RESTClient connectorByName(String name) throws URISyntaxException, TechnicalNotFoundException {
		for(ConnectorRest connector : apiConnectorResources.doFindAllRest()) {
			if(connector.name.equals(name)) {
				return new RESTClient( connector.adress );
			}
		}
		throw new TechnicalNotFoundException(name);
	}
}
