package org.jarvis.main.model.parser.template.condition.impl;

import org.jarvis.main.model.parser.category.IAimlLi;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

/**
 * Condition List Items
 * 
 * As described above, two types of condition may contain li elements. There are
 * three types of li elements. The type of li element allowed in a given
 * condition depends upon the type of that condition, as described above.
 * 
 * a. Default List Items
 * 
 * An li element of the type defaultListItem has no attributes. It may contain
 * any AIML template elements.
 * 
 * <!-- Category: condition-list-item -->
 * 
 * <aiml:li xsi:type = "defaultListItem">
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:li>
 * 
 * b. Value-only List Items
 * 
 * An li element of the type valueOnlyListItem has a required attribute value,
 * which must contain a simple pattern expression. The element may contain any
 * AIML template elements.
 * 
 * <!-- Category: condition-list-item -->
 * 
 * <aiml:li xsi:type = "valueOnlyListItem" value =
 * aiml-simple-pattern-expression >
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:li>
 * 
 * c. Name and Value List Items
 * 
 * An li element of the type nameValueListItem has a required attribute name,
 * which specifies an AIML predicate, and a required attribute value, which
 * contains a simple pattern expression. The element may contain any AIML
 * template elements.
 * 
 * <!-- Category: condition-list-item -->
 * 
 * <aiml:li xsi:type = "nameValueListItem" name = aiml-predicate-name value =
 * aiml-simple-pattern-expression >
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:li>
 */
public class AimlListItemConditionImpl extends AimlElementContainer implements
		IAimlLi {

	public AimlListItemConditionImpl() {
		super("li");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlListItemCondition [elements=" + elements + "]";
	}
}
