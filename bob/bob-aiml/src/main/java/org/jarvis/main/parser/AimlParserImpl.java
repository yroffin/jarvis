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

package org.jarvis.main.parser;

import java.io.File;
import java.util.Stack;

import org.antlr.v4.runtime.RecognitionException;
import org.jarvis.main.antlr4.aimlParser;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.impl.AimlCategory;
import org.jarvis.main.model.parser.impl.AimlPattern;
import org.jarvis.main.model.parser.impl.AimlRepository;
import org.jarvis.main.model.parser.impl.AimlTemplate;
import org.jarvis.main.model.parser.impl.AimlTopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlParserImpl extends aimlParser {
	Logger logger = LoggerFactory.getLogger(AimlParserImpl.class);

	protected AimlLexerImpl lexer = null;
	
	/**
	 * AIML model
	 */
	private IAimlRepository repository = null;

	private IAimlTopic currentTopic;
	private IAimlCategory currentCategory;
	private AimlTemplate currentTemplate;
	private AimlPattern currentPattern;

	Stack<IAimlPcDataListener> pcdata = new Stack<IAimlPcDataListener>();

	/**
	 * default construtor
	 * @param filename
	 * @throws AimlParsingError
	 */
	public AimlParserImpl(File filename) throws AimlParsingError {
		super(AimlLexerImpl.getTokens(filename.getAbsolutePath()));
		lexer = (AimlLexerImpl) _input.getTokenSource();
	}

	/**
	 * parse this document
	 * @return
	 * @throws AimlParsingError
	 */
	public IAimlRepository parse() throws AimlParsingError {
		try {
			document();
			return repository;
		} catch (RecognitionException e) {
			throw new AimlParsingError(e);
		}
	}

	@Override
	public void onPcData(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("onPcData - [" + value + "]");
		}
		/**
		 * register this new PCDATA element
		 * to current receiver
		 */
		pcdata.lastElement().add(value);
	}

	@Override
	public void onOpenTag(String value) {
		switch(decode(value)) {
			case AIML:
				handleOpenTagAiml(value);
				break;
			case TOPIC:
				handleOpenTagTopic(value);
				break;
			case CATEGORY:
				handleOpenTagCategory(value);
				break;
			case TEMPLATE:
				handleOpenTagTemplate(value);
				break;
			case PATTERN:
				handleOpenTagPattern(value);
				break;
		default:
			break;
		}
	}

	@Override
	public void onCloseTag(String value) {
		switch(decode(value)) {
			case AIML:
				handleCloseTagAiml(value);
				break;
			case TOPIC:
				handleCloseTagTopic(value);
				break;
			case CATEGORY:
				handleCloseTagCategory(value);
				break;
			case TEMPLATE:
				handleCloseTagTemplate(value);
				break;
			case PATTERN:
				handleCloseTagPattern(value);
				break;
		default:
			break;
		}
	}

	private void handleOpenTagAiml(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleOpenTagAiml - " + value);
		}
		/**
		 * create default repository on aiml tag detection
		 * attributes must be handle next 
		 */
		AimlRepository local = new AimlRepository();
		repository = local;
		pcdata.push(local);
	}

	private void handleOpenTagTopic(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleOpenTagTopic - " + value);
		}
		AimlTopic local = new AimlTopic();
		currentTopic = local;
		repository.addTopic(currentTopic);
		pcdata.push(local);
	}

	private void handleOpenTagCategory(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleOpenTagCategory - " + value);
		}
		AimlCategory local = new AimlCategory();
		currentCategory = local;
		if(currentTopic != null) {
			currentTopic.addCategory(currentCategory);
		} else {
			/**
			 * just add on top level if no topic
			 * is opened
			 */
			repository.addCategory(currentCategory);
		}
		pcdata.push(local);
	}

	private void handleOpenTagTemplate(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleOpenTagTemplate - " + value);
		}
		AimlTemplate local = new AimlTemplate();
		currentTemplate = local;
		if(currentCategory != null) {
			currentCategory.setTemplate(currentTemplate);
		}
		pcdata.push(local);
	}

	private void handleOpenTagPattern(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleOpenTagPattern - " + value);
		}
		AimlPattern local = new AimlPattern();
		currentPattern = local;
		if(currentCategory != null) {
			currentCategory.setPattern(currentPattern);
		}
		pcdata.push(local);
	}

	/**
	 * close aiml tag
	 * @param value
	 */
	private void handleCloseTagAiml(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleCloseTagAiml - " + value);
		}
		pcdata.pop();
	}

	private void handleCloseTagTopic(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleCloseTagTopic - " + value);
		}
		currentTopic = null;
		pcdata.pop();
	}

	private void handleCloseTagCategory(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleCloseTagCategory - " + value);
		}
		currentCategory = null;
		pcdata.pop();
	}

	private void handleCloseTagTemplate(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleCloseTagTemplate - " + value);
		}
		currentTemplate = null;
		pcdata.pop();
	}

	private void handleCloseTagPattern(String value) {
		if(logger.isDebugEnabled()) {
			logger.debug("handleCloseTagPattern - " + value);
		}
		currentPattern = null;
		pcdata.pop();
	}
}
