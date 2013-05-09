package org.jarvis.main.model.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlProperty;

public abstract class AimlElementContainer implements IAimlElement {
	protected List<IAimlProperty> props   = new ArrayList<IAimlProperty>();
	protected List<IAimlElement> elements = new ArrayList<IAimlElement>();
	protected String tag;

	public AimlElementContainer(String tag) {
		this.tag = tag;
	}

	@Override
	public void add(String value) {
		elements.add(new AimlData(value));
	}

	@Override
	public void add(IAimlElement value) {
		elements.add(value);
	}

	@Override
	public void add(IAimlProperty value) {
		/**
		 * no property by default
		 * @return 
		 */
	}

	protected String accept(IAimlProperty e) {
		props.add(e);
		return e.getValue();
	}

	public String get(String key) {
		for(IAimlProperty p : props) {
			if(p.getKey().compareTo(key) == 0) return p.getValue();
		}
		return null;
	}

	protected StringBuilder properties(StringBuilder render) {
		for(IAimlProperty e : props) {
			render.append(" " + e.getKey() + "=\"" + e.getValue() + "\"");
		}
		return render;
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<" + tag);
			properties(render);
			render.append("/>");
		} else {
			render.append("<" + tag);
			properties(render);
			render.append(">");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</" + tag + ">");
		}
		return render;
	}
}
