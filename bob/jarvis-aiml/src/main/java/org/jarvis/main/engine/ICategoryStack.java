package org.jarvis.main.engine;

import org.jarvis.main.model.parser.IAimlResult;

public interface ICategoryStack {

	int getLevel();

	String getCategory();

	IAimlResult getResult();

}
