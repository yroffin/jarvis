package org.jarvis.main.engine.transform;

import org.jarvis.main.model.parser.IAimlCategory;

public interface IAimlScore extends Comparable<IAimlScore> {
	public int getKey();

	public IAimlCategory getValue();
}
