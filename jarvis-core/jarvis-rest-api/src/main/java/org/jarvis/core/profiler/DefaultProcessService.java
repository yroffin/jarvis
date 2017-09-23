package org.jarvis.core.profiler;

import java.util.List;

import org.common.core.exception.TechnicalException;
import org.jarvis.core.profiler.element.ActivityNode;
import org.jarvis.core.profiler.element.BooleanGatewayNode;
import org.jarvis.core.profiler.element.CallActivityNode;
import org.jarvis.core.profiler.element.EndNode;
import org.jarvis.core.profiler.element.SequenceFlow;
import org.jarvis.core.profiler.element.StartNode;
import org.jarvis.core.profiler.model.DefaultFlow;
import org.jarvis.core.profiler.model.DefaultProcess;
import org.jarvis.core.profiler.model.GenericNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic map
 */
public class DefaultProcessService {

	protected static ObjectMapper mapper = new ObjectMapper();

	/**
	 * @param process 
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public static GenericNode addStartNode(DefaultProcess process, String name, String description) {
		GenericNode node = new StartNode(name, description);
		process.nodes(node.getId(), node);
		return node;
	}

	/**
	 * @param process 
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public static GenericNode addEndNode(DefaultProcess process, String name, String description) {
		GenericNode node = new EndNode(name, description);
		process.nodes(node.getId(), node);
		return node;
	}

	/**
	 * @param process 
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public static GenericNode addActivity(DefaultProcess process, String name, String description) {
		GenericNode node = new ActivityNode(name, description);
		process.nodes(node.getId(), node);
		return node;
	}

	/**
	 * @param process 
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public static GenericNode addCallActivty(DefaultProcess process, String name, String description) {
		GenericNode node = new CallActivityNode(name, description);
		process.nodes(node.getId(), node);
		return node;
	}

	/**
	 * @param process 
	 * @param name
	 * @param description 
	 * @return GenericNode
	 */
	public static GenericNode addBooleanGateway(DefaultProcess process, String name, String description) {
		GenericNode node = new BooleanGatewayNode(name, description);
		process.nodes(node.getId(), node);
		return node;
	}

	/**
	 * @param process 
	 * @param source 
	 * @param target 
	 * @return DefaultFlow
	 */
	public static DefaultFlow addSequenceFlowSimple(DefaultProcess process, GenericNode source, GenericNode target) {
		DefaultFlow node = new SequenceFlow(source.getLongId() + "->" + target.getLongId(), source, target);
		process.edges(node);
		return node;
	}

	/**
	 * @param process 
	 * @param name
	 * @param source
	 * @param target
	 * @param b
	 * @return DefaultFlow
	 */
	public static DefaultFlow addSequenceFlowDecision(DefaultProcess process, String name, GenericNode source, GenericNode target, boolean b) {
		DefaultFlow flow = new SequenceFlow(source.getLongId() + "["+name+"]" + target.getLongId(), source, target, b);
		process.edges(flow);
		return flow;
	}

	/**
	 * @param processes 
	 * @return String
	 */
	public static String toJson(List<DefaultProcess> processes) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processes);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * @param stage
	 * @param position 
	 * @param message
	 */
	public static void addNote(StringBuilder stage, String position, String message) {
		stage.append("note " + position + "\n");
		stage.append(message + "\n");
		stage.append("end note\n");
	}

	/**
	 * @param stage
	 * @param step
	 */
	public static void addLine(StringBuilder stage, String step) {
		stage.append(step + "\n");
	}

	/**
	 * @param stage
	 * @param step
	 * @param type 
	 */
	public static void addCall(StringBuilder stage, String step, String type) {
		stage.append(":" + step + type + "\n");
	}

	/**
	 * @param stage
	 * @param expression
	 * @param value 
	 */
	public static void addIf(StringBuilder stage, String expression, String value) {
		stage.append("if(" + expression + ") then (" + value + ")\n");
	}

	/**
	 * @param stage
	 */
	public static void addEndIf(StringBuilder stage) {
		stage.append("endif\n");
	}

	/**
	 * @param stage
	 * @param expression
	 * @param value 
	 */
	public static void addElse(StringBuilder stage, String expression, String value) {
		stage.append("else (" + value + ")\n");
	}
}
