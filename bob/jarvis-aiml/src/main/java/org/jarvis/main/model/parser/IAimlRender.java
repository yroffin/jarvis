package org.jarvis.main.model.parser;

public interface IAimlRender {
	public String unescapedHtml();
	public StringBuilder toAiml(StringBuilder render);
	public String escapedHtml();
}
