package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlLi;

public class AimlLiImpl extends AimlElementContainer implements IAimlLi {

	public AimlLiImpl() {
		super("li");
	}

	private String name;
	private String value;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isValueOnlyListItem() {
		return name == null && value != null;
	}

	@Override
	public boolean isNameValueListItem() {
		return name != null && value != null;
	}

	@Override
	public boolean isDefaultListItem() {
		return name == null && value == null;
	}

	@Override
	public void add(IAimlProperty v) {
		if (v.getKey().compareTo("name") == 0) name = accept(v);
		if (v.getKey().compareTo("value") == 0) value = accept(v);
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlLiImpl [name=" + name + ", value=" + value + "]";
	}
}
