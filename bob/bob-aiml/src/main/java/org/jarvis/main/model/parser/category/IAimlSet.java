package org.jarvis.main.model.parser.category;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlPcDataListener;

public interface IAimlSet extends IAimlElement, IAimlPcDataListener {
	public void setName(String name);
}
