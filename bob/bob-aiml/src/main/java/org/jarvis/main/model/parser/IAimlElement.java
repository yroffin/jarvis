package org.jarvis.main.model.parser;

public interface IAimlElement extends IAimlRender {
	public void add(String value);
	public void add(IAimlElement value);
	public void add(IAimlProperty value);
	public String get(String key);
	public StringBuilder toAiml(StringBuilder render);

}
