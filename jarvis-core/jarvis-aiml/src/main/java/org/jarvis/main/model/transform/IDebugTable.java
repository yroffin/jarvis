package org.jarvis.main.model.transform;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jarvis.main.engine.transform.IAimlScore;
import org.jarvis.main.model.parser.IAimlCategory;

public interface IDebugTable {

	void addCategories(List<IAimlCategory> availableCategories,
			ITransformedItem sentence, String object);

	void addFound(String sessiondIdText, String catXmlValue, int score,
			ITransformedItem toScore, IAimlScore found, IAimlScore old);

	File store() throws IOException;

}
