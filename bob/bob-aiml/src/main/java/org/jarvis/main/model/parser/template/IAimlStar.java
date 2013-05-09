package org.jarvis.main.model.parser.template;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The star element indicates that an AIML interpreter should substitute the value "captured" by a particular wildcard from the pattern-specified portion of the match path when returning the template.
 *
 * The star element has an optional integer index attribute that indicates which wildcard to use. The minimum acceptable value for the index is "1" (the first wildcard), and the maximum acceptable value is equal to the number of wildcards in the pattern.
 *
 * An AIML interpreter should raise an error if the index attribute of a star specifies a wildcard that does not exist in the category element's pattern. Not specifying the index is the same as specifying an index of "1".
 *
 * The star element does not have any content.
 *
 * <!-- Category: aiml-template-elements -->
 *
 * <aiml:star
 *    index = single-integer-index />
 */
public interface IAimlStar extends IAimlElement {
}
