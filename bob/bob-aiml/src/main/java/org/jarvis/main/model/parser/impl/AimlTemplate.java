package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlTemplate;

public class AimlTemplate implements IAimlTemplate, IAimlPcDataListener {
	
	@Override
	public void add(String value) {
		/**
		 * nothing todo with any PCDATA at top level
		 */
	}
}
