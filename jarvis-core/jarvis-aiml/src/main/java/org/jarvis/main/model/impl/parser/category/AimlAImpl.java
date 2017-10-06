package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlA;

/**
 * AimlAImpl
 */
public class AimlAImpl extends AimlElementContainer implements IAimlA {

	/**
	 * AimlAImpl
	 */
	public AimlAImpl() {
		super("a");
	}

	private String href;

	/**
	 * @return String
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 */
	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public void add(IAimlProperty value) {
		if(value.getKey().compareTo("href")==0) href = accept(value);		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlA [elements=" + elements + "]";
	}
}
