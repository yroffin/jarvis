package models.profiling;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jarvis.main.engine.ICategoryStack;

public class TransactionProfiler {
	@JsonProperty("categories")
	List<CategoryProfiler> categories = new ArrayList<CategoryProfiler>();

	public void add(ICategoryStack category) {
		categories.add(new CategoryProfiler(category));		
	}

	@Override
	public String toString() {
		return "TransactionProfiler [categories=" + categories + "]";
	}
}
