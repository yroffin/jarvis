package models.profiling;

import org.jarvis.main.engine.ICategoryStack;

public class CategoryProfiler {
	private int level;
	private String category;
	private String result;

	public CategoryProfiler(ICategoryStack category) {
		this.level = category.getLevel();
		this.category = category.getCategory();
		this.result = category.getResult();
	}

	public int getLevel() {
		return level;
	}

	public String getCategory() {
		return category;
	}

	public String getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "CategoryProfiler [level=" + level + ", category=" + category
				+ ", result=" + result + "]";
	}
}
