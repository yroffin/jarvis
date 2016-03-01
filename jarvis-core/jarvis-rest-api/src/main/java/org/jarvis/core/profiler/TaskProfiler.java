package org.jarvis.core.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.profiler.element.ActivityNode;
import org.jarvis.core.profiler.element.BooleanGatewayNode;
import org.jarvis.core.profiler.element.CallActivityNode;
import org.jarvis.core.profiler.element.EndNode;
import org.jarvis.core.profiler.element.SequenceFlow;
import org.jarvis.core.profiler.element.StartNode;
import org.jarvis.core.profiler.model.DefaultFlow;
import org.jarvis.core.profiler.model.DefaultNode;
import org.jarvis.core.profiler.model.GenericNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic map
 */
public class TaskProfiler {

	protected ObjectMapper mapper = new ObjectMapper();
	Map<String, GenericNode> nodes = new TreeMap<String, GenericNode>();
	List<DefaultNode> ends = new ArrayList<DefaultNode>();
	List<DefaultFlow> edges = new ArrayList<DefaultFlow>();

	/**
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public GenericNode addStartNode(String name, String description) {
		GenericNode node = new StartNode(name, description);
		nodes.put(node.getId(), node);
		return node;
	}

	/**
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public GenericNode addEndNode(String name, String description) {
		GenericNode node = new EndNode(name, description);
		nodes.put(node.getId(), node);
		ends.add((DefaultNode) node);
		return node;
	}

	/**
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public GenericNode addActivty(String name, String description) {
		GenericNode node = new ActivityNode(name, description);
		nodes.put(node.getId(), node);
		return node;
	}

	/**
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public GenericNode addCallActivty(String name, String description) {
		GenericNode node = new CallActivityNode(name, description);
		nodes.put(node.getId(), node);
		return node;
	}

	/**
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public GenericNode addBooleanGateway(String name, String description) {
		GenericNode node = new BooleanGatewayNode(name, description);
		nodes.put(node.getId(), node);
		return node;
	}

	/**
	 * @param name
	 * @param source 
	 * @param target 
	 * @return GenericNode
	 */
	public GenericNode addSequenceFlowSimple(String name, GenericNode source, GenericNode target) {
		DefaultFlow node = new SequenceFlow(name, source, target);
		edges.add(node);
		return node;
	}

	/**
	 * @param name
	 * @param source
	 * @param target
	 * @param b
	 * @return GenericNode
	 */
	public GenericNode addSequenceFlowDecision(String name, GenericNode source, GenericNode target, boolean b) {
		DefaultFlow node = new SequenceFlow(name, source, target, b);
		edges.add(node);
		return node;
	}

	/**
	 * @return String
	 */
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"nodes\":\n");
		try {
			json.append(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nodes));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		json.append(",\n");
		json.append("\"edges\":\n");
		try {
			json.append(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(edges));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		json.append("\n");
		json.append("}\n");
		return json.toString();
	}
}
