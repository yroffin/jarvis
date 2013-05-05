package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlPattern;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlTemplate;

public class AimlCategory implements IAimlCategory, IAimlPcDataListener {

	IAimlTemplate template;
	IAimlPattern pattern;
	
	@Override
	public void setTemplate(IAimlTemplate e) {
		template = e;		
	}

	@Override
	public void setPattern(IAimlPattern e) {
		pattern = e;
	}

	@Override
	public String toString() {
		return "AimlCategory [template=" + template + ", pattern=" + pattern
				+ "]";
	}

	@Override
	public void add(String value) {
		/**
		 * nothing todo with any PCDATA at top level
		 */
	}

}
