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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.jarvis.main.antlr4.aimlParser;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlCategoryElement;
import org.jarvis.main.model.parser.IAimlGet;
import org.jarvis.main.model.parser.IAimlPattern;
import org.jarvis.main.model.parser.IAimlPcDataListener;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlSrai;
import org.jarvis.main.model.parser.IAimlTemplate;
import org.jarvis.main.model.parser.IAimlThat;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.impl.AimlCategory;
import org.jarvis.main.model.parser.impl.AimlGet;
import org.jarvis.main.model.parser.impl.AimlPattern;
import org.jarvis.main.model.parser.impl.AimlProperty;
import org.jarvis.main.model.parser.impl.AimlRepository;
import org.jarvis.main.model.parser.impl.AimlSrai;
import org.jarvis.main.model.parser.impl.AimlTemplate;
import org.jarvis.main.model.parser.impl.AimlThat;
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

	public IAimlRepository getRepository() {
		return repository;
	}

	private IAimlTopic currentTopic;
	private IAimlCategory currentCategory;
	private IAimlTemplate currentTemplate;
	private IAimlPattern currentPattern;
	private IAimlThat currentThat;
	private IAimlSrai currentSrai;
	private IAimlGet currentGet;

	private List<AimlProperty> currentAttributes = new ArrayList<AimlProperty>();

	Stack<IAimlPcDataListener> pcdata = new Stack<IAimlPcDataListener>();

	private static boolean debugParsing = false;

	/**
	 * default construtor
	 * @param filename
	 * @param encoding 
	 * @throws AimlParsingError
	 */
	private AimlParserImpl(File filename, String encoding) throws AimlParsingError {
		super(AimlLexerImpl.getTokens(filename.getAbsolutePath(), encoding));
		lexer = (AimlLexerImpl) _input.getTokenSource();
	}

	/**
	 * another default constructor
	 * @param local
	 */
	private AimlParserImpl(CommonTokenStream local) {
		super(local);
	}

	/**
	 * parse this document
	 * @return
	 * @throws AimlParsingError
	 */
	public static IAimlRepository parse(File filename) throws AimlParsingError {
		try {
			/**
			 * find encoding
			 */
			String encoding = "UTF8";
			try {
				Scanner scan = new Scanner(filename);
				CommonTokenStream local = AimlLexerImpl.getTokensFromString(scan.nextLine());
				scan.close();
				AimlParserImpl parser = new AimlParserImpl(local);
				parser.header();
				for(AimlProperty p : parser.currentAttributes) {
					if("encoding".compareTo(p.getKey()) == 0) {
						encoding = p.getValue();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new AimlParsingError(e);
			}
			/**
			 * parse all file
			 */
			AimlParserImpl parser = new AimlParserImpl(filename, encoding);
			parser.document();
			return parser.getRepository();
		} catch (RecognitionException e) {
			throw new AimlParsingError(e);
		}
	}

	@Override
	public void onAttribute(String key, String value) {
		if(logger.isDebugEnabled() && debugParsing) {
			logger.debug("onAttribute - [" + key + " = " + value + "]");
		}
		currentAttributes.add(new AimlProperty(key, value));
	}

	@Override
	public void onPcData(String value) {
		if(logger.isDebugEnabled() && debugParsing) {
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
		if(logger.isDebugEnabled() && debugParsing) {
			logger.debug("onOpenTag - " + value);
		}
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
			case THAT:
				handleOpenTagThat(value);
				break;
			case SRAI:
				handleOpenTagSrai(value);
				break;
			case GET:
				handleOpenTagGet(value);
				break;
		default:
			// logger.warn("Unknown tag element : " + value);
			break;
		}
	}

	@Override
	public void onCloseTag(String value) {
		if(logger.isDebugEnabled() && debugParsing) {
			logger.debug("onCloseTag - " + value);
		}
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
			case THAT:
				handleCloseTagThat(value);
				break;
			case SRAI:
				handleCloseTagSrai(value);
				break;
			case GET:
				handleCloseTagGet(value);
				break;
		default:
			break;
		}
		/**
		 * attributes parsing is clear
		 * on tag close
		 */
		currentAttributes.clear();
	}

	/**
	 * An AIML object is represented by an aiml:aiml element in an XML document.
	 * The aiml:aiml element may contain the following types of elements:
	 * - aiml:topic
	 * - aiml:category
	 * @param tagname
	 */
	private void handleOpenTagAiml(String tagname) {
		/**
		 * create default repository on aiml tag detection
		 * attributes must be handle next 
		 */
		AimlRepository local = new AimlRepository();
		repository = local;
		pcdata.push(local);
	}

	/**
	 * A topic is an optional top-level element that contains category elements.
	 * A topic element has a required name attribute that must contain a simple pattern expression.
	 * A topic element may contain one or more category elements.
	 * @param tagname
	 */
	private void handleOpenTagTopic(String tagname) {
		AimlTopic local = new AimlTopic();
		currentTopic = local;
		repository.addTopic(currentTopic);
		pcdata.push(local);
	}

	/**
	 * A category is a top-level (or second-level, if contained within a topic) element that contains exactly one pattern and exactly one template.
	 * A category does not have any attributes.
	 * @param value
	 */
	private void handleOpenTagCategory(String value) {
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

	/**
	 * A pattern is an element whose content is a mixed pattern expression. Exactly one pattern must appear in each category. The pattern must always be the first child element of the category.
	 * A pattern does not have any attributes.
	 * @param value
	 */
	private void handleOpenTagPattern(String value) {
		AimlPattern local = new AimlPattern();
		currentPattern = local;
		if(currentCategory != null) {
			currentCategory.setPattern(currentPattern);
		}
		pcdata.push(local);
	}

	/**
	 * The pattern-side that element is a special type of pattern element used for context matching. The pattern-side that is optional in a category, but if it occurs it must occur no more than once, and must immediately follow the pattern and immediately precede the template.
	 * A pattern-side that element contains a simple pattern expression.
	 * @param value
	 */
	private void handleOpenTagThat(String value) {
		AimlThat local = new AimlThat();
		IAimlCategoryElement element = local;
		currentThat = local;
		if(currentCategory != null) {
			currentCategory.add(element);
		}
		pcdata.push(currentThat);
	}

	/**
	 * The srai element instructs the AIML interpreter to pass the result of processing the contents of the srai element to the AIML matching loop,
	 * as if the input had been produced by the user (this includes stepping through the entire input normalization process). The srai element does not have any attributes.
	 * It may contain any AIML template elements.
	 * @param value
	 */
	private void handleOpenTagSrai(String value) {
		AimlSrai local = new AimlSrai();
		IAimlCategoryElement element = local;
		currentSrai = local;
		if(currentCategory != null) {
			currentCategory.add(element);
		}
		pcdata.push(currentSrai);
	}

	/**
	 * A template is an element that appears within category elements.
	 * The template must follow the pattern-side that element, if it exists; otherwise, it follows the pattern element.
	 * A template does not have any attributes.
	 * @param value
	 */
	private void handleOpenTagTemplate(String value) {
		AimlTemplate local = new AimlTemplate();
		currentTemplate = local;
		if(currentCategory != null) {
			currentCategory.setTemplate(currentTemplate);
		}
		pcdata.push(local);
	}

	private void handleOpenTagGet(String value) {
		AimlGet local = new AimlGet();
		currentGet = local;
		if(currentTemplate != null) {
			currentTemplate.add(currentGet);
		}
		pcdata.push(local);
	}

	/**
	 * close aiml tag
	 * @param value
	 */
	private void handleCloseTagAiml(String value) {
		pcdata.pop();
	}

	private void handleCloseTagTopic(String value) {
		currentTopic = null;
		pcdata.pop();
	}

	private void handleCloseTagCategory(String value) {
		currentCategory = null;
		pcdata.pop();
	}

	private void handleCloseTagPattern(String value) {
		currentPattern = null;
		pcdata.pop();
	}

	private void handleCloseTagThat(String value) {
		currentThat = null;
		pcdata.pop();
	}

	private void handleCloseTagSrai(String value) {
		currentSrai = null;
		pcdata.pop();
	}

	private void handleCloseTagTemplate(String value) {
		currentTemplate = null;
		pcdata.pop();
	}

	private void handleCloseTagGet(String value) {
		/**
		 * attributes resolution
		 */
		currentGet.setName(findKeyInAttributes("name"));
		currentGet = null;
		pcdata.pop();
	}

	/**
	 * utility
	 * @param key
	 * @return
	 */
	private String findKeyInAttributes(String key) {
		for(AimlProperty element : currentAttributes) {
			if(element.getKey().compareTo(key)==0) {
				return element.getValue();
			}
		}
		return null;
	}
}
