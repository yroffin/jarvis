package org.jarvis.main.model.parser.category;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlRender;

public interface IAimlInput extends IAimlElement, IAimlPcDataListener, IAimlRender {

	void setIndex(String index);

}
