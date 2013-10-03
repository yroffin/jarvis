package org.jarvis.main.engine.impl;

import org.jarvis.main.engine.ICategoryStack;
import org.jarvis.main.model.parser.IAimlCategory;

public class AimlCategoryStackImpl implements ICategoryStack {

	int level = 0;
	IAimlCategory category = null;
	String result;

	public AimlCategoryStackImpl(int level, IAimlCategory category, String result) {
		this.level = level;
		this.category = category;
		this.result = result;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public String getCategory() {
		StringBuilder sb = category.toAiml(new StringBuilder());
		return sb.toString();
	}

	@Override
	public String getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "CategoryStackImpl [\nlevel=" + level + ", \ncategory=" + getCategory()
				+ ", \nresult=" + result + "]";
	}
}
