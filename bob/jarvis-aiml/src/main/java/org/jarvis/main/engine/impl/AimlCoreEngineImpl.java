/**
 *  Copyright 2012 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.main.engine.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.transform.IAimlScore;
import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.engine.transform.impl.AimlScoreImpl;
import org.jarvis.main.engine.transform.impl.AimlTranformImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.impl.AimlRepository;
import org.jarvis.main.model.transform.ITransformedItem;
import org.jarvis.main.model.transform.impl.TransformedItemImpl;
import org.jarvis.main.parser.AimlParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlCoreEngineImpl implements IAimlCoreEngine {

	protected Logger					logger		= LoggerFactory
															.getLogger(AimlCoreEngineImpl.class);

	private final IAimlRepository		aiml		= new AimlRepository();
	private final IAimlTransform		transformer	= new AimlTranformImpl();
	private final List<File>			resources	= new ArrayList<File>();
	private final Stack<IAimlCategory>	stack		= new Stack<IAimlCategory>();
	private final Stack<List<String>>	userInputs	= new Stack<List<String>>();

	@Override
	public Stack<List<String>> getUserInputs() {
		return userInputs;
	}

	private final Map<String, Object>	bot			= new HashMap<String, Object>();
	private final Map<String, Object>	properties	= new HashMap<String, Object>();

	@Override
	public Object getBot(String key) {
		return bot.get(key);
	}

	@Override
	public Object setBot(String key, Object value) {
		logger.info("set (bot)" + key + " to " + value);
		return bot.put(key, value);
	}

	@Override
	public Object get(String key) {
		return properties.get(key);
	}

	@Override
	public Object set(String key, Object value) {
		logger.info("set " + key + " to " + value);
		return properties.put(key, value);
	}

	/**
	 * last answer
	 */
	private String				that			= null;
	private ITransformedItem	thatTransformed	= new TransformedItemImpl("");

	@Override
	public void register(File resource) {
		resources.add(resource);
	}

	@Override
	public void parse() throws AimlParsingError {
		IAimlRepository result = null;
		for (File file : resources) {
			result = AimlParserImpl.parse(file);
			/**
			 * add all resources to current repository
			 */
			for (IAimlTopic topic : result.getTopics()) {
				aiml.addTopic(topic);
			}
			logger.info("Succesfull insert of " + result.getTopics().size()
					+ " topic(s)");
			for (IAimlCategory cat : result.getCategories()) {
				aiml.addCategory(cat);
			}
			logger.info("Succesfull insert of " + result.getCategories().size()
					+ " categorie(s)");
		}

		/**
		 * transformation on all category for optimisation during runtime
		 */
		for (IAimlCategory cat : aiml.getCategories()) {
			/**
			 * optimisation : transformation on categories only deal with the
			 * first sentence
			 */
			cat.setTransformedPattern(transformer.transform(
					cat.getPattern().getElements()).get(0));
			if (cat.hasThat()) {
				cat.setTransformedThat(transformer.transform(
						cat.getThat().getElements()).get(0));
			}
		}
	}

	@Override
	public List<String> ask(String sentence) throws AimlParsingError {
		List<String> inputs = new ArrayList<String>();
		List<String> answers = new ArrayList<String>();
		/**
		 * transform user sentence into common tranform state
		 */
		List<ITransformedItem> tx = transformer.transform(sentence);
		for (ITransformedItem s : tx) {
			answers.add(ask(s));
			inputs.add(s.getRaw());
		}
		/**
		 * log conversation
		 */
		getUserInputs().push(inputs);
		/**
		 * that become the last computed item
		 */
		if (logger.isDebugEnabled()) {
			logger.debug("That: " + that);
			logger.debug("That (tx): " + thatTransformed);
			logger.debug("Answer: " + answers);
			logger.debug("Inputs: " + getUserInputs());
		}
		that = answers.get(answers.size() - 1);
		List<ITransformedItem> txs = transformer.transform(that);
		thatTransformed = txs.get(txs.size() - 1);
		return answers;
	}

	private String ask(ITransformedItem sentence) throws AimlParsingError {
		IAimlScore found = null;
		/**
		 * find the category with pattern match algorithm based on scoring
		 */
		for (int iCat = 0; iCat < aiml.getCategories().size() && found == null; iCat++) {
			IAimlCategory cat = aiml.getCategories().get(iCat);
			/**
			 * ignore stacked category
			 */
			if (stack.contains(cat)) continue;

			ITransformedItem toScore = cat.getTransformedPattern();
			int score = sentence.score(toScore);
			/**
			 * handle that element - if score is ok - category have that element
			 * - current that is not null
			 */
			if (score >= 0) {
				if (cat.hasThat() && that != null) {
					int thatScore = thatTransformed.score(cat
							.getTransformedThat());
					if (thatScore > 0) {
						found = new AimlScoreImpl(score + thatScore, cat);
					}
				} else {
					found = new AimlScoreImpl(score, cat);
				}
			}
		}

		logger.info("Ask for : " + sentence);
		if (found != null) {
			IAimlCategory category = found.getValue();
			/**
			 * identify star value
			 */
			stack.push(category);
			String result = category
					.answer(this,
							sentence.star(category.getTransformedPattern(),
									new ArrayList<String>()), that,
							new StringBuilder()).toString();
			stack.pop();
			return result;
		} else {
			return "no anwser";
		}
	}
}
