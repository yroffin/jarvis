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
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.jarvis.main.antlr4.aimlParser;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlGet;
import org.jarvis.main.model.parser.IAimlPattern;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlSrai;
import org.jarvis.main.model.parser.IAimlTemplate;
import org.jarvis.main.model.parser.IAimlThat;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.IAimlXml;
import org.jarvis.main.model.parser.category.IAimlA;
import org.jarvis.main.model.parser.category.IAimlBot;
import org.jarvis.main.model.parser.category.IAimlBr;
import org.jarvis.main.model.parser.category.IAimlCondition;
import org.jarvis.main.model.parser.category.IAimlFormal;
import org.jarvis.main.model.parser.category.IAimlId;
import org.jarvis.main.model.parser.category.IAimlInput;
import org.jarvis.main.model.parser.category.IAimlLi;
import org.jarvis.main.model.parser.category.IAimlPerson;
import org.jarvis.main.model.parser.category.IAimlPerson2;
import org.jarvis.main.model.parser.category.IAimlRandom;
import org.jarvis.main.model.parser.category.IAimlSet;
import org.jarvis.main.model.parser.category.IAimlStar;
import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.category.IAimlVersion;
import org.jarvis.main.model.parser.category.impl.AimlAImpl;
import org.jarvis.main.model.parser.category.impl.AimlBotImpl;
import org.jarvis.main.model.parser.category.impl.AimlBrImpl;
import org.jarvis.main.model.parser.category.impl.AimlConditionImpl;
import org.jarvis.main.model.parser.category.impl.AimlFormalImpl;
import org.jarvis.main.model.parser.category.impl.AimlInputImpl;
import org.jarvis.main.model.parser.category.impl.AimlIdImpl;
import org.jarvis.main.model.parser.category.impl.AimlLiImpl;
import org.jarvis.main.model.parser.category.impl.AimlPerson2Impl;
import org.jarvis.main.model.parser.category.impl.AimlPersonImpl;
import org.jarvis.main.model.parser.category.impl.AimlRandomImpl;
import org.jarvis.main.model.parser.category.impl.AimlSetImpl;
import org.jarvis.main.model.parser.category.impl.AimlStarImpl;
import org.jarvis.main.model.parser.category.impl.AimlThinkImpl;
import org.jarvis.main.model.parser.category.impl.AimlVersionImpl;
import org.jarvis.main.model.parser.category.impl.AimlXmlImpl;
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
	private IAimlXml root = null;
	private IAimlRepository repository = null;

	public IAimlRepository getRepository() {
		return repository;
	}

	private class StackAiml {
		private Stack<IAimlElement> stack = new Stack<IAimlElement>();

		public IAimlTopic findTopic() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlTopic.class)
					return (IAimlTopic) stack.get(index);
			}
			return null;
		}

		public IAimlCategory findCategory() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlCategory.class)
					return (IAimlCategory) stack.get(index);
			}
			return null;
		}

		public IAimlTemplate findTemplate() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlTemplate.class)
					return (IAimlTemplate) stack.get(index);
			}
			return null;
		}

		public IAimlRandom findRandom() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlRandomImpl.class)
					return (IAimlRandom) stack.get(index);
			}
			return null;
		}

		public IAimlFormal findFormal() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlFormalImpl.class)
					return (IAimlFormal) stack.get(index);
			}
			return null;
		}

		public IAimlThink findThink() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlThinkImpl.class)
					return (IAimlThink) stack.get(index);
			}
			return null;
		}

		public IAimlGet findGet() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlGet.class)
					return (IAimlGet) stack.get(index);
			}
			return null;
		}

		public IAimlInput findInput() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlInputImpl.class)
					return (IAimlInput) stack.get(index);
			}
			return null;
		}

		public IAimlSet findSet() {
			for (int index = stack.size() - 1; index >= 0; index--) {
				if (stack.get(index).getClass() == AimlSetImpl.class)
					return (IAimlSet) stack.get(index);
			}
			return null;
		}

		public IAimlElement pop() {
			return stack.pop();
		}

		public IAimlRepository push(AimlRepository e) {
			stack.push(e);
			return e;
		}

		public IAimlTopic pushTopic() {
			AimlTopic e = new AimlTopic();
			repository.addTopic(e);
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlCategory pushCategory() {
			AimlCategory e = new AimlCategory();
			repository.addCategory(e);
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlTemplate pushTemplate() {
			AimlTemplate e = new AimlTemplate();
			findCategory().setTemplate(e);
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlPattern pushPattern() {
			AimlPattern e = new AimlPattern();
			findCategory().setPattern(e);
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlThat pushThat() {
			AimlThat e = new AimlThat();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSrai pushSrai() {
			AimlSrai e = new AimlSrai();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlGet pushGet() {
			AimlGet e = new AimlGet();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSet pushSet() {
			AimlSetImpl e = new AimlSetImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlBr push(AimlBrImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlA push(AimlAImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlBot push(AimlBotImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlCondition push(AimlConditionImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlId push(AimlIdImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlPerson2 push(AimlPerson2Impl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlStar push(AimlStarImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlVersion push(AimlVersionImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlInput pushInput() {
			AimlInputImpl e = new AimlInputImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlPerson pushPerson() {
			AimlPersonImpl e = new AimlPersonImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlThink pushThink() {
			AimlThinkImpl e = new AimlThinkImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlRandom pushRandom() {
			AimlRandomImpl e = new AimlRandomImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlLi pushLi() {
			AimlLiImpl e = new AimlLiImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlFormal pushFormal() {
			AimlFormalImpl e = new AimlFormalImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlXml push(AimlXmlImpl e) {
			stack.push(e);
			return e;
		}

		public void add(String value) {
			try {
				stack.lastElement().add(value);
			} catch (NoSuchElementException e) {
				/**
				 * TODO handle with exception throw
				 */
				logger.error("Internal error");
			}
		}

		public void add(AimlProperty value) {
			try {
				stack.lastElement().add(value);
			} catch (NoSuchElementException e) {
				/**
				 * TODO handle with exception throw
				 */
				logger.error("Internal error");
			}
		}
	}

	private StackAiml stack = new StackAiml();
	private List<AimlProperty> currentAttributes = new ArrayList<AimlProperty>();

	private static boolean debugParsing = false;

	/**
	 * default construtor
	 * 
	 * @param filename
	 * @param encoding
	 * @throws AimlParsingError
	 */
	private AimlParserImpl(File filename, String encoding)
			throws AimlParsingError {
		super(AimlLexerImpl.getTokens(filename.getAbsolutePath(), encoding));
		lexer = (AimlLexerImpl) _input.getTokenSource();
		root = stack.push(new AimlXmlImpl());
	}

	/**
	 * another default constructor
	 * 
	 * @param local
	 */
	private AimlParserImpl(CommonTokenStream local) {
		super(local);
		root = stack.push(new AimlXmlImpl());
	}

	/**
	 * parse this document
	 * 
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
				CommonTokenStream local = AimlLexerImpl
						.getTokensFromString(scan.nextLine());
				scan.close();
				AimlParserImpl parser = new AimlParserImpl(local);
				parser.header();
				for (AimlProperty p : parser.currentAttributes) {
					if ("encoding".compareTo(p.getKey()) == 0) {
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
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onAttribute - [" + key + " = " + value + "]");
		}
		currentAttributes.add(new AimlProperty(key, value));
		stack.add(new AimlProperty(key, value));
	}

	@Override
	public void onPcData(String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onPcData - [" + value + "]");
		}
		/**
		 * register this new PCDATA element to current receiver
		 */
		stack.add(value);
	}

	@Override
	public void onOpenTag(String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onOpenTag - " + value);
		}
		switch (decode(value)) {
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
		case SET:
			handleOpenTagSet(value);
			break;
		case RANDOM:
			handleOpenTagRandom(value);
			break;
		case LI:
			handleOpenTagLi(value);
			break;
		case FORMAL:
			handleOpenTagFormal(value);
			break;
		case INPUT:
			stack.pushInput();
			break;
		case PERSON:
			stack.pushPerson();
			break;
		case THINK:
			stack.pushThink();
			break;
		case BR:
			stack.push(new AimlBrImpl());
			break;
		case STAR:
			stack.push(new AimlStarImpl());
			break;
		case A:
			stack.push(new AimlAImpl());
			break;
		case BOT:
			stack.push(new AimlBotImpl());
			break;
		case CONDITION:
			stack.push(new AimlConditionImpl());
			break;
		case PERSON2:
			stack.push(new AimlPerson2Impl());
			break;
		case ID:
			stack.push(new AimlIdImpl());
			break;
		case VERSION:
			stack.push(new AimlVersionImpl());
			break;
		default:
			logger.warn("Unknown tag element : " + value);
			break;
		}
	}

	@Override
	public void onCloseTag(String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onCloseTag - " + value);
		}

		if (decode(value) != router.UNKNOWN) {
			/**
			 * TODO leave this protection
			 */
			stack.pop();

			/**
			 * attributes parsing is clear on tag close
			 */
			currentAttributes.clear();
		}
	}

	/**
	 * An AIML object is represented by an aiml:aiml element in an XML document.
	 * The aiml:aiml element may contain the following types of elements: -
	 * aiml:topic - aiml:category
	 * 
	 * @param tagname
	 */
	private void handleOpenTagAiml(String tagname) {
		/**
		 * create default repository on aiml tag detection attributes must be
		 * handle next
		 */
		repository = stack.push(new AimlRepository());
		repository.setRoot(root);
	}

	/**
	 * A topic is an optional top-level element that contains category elements.
	 * A topic element has a required name attribute that must contain a simple
	 * pattern expression. A topic element may contain one or more category
	 * elements.
	 * 
	 * @param tagname
	 */
	private void handleOpenTagTopic(String tagname) {
		stack.pushTopic();
	}

	/**
	 * A category is a top-level (or second-level, if contained within a topic)
	 * element that contains exactly one pattern and exactly one template. A
	 * category does not have any attributes.
	 * 
	 * @param value
	 */
	private void handleOpenTagCategory(String value) {
		stack.pushCategory();
	}

	/**
	 * A pattern is an element whose content is a mixed pattern expression.
	 * Exactly one pattern must appear in each category. The pattern must always
	 * be the first child element of the category. A pattern does not have any
	 * attributes.
	 * 
	 * @param value
	 */
	private void handleOpenTagPattern(String value) {
		stack.pushPattern();
	}

	/**
	 * The pattern-side that element is a special type of pattern element used
	 * for context matching. The pattern-side that is optional in a category,
	 * but if it occurs it must occur no more than once, and must immediately
	 * follow the pattern and immediately precede the template. A pattern-side
	 * that element contains a simple pattern expression.
	 * 
	 * @param value
	 */
	private void handleOpenTagThat(String value) {
		stack.pushThat();
	}

	/**
	 * The srai element instructs the AIML interpreter to pass the result of
	 * processing the contents of the srai element to the AIML matching loop, as
	 * if the input had been produced by the user (this includes stepping
	 * through the entire input normalization process). The srai element does
	 * not have any attributes. It may contain any AIML template elements.
	 * 
	 * @param value
	 */
	private void handleOpenTagSrai(String value) {
		stack.pushSrai();
	}

	/**
	 * The random element instructs the AIML interpreter to return exactly one
	 * of its contained li elements randomly. The random element must contain
	 * one or more li elements of type defaultListItem, and cannot contain any
	 * other elements.
	 * 
	 * @param value
	 */
	private void handleOpenTagRandom(String value) {
		stack.pushRandom();
	}

	/**
	 * The formal element tells the AIML interpreter to render the contents of
	 * the element such that the first letter of each word is in uppercase, as
	 * defined (if defined) by the locale indicated by the specified language
	 * (if specified). This is similar to methods that are sometimes called
	 * "Title Case".
	 * 
	 * @param value
	 */
	private void handleOpenTagFormal(String value) {
		stack.pushFormal();
	}

	/**
	 * The random element instructs the AIML interpreter to return exactly one
	 * of its contained li elements randomly. The random element must contain
	 * one or more li elements of type defaultListItem, and cannot contain any
	 * other elements.
	 * 
	 * @param value
	 */
	private void handleOpenTagLi(String value) {
		stack.pushLi();
	}

	/**
	 * A template is an element that appears within category elements. The
	 * template must follow the pattern-side that element, if it exists;
	 * otherwise, it follows the pattern element. A template does not have any
	 * attributes.
	 * 
	 * @param value
	 */
	private void handleOpenTagTemplate(String value) {
		stack.pushTemplate();
	}

	/**
	 * The get element tells the AIML interpreter that it should substitute the
	 * contents of a predicate, if that predicate has a value defined. If the
	 * predicate has no value defined, the AIML interpreter should substitute
	 * the empty string "".
	 * 
	 * @param value
	 */
	private void handleOpenTagGet(String value) {
		stack.pushGet();
	}

	/**
	 * The get element tells the AIML interpreter that it should substitute the
	 * contents of a predicate, if that predicate has a value defined. If the
	 * predicate has no value defined, the AIML interpreter should substitute
	 * the empty string "".
	 * 
	 * @param value
	 */
	private void handleOpenTagSet(String value) {
		stack.pushSet();
	}

	/**
	 * utility
	 * 
	 * @param key
	 * @return
	 */
	private String findKeyInAttributes(String key) {
		for (AimlProperty element : currentAttributes) {
			if (element.getKey().compareTo(key) == 0) {
				return element.getValue();
			}
		}
		return null;
	}
}
