package org.jarvis.core.profiler.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Processus
 */
@JsonPropertyOrder(alphabetic=true)
public class DefaultProcess {
	/**
	 * @param stage
	 */
	public DefaultProcess(String stage) {
		this.stage = stage;
	}
	/**
	 * nodes mapper
	 * @param key
	 * @param value 
	 */
	public void nodes(String key, GenericNode value) {
		nodes.put(key, value);		
	}
	/**
	 * @param flow
	 */
	public void edges(DefaultFlow flow) {
		edges.add(flow);
	}
	@JsonProperty("nodes")
	Map<String, GenericNode> nodes = new TreeMap<String, GenericNode>();
	@JsonProperty("edges")
	List<DefaultFlow> edges = new ArrayList<DefaultFlow>();
	@JsonProperty("stage")
	private String stage;
}
