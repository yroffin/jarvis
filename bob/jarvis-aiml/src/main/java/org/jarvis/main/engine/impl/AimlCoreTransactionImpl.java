package org.jarvis.main.engine.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.jarvis.main.engine.IAimlCoreTransaction;
import org.jarvis.main.engine.ICategoryStack;
import org.jarvis.main.model.parser.IAimlCategory;

public class AimlCoreTransactionImpl implements IAimlCoreTransaction {
	
	List<String> messages = new ArrayList<String>();

	@Override
	public void message(String message) {
		messages.add(message);		
	}

	@Override
	public List<String> getMessages() {
		return messages;
	}

	private final Stack<IAimlCategory> stack = new Stack<IAimlCategory>();
	private final List<ICategoryStack> categories = new ArrayList<ICategoryStack>();

	@Override
	public void push(IAimlCategory category) {
		stack.push(category);
	}

	@Override
	public void pop(String result) {
		IAimlCategory categorie = stack.pop();
		categories.add(new AimlCategoryStackImpl(stack.size(),categorie,result));
	}

	class CategoryStackComparator implements Comparator<ICategoryStack> {
		@Override
		public int compare(ICategoryStack o1, ICategoryStack o2) {
			return o1.getLevel() - o2.getLevel();
		}
	}

	@Override
	public List<ICategoryStack> getCategories() {
		Collections.sort(categories,new CategoryStackComparator());
		return categories;
	}
}
