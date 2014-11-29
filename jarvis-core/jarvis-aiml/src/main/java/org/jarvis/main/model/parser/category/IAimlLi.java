package org.jarvis.main.model.parser.category;

import org.jarvis.main.model.parser.IAimlElement;

public interface IAimlLi extends IAimlElement {

	public void setName(String name);

	public String getName();

	public void setValue(String value);

	public String getValue();

	public boolean isValueOnlyListItem();

	public boolean isDefaultListItem();

	public boolean isNameValueListItem();

}
