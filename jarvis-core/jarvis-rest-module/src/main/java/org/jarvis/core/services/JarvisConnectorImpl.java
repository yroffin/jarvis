package org.jarvis.core.services;

import java.util.HashMap;
import java.util.Map;

import org.jarvis.rest.services.impl.JarvisModuleException;

/**
 * abstract connector
 */
public abstract class JarvisConnectorImpl implements JarvisConnector {

	protected String id;
	protected String name;
	protected boolean renderer;
	protected boolean sensor;
	protected boolean canAnswer;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isRenderer() {
		return renderer;
	}

	@Override
	public boolean isSensor() {
		return sensor;
	}

	@Override
	public boolean canAnswer() {
		return canAnswer;
	}

	@Override
	public Map<String, Object> post(Map<String, Object> input, Map<String, String> params) throws JarvisModuleException {
		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> get(Map<String, String> params) throws JarvisModuleException {
		return new HashMap<String, Object>();
	}

}
