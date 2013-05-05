package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlPattern;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlPattern implements IAimlPattern, IAimlPcDataListener {
	Logger logger = LoggerFactory.getLogger(AimlPattern.class);

	@Override
	public void add(String value) {
		logger.info("PCDATA: " + value.replace("\n", ""));
	}
}
