package org.jarvis.main.model.parser.template.impl;

import java.util.Random;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlRandom;

public class AimlRandomImpl extends AimlElementContainer implements IAimlRandom {

	private static Random	randomGenerator	= new Random();

	public AimlRandomImpl() {
		super("random");
	}

	@Override
	public void add(String value) {
		/**
		 * The random element must contain one or more li elements of type
		 * defaultListItem, and cannot contain any other elements.
		 * 
		 * PCDATA is ignored
		 */
	}

	@Override
	public void answer(String star, String that, StringBuilder render) {
		/**
		 * randomize li selection and render it
		 */
		int li = randomGenerator.nextInt(1000000) % (getElements().size());
		getElements().get(li).answer(star, that, render);
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
