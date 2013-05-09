package org.jarvis.main.model.parser.template.impl;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The input element tells the AIML interpreter that it should substitute the contents of a previous user input.
 * 
 * The template-side input has an optional index attribute that may contain either a single integer or a comma-separated pair of integers. The minimum value for either of the integers in the index is "1". The index tells the AIML interpreter which previous user input should be returned (first dimension), and optionally which "sentence" (see [8.3.2.]) of the previous user input.
 * 
 * The AIML interpreter should raise an error if either of the specified index dimensions is invalid at run-time.
 * 
 * An unspecified index is the equivalent of "1,1". An unspecified second dimension of the index is the equivalent of specifying a "1" for the second dimension.
 * 
 * The input element does not have any content.
 *
 * <!-- Category: aiml-template-elements -->
 *
 * <aiml:input
 *   index = (single-integer-index | comma-separated-integer-pair) />
 */
public interface IAimlInput extends IAimlElement {

	void setIndex(String index);

}
