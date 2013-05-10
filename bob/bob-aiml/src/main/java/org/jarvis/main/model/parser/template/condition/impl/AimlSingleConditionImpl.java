package org.jarvis.main.model.parser.template.condition.impl;

import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.condition.IAimlCondition;

/**
 * Single-predicate Condition
 * 
 * The singlePredicateCondition type of condition has a required attribute name,
 * which specifies an AIML predicate. This form of condition must contain at
 * least one li element. Zero or more of these li elements may be of the
 * valueOnlyListItem type. Zero or one of these li elements may be of the
 * defaultListItem type.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:condition xsi:type = "singlePredicateCondition" name =
 * aiml-predicate-name >
 * 
 * <!-- Contents: value-only-list-item*, default-list-item{0,1} -->
 * 
 * </aiml:condition>
 * 
 * The singlePredicateCondition type of condition is processed as follows:
 * 
 * Reading each contained li in order:
 * 
 * If the li is a valueOnlyListItem type, then compare the contents of the value
 * attribute of the li with the value of the predicate specified by the name
 * attribute of the enclosing condition. If they match, then return the contents
 * of the li and stop processing this condition. If they do not match, continue
 * processing the condition. If the li is a defaultListItem type, then return
 * the contents of the li and stop processing this condition.
 */
public class AimlSingleConditionImpl extends AimlElementContainer implements
		IAimlCondition {

	public AimlSingleConditionImpl() {
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
