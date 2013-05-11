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
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.jarvis.main.antlr4.aimlParser;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.IAimlXml;
import org.jarvis.main.model.parser.category.IAimlA;
import org.jarvis.main.model.parser.category.IAimlBot;
import org.jarvis.main.model.parser.category.IAimlBr;
import org.jarvis.main.model.parser.category.IAimlGet;
import org.jarvis.main.model.parser.category.IAimlLi;
import org.jarvis.main.model.parser.category.IAimlPattern;
import org.jarvis.main.model.parser.category.IAimlSrai;
import org.jarvis.main.model.parser.category.IAimlTemplate;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.category.impl.AimlAImpl;
import org.jarvis.main.model.parser.category.impl.AimlBotImpl;
import org.jarvis.main.model.parser.category.impl.AimlBrImpl;
import org.jarvis.main.model.parser.category.impl.AimlGetImpl;
import org.jarvis.main.model.parser.category.impl.AimlLiImpl;
import org.jarvis.main.model.parser.category.impl.AimlPattern;
import org.jarvis.main.model.parser.category.impl.AimlSraiImpl;
import org.jarvis.main.model.parser.category.impl.AimlTemplateImpl;
import org.jarvis.main.model.parser.category.impl.AimlThatCategoryImpl;
import org.jarvis.main.model.parser.category.impl.AimlThinkImpl;
import org.jarvis.main.model.parser.impl.AimlCategory;
import org.jarvis.main.model.parser.impl.AimlProperty;
import org.jarvis.main.model.parser.impl.AimlRepository;
import org.jarvis.main.model.parser.impl.AimlTopic;
import org.jarvis.main.model.parser.impl.AimlXmlImpl;
import org.jarvis.main.model.parser.pattern.impl.AimlThatPatternImpl;
import org.jarvis.main.model.parser.template.IAimlInput;
import org.jarvis.main.model.parser.template.IAimlPerson;
import org.jarvis.main.model.parser.template.IAimlPerson2;
import org.jarvis.main.model.parser.template.IAimlRandom;
import org.jarvis.main.model.parser.template.IAimlSet;
import org.jarvis.main.model.parser.template.IAimlStar;
import org.jarvis.main.model.parser.template.condition.IAimlCondition;
import org.jarvis.main.model.parser.template.condition.impl.AimlBlockConditionImpl;
import org.jarvis.main.model.parser.template.format.IAimlFormal;
import org.jarvis.main.model.parser.template.format.impl.AimlFormalImpl;
import org.jarvis.main.model.parser.template.impl.AimlInputImpl;
import org.jarvis.main.model.parser.template.impl.AimlPerson2Impl;
import org.jarvis.main.model.parser.template.impl.AimlPersonImpl;
import org.jarvis.main.model.parser.template.impl.AimlRandomImpl;
import org.jarvis.main.model.parser.template.impl.AimlSetImpl;
import org.jarvis.main.model.parser.template.impl.AimlStarImpl;
import org.jarvis.main.model.parser.template.impl.AimlThatTemplateImpl;
import org.jarvis.main.model.parser.template.system.IAimlId;
import org.jarvis.main.model.parser.template.system.IAimlVersion;
import org.jarvis.main.model.parser.template.system.impl.AimlIdImpl;
import org.jarvis.main.model.parser.template.system.impl.AimlVersionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlParserImpl extends aimlParser {
	Logger							logger			= LoggerFactory
															.getLogger(AimlParserImpl.class);

	protected AimlLexerImpl			lexer			= null;

	/**
	 * AIML model
	 */
	private IAimlXml				root			= null;
	private IAimlRepository			repository		= null;

	private final StackAimlElement	stackElements	= new StackAimlElement();

	private static boolean			debugParsing	= false;

	public IAimlRepository getRepository() {
		return repository;
	}

	private class StackAimlStructure {
		protected Stack<IAimlElement>	stack	= new Stack<IAimlElement>();

		protected IAimlXml push(AimlXmlImpl e) {
			stack.push(e);
			return e;
		}

		protected IAimlRepository push(AimlRepository e) {
			stack.push(e);
			return e;
		}

		protected IAimlTopic push(AimlTopic e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		protected IAimlCategory push(AimlCategory e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		protected IAimlTemplate push(AimlTemplateImpl e) {
			/**
			 * template is always a category child
			 */
			IAimlCategory cat = (IAimlCategory) stack.lastElement();
			cat.setTemplate(e);
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		protected IAimlPattern push(AimlPattern e) {
			/**
			 * pattern is always a category child
			 */
			IAimlCategory cat = (IAimlCategory) stack.lastElement();
			cat.setPattern(e);
			stack.lastElement().add(e);
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

		public IAimlElement lastElement() {
			return stack.lastElement();
		}

		protected IAimlElement pop(String tagName) {
			return stack.pop();
		}
	}

	private class StackAimlElement extends StackAimlStructure {
		private final StackAimlStructure	filter	= new StackAimlStructure();

		@Override
		public IAimlElement pop(String tagName) {
			IAimlElement result = super.pop(tagName);
			/**
			 * only pop specific tag name in filter structure
			 */
			if ("xml".compareTo(tagName) == 0 || "aiml".compareTo(tagName) == 0
					|| "category".compareTo(tagName) == 0
					|| "template".compareTo(tagName) == 0
					|| "pattern".compareTo(tagName) == 0) {
				filter.pop(tagName);
			}
			return result;
		}

		@Override
		public IAimlXml push(AimlXmlImpl e) {
			super.push(e);
			filter.push(new AimlXmlImpl());
			return e;
		}

		@Override
		public IAimlRepository push(AimlRepository e) {
			super.push(e);
			filter.push(new AimlRepository());
			return e;
		}

		@Override
		public IAimlTopic push(AimlTopic e) {
			super.push(e);
			filter.push(new AimlTopic());
			return e;
		}

		@Override
		public IAimlCategory push(AimlCategory e) {
			super.push(e);
			filter.push(new AimlCategory());
			return e;
		}

		@Override
		public IAimlTemplate push(AimlTemplateImpl e) {
			super.push(e);
			filter.push(new AimlTemplateImpl());
			return e;
		}

		@Override
		public IAimlPattern push(AimlPattern e) {
			super.push(e);
			filter.push(new AimlPattern());
			return e;
		}

		public IAimlThat push(AimlThatCategoryImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlThat push(AimlThatPatternImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlThat push(AimlThatTemplateImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSrai pushSrai() {
			AimlSraiImpl e = new AimlSraiImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlGet pushGet() {
			AimlGetImpl e = new AimlGetImpl();
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSet push(AimlSetImpl e) {
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

		public IAimlCondition push(AimlBlockConditionImpl e) {
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

		public IAimlInput push(AimlInputImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlPerson push(AimlPersonImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlThink push(AimlThinkImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlRandom push(AimlRandomImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlLi push(AimlLiImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlFormal push(AimlFormalImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public StackAimlStructure getFilter() {
			return filter;
		}
	}

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
		root = stackElements.push(new AimlXmlImpl());
	}

	/**
	 * another default constructor
	 * 
	 * @param local
	 */
	private AimlParserImpl(CommonTokenStream local) {
		super(local);
		root = stackElements.push(new AimlXmlImpl());
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
				parser.setRepository(new AimlRepository());
				parser.getRepository().setRoot(parser.getRoot());
				parser.header();
				/**
				 * find encoding
				 */
				String enc = parser.getRepository().getRoot().get("encoding");
				if (enc != null) encoding = enc;
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

	private IAimlXml getRoot() {
		return root;
	}

	private void setRepository(AimlRepository aimlRepository) {
		repository = aimlRepository;
	}

	@Override
	public void onAttribute(String key, String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onAttribute - [" + key + " = " + value + "]");
		}
		stackElements.add(new AimlProperty(key, value));
	}

	@Override
	public void onPcData(String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onPcData - [" + value + "]");
		}
		/**
		 * register this new PCDATA element to current receiver
		 */
		stackElements.add(value);
	}

	@Override
	public void onOpenTag(String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onOpenTag - " + value);
		}
		switch (decode(value)) {
			case AIML:
				/**
				 * An AIML object is represented by an aiml:aiml element in an
				 * XML document. The aiml:aiml element may contain the following
				 * types of elements: - aiml:topic - aiml:category
				 * 
				 * create default repository on aiml tag detection attributes
				 * must be handle next
				 */
				repository = stackElements.push(new AimlRepository());
				repository.setRoot(root);
				break;
			case TOPIC:
				/**
				 * A topic is an optional top-level element that contains
				 * category elements. A topic element has a required name
				 * attribute that must contain a simple pattern expression. A
				 * topic element may contain one or more category elements.
				 */
				IAimlTopic topic = stackElements.push(new AimlTopic());
				repository.addTopic(topic);
				break;
			case CATEGORY:
				/**
				 * A category is a top-level (or second-level, if contained
				 * within a topic) element that contains exactly one pattern and
				 * exactly one template. A category does not have any
				 * attributes.
				 */
				IAimlCategory category = stackElements.push(new AimlCategory());
				repository.addCategory(category);
				break;
			case TEMPLATE:
				/**
				 * A template is an element that appears within category
				 * elements. The template must follow the pattern-side that
				 * element, if it exists; otherwise, it follows the pattern
				 * element. A template does not have any attributes.
				 */
				stackElements.push(new AimlTemplateImpl());
				break;
			case PATTERN:
				/**
				 * A pattern is an element whose content is a mixed pattern
				 * expression. Exactly one pattern must appear in each category.
				 * The pattern must always be the first child element of the
				 * category. A pattern does not have any attributes.
				 */
				stackElements.push(new AimlPattern());
				break;
			case THAT:
				/**
				 * The pattern-side that element is a special type of pattern
				 * element used for context matching. The pattern-side that is
				 * optional in a category, but if it occurs it must occur no
				 * more than once, and must immediately follow the pattern and
				 * immediately precede the template. A pattern-side that element
				 * contains a simple pattern expression.
				 * 
				 * allocate that on category side, or template side according to
				 * the parsing context
				 */
				if (stackElements.getFilter().lastElement().getClass() == AimlTemplateImpl.class) {
					stackElements.push(new AimlThatTemplateImpl());
					return;
				}
				if (stackElements.getFilter().lastElement().getClass() == AimlPattern.class) {
					stackElements.push(new AimlThatPatternImpl());
					return;
				}
				if (stackElements.getFilter().lastElement().getClass() == AimlCategory.class) {
					stackElements.push(new AimlThatCategoryImpl());
					return;
				}

				logger.error("Element cannot be null");
				/**
				 * TODO error management
				 */
				break;
			case SRAI:
				/**
				 * The srai element instructs the AIML interpreter to pass the
				 * result of processing the contents of the srai element to the
				 * AIML matching loop, as if the input had been produced by the
				 * user (this includes stepping through the entire input
				 * normalization process). The srai element does not have any
				 * attributes. It may contain any AIML template elements.
				 */
				stackElements.pushSrai();
				break;
			case GET:
				/**
				 * The get element tells the AIML interpreter that it should
				 * substitute the contents of a predicate, if that predicate has
				 * a value defined. If the predicate has no value defined, the
				 * AIML interpreter should substitute the empty string "".
				 */
				stackElements.pushGet();
				break;
			case SET:
				/**
				 * The set element instructs the AIML interpreter to set the
				 * value of a predicate to the result of processing the contents
				 * of the set element. The set element has a required attribute
				 * name, which must be a valid AIML predicate name. If the
				 * predicate has not yet been defined, the AIML interpreter
				 * should define it in memory.
				 */
				stackElements.push(new AimlSetImpl());
				break;
			case RANDOM:
				/**
				 * The random element instructs the AIML interpreter to return
				 * exactly one of its contained li elements randomly. The random
				 * element must contain one or more li elements of type
				 * defaultListItem, and cannot contain any other elements.
				 */
				stackElements.push(new AimlRandomImpl());
				break;
			case LI:
				/**
				 * The random element instructs the AIML interpreter to return
				 * exactly one of its contained li elements randomly. The random
				 * element must contain one or more li elements of type
				 * defaultListItem, and cannot contain any other elements.
				 */
				stackElements.push(new AimlLiImpl());
				break;
			case FORMAL:
				/**
				 * The formal element tells the AIML interpreter to render the
				 * contents of the element such that the first letter of each
				 * word is in uppercase, as defined (if defined) by the locale
				 * indicated by the specified language (if specified). This is
				 * similar to methods that are sometimes called "Title Case".
				 */
				stackElements.push(new AimlFormalImpl());
				break;
			case INPUT:
				stackElements.push(new AimlInputImpl());
				break;
			case PERSON:
				stackElements.push(new AimlPersonImpl());
				break;
			case THINK:
				stackElements.push(new AimlThinkImpl());
				break;
			case BR:
				stackElements.push(new AimlBrImpl());
				break;
			case STAR:
				stackElements.push(new AimlStarImpl());
				break;
			case A:
				stackElements.push(new AimlAImpl());
				break;
			case BOT:
				stackElements.push(new AimlBotImpl());
				break;
			case CONDITION:
				stackElements.push(new AimlBlockConditionImpl());
				break;
			case PERSON2:
				stackElements.push(new AimlPerson2Impl());
				break;
			case ID:
				stackElements.push(new AimlIdImpl());
				break;
			case VERSION:
				stackElements.push(new AimlVersionImpl());
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
		stackElements.pop(value);
	}
}
