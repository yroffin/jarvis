package org.jarvis.main.model.impl.parser;

import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.IAimlXml;

/**
 * AIML xml extend
 */
public class AimlXmlImpl extends AimlElementContainer implements IAimlXml {

	/**
	 * constructor
	 */
	public AimlXmlImpl() {
		super("xml");
	}

	private String	version		= "1.0";
	private String	encoding	= "UTF8";

	@Override
	public void add(String value) {
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("version") == 0) version = accept(value);
		if (value.getKey().compareTo("encoding") == 0) encoding = accept(value);
	}

	@Override
	public String toString() {
		return "AimlXmlImpl [version=" + version + ", encoding=" + encoding
				+ "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append("<?xml version=\"" + version + "\" encoding=\""
				+ encoding + "\" ?>\n");
		return render;
	}
}
