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
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.engine.transform.impl.AimlTranformImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.impl.AimlRepository;
import org.jarvis.main.parser.AimlParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlCoreEngineImpl implements IAimlCoreEngine {

	protected Logger				logger		= LoggerFactory
														.getLogger(AimlCoreEngineImpl.class);

	private final IAimlRepository	aiml		= new AimlRepository();
	private final IAimlTransform	transformer	= new AimlTranformImpl();
	private final List<File>		resources	= new ArrayList<File>();

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
		 * transformation
		 */
		for (IAimlCategory cat : result.getCategories()) {
			transformer.transform(cat.getPattern().getElements());
		}
	}

	@Override
	public String ask(String sentence) {
		logger.info("Ask for : " + sentence);
		return sentence;
	}
}
