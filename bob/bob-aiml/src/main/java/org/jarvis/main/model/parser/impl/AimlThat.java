package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlCategoryElement;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlThat;

public class AimlThat implements IAimlThat, IAimlCategoryElement, IAimlPcDataListener {

	@Override
	public String toString() {
		return "AimlThat []";
	}

	@Override
	public void add(String value) {
		/**
		 * nothing todo with any PCDATA at top level
		 */
	}

}
