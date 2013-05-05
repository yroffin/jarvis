package org.jarvis.main.model.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlTopic;

public class AimlRepository implements IAimlRepository, IAimlPcDataListener {

	private List<IAimlTopic> topics = new ArrayList<IAimlTopic>();
	private List<IAimlCategory> categories = new ArrayList<IAimlCategory>();

	@Override
	public void addCategory(IAimlCategory e) {
		categories.add(e);
	}

	@Override
	public void addTopic(IAimlTopic e) {
		topics.add(e);
	}

	@Override
	public String toString() {
		return "AimlRepository [topics=" + topics + ", categories="
				+ categories + "]";
	}

	@Override
	public void add(String value) {
		/**
		 * nothing todo with any PCDATA at top level
		 */
	}

}
