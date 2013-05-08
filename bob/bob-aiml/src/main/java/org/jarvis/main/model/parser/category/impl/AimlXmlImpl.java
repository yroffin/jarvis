package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlXml;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.impl.AimlProperty;

public class AimlXmlImpl extends AimlElementContainer implements IAimlXml {

	private String version;
	private String encoding;

	@Override
	public void add(String value) {
	}

	@Override
	public void add(AimlProperty value) {
		if(value.getKey().compareTo("version")==0) version = value.getValue();		
		if(value.getKey().compareTo("encoding")==0) encoding = value.getValue();		
	}

	@Override
	public String toString() {
		return "AimlXmlImpl [version=" + version + ", encoding=" + encoding
				+ "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\" ?>\n");
		return render;
	}
}
