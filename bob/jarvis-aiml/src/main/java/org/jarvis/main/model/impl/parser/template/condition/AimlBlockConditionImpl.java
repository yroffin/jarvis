package org.jarvis.main.model.impl.parser.template.condition;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.template.condition.IAimlCondition;

/**
 * Block Condition
 * 
 * The blockCondition type of condition has a required attribute name, which
 * specifies an AIML predicate, and a required attribute value, which contains a
 * simple pattern expression.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:condition xsi:type = "blockCondition" name = aiml-predicate-name value
 * = aiml-simple-pattern-expression >
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:condition>
 * 
 * If the contents of the value attribute match the value of the predicate
 * specified by name, then the AIML interpreter should return the contents of
 * the condition. If not, the empty string "" should be returned.
 */
public class AimlBlockConditionImpl extends AimlElementContainer implements
		IAimlCondition {

	public AimlBlockConditionImpl() {
		super("condition");
	}

	private String	name;
	private String	value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void add(IAimlProperty p) {
		if (p.getKey().compareTo("name") == 0) name = accept(p);
		if (p.getKey().compareTo("value") == 0) value = accept(p);
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCondition [elements=" + elements + "]";
	}
}
