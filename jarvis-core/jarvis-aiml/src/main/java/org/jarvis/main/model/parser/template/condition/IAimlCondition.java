/**
 *  Copyright 2012 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.jarvis.main.model.parser.template.condition;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The condition element instructs the AIML interpreter to return specified
 * contents depending upon the results of matching a predicate against a
 * pattern.
 * 
 * NB: The condition element has three different types. The three different
 * types specified here are distinguished by an xsi:type attribute, which
 * permits a validating XML Schema processor to validate them. Two of the types
 * may contain li elements, of which there are three different types, whose
 * validity is determined by the type of enclosing condition. In practice, an
 * AIML interpreter may allow the omission of the xsi:type attribute and may
 * instead heuristically determine which type of condition (and hence li) is in
 * use.
 * 
 * Block Condition ...
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
 * 
 * Condition List Items ...
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
 * 
 * Single-predicate Condition ...
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
 * 
 * Multi-predicate Condition ...
 * 
 * The multiPredicateCondition type of condition has no attributes. This form of
 * condition must contain at least one li element. Zero or more of these li
 * elements may be of the nameValueListItem type. Zero or one of these li
 * elements may be of the defaultListItem type.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:condition xsi:type = "multiPredicateCondition">
 * 
 * <!-- Contents: name-value-list-item*, default-list-item{0,1} -->
 * 
 * </aiml:condition>
 * 
 * The multiPredicateCondition type of condition is processed as follows:
 * 
 * Reading each contained li in order:
 * 
 * If the li is a nameValueListItem type, then compare the contents of the value
 * attribute of the li with the value of the predicate specified by the name
 * attribute of the li. If they match, then return the contents of the li and
 * stop processing this condition. If they do not match, continue processing the
 * condition. If the li is a defaultListItem type, then return the contents of
 * the li and stop processing this condition.
 * 
 */
public interface IAimlCondition extends IAimlElement {

}
