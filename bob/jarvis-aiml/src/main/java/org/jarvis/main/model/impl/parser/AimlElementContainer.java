package org.jarvis.main.model.impl.parser;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.transform.AimlTranformImpl;
import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.transform.ITransformedItem;

public abstract class AimlElementContainer implements IAimlElement {
	protected List<IAimlProperty> props = new ArrayList<IAimlProperty>();
	protected List<IAimlElement> elements = new ArrayList<IAimlElement>();
	private final IAimlTransform transformer = new AimlTranformImpl();

	protected String tag;
	private List<ITransformedItem> cache = null;

	public AimlElementContainer(String tag) {
		this.tag = tag;
	}

	@Override
	public List<IAimlElement> getElements() {
		return elements;
	}

	@Override
	public void add(String value) {
		elements.add(new AimlDataImpl(value, true));
	}

	@Override
	public List<ITransformedItem> getTransforms() throws AimlParsingError {
		if (cache != null) return cache;
		cache = transformer.transform(elements);
		return cache;
	}

	@Override
	public void add(IAimlElement value) {
		elements.add(value);
	}

	@Override
	public void add(IAimlProperty value) {
		/**
		 * no property by default
		 * 
		 * @return
		 */
	}

	protected String accept(IAimlProperty e) {
		props.add(e);
		return e.getValue();
	}

	@Override
	public String get(String key) {
		for (IAimlProperty p : props) {
			if (p.getKey().compareTo(key) == 0) return p.getValue();
		}
		return null;
	}

	protected StringBuilder properties(StringBuilder render) {
		for (IAimlProperty e : props) {
			render.append(" " + e.getKey() + "=\"" + e.getValue() + "\"");
		}
		return render;
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) throws AimlParsingError {
		for (IAimlElement element : elements) {
			element.answer(engine, star, that, render);
		}
		return render;
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if (elements.size() == 0) {
			render.append("<" + tag);
			properties(render);
			render.append("/>");
		} else {
			render.append("<" + tag);
			properties(render);
			render.append(">");
			for (IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</" + tag + ">");
		}
		return render;
	}
}
