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

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.jarvis.main.antlr4.aimlParser;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlCategory;
import org.jarvis.main.model.impl.parser.AimlProperty;
import org.jarvis.main.model.impl.parser.AimlRepository;
import org.jarvis.main.model.impl.parser.AimlTopic;
import org.jarvis.main.model.impl.parser.AimlXmlImpl;
import org.jarvis.main.model.impl.parser.category.AimlAImpl;
import org.jarvis.main.model.impl.parser.category.AimlBotImpl;
import org.jarvis.main.model.impl.parser.category.AimlBrImpl;
import org.jarvis.main.model.impl.parser.category.AimlGetImpl;
import org.jarvis.main.model.impl.parser.category.AimlLiImpl;
import org.jarvis.main.model.impl.parser.category.AimlPatternImpl;
import org.jarvis.main.model.impl.parser.category.AimlSetImpl;
import org.jarvis.main.model.impl.parser.category.AimlSraiImpl;
import org.jarvis.main.model.impl.parser.category.AimlTemplateImpl;
import org.jarvis.main.model.impl.parser.category.AimlThatCategoryImpl;
import org.jarvis.main.model.impl.parser.category.AimlThinkImpl;
import org.jarvis.main.model.impl.parser.pattern.AimlThatPatternImpl;
import org.jarvis.main.model.impl.parser.template.AimlGossipImpl;
import org.jarvis.main.model.impl.parser.template.AimlInputImpl;
import org.jarvis.main.model.impl.parser.template.AimlRandomImpl;
import org.jarvis.main.model.impl.parser.template.AimlStarImpl;
import org.jarvis.main.model.impl.parser.template.AimlThatStarImpl;
import org.jarvis.main.model.impl.parser.template.AimlThatTemplateImpl;
import org.jarvis.main.model.impl.parser.template.AimlTopicStarImpl;
import org.jarvis.main.model.impl.parser.template.condition.AimlConditionImpl;
import org.jarvis.main.model.impl.parser.template.format.AimlFormalImpl;
import org.jarvis.main.model.impl.parser.template.format.AimlLowercaseImpl;
import org.jarvis.main.model.impl.parser.template.format.AimlUppercaseImpl;
import org.jarvis.main.model.impl.parser.template.system.AimlDateImpl;
import org.jarvis.main.model.impl.parser.template.system.AimlIdImpl;
import org.jarvis.main.model.impl.parser.template.system.AimlJavascriptImpl;
import org.jarvis.main.model.impl.parser.template.system.AimlSizeImpl;
import org.jarvis.main.model.impl.parser.template.system.AimlSystemImpl;
import org.jarvis.main.model.impl.parser.template.system.AimlVersionImpl;
import org.jarvis.main.model.impl.parser.template.trans.AimlGenderImpl;
import org.jarvis.main.model.impl.parser.template.trans.AimlPerson2Impl;
import org.jarvis.main.model.impl.parser.template.trans.AimlPersonImpl;
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
import org.jarvis.main.model.parser.category.IAimlSet;
import org.jarvis.main.model.parser.category.IAimlSrai;
import org.jarvis.main.model.parser.category.IAimlTemplate;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.template.IAimlGossip;
import org.jarvis.main.model.parser.template.IAimlInput;
import org.jarvis.main.model.parser.template.IAimlRandom;
import org.jarvis.main.model.parser.template.IAimlStar;
import org.jarvis.main.model.parser.template.IAimlThatStar;
import org.jarvis.main.model.parser.template.IAimlTopicStar;
import org.jarvis.main.model.parser.template.condition.IAimlCondition;
import org.jarvis.main.model.parser.template.format.IAimlFormal;
import org.jarvis.main.model.parser.template.format.IAimlLowercase;
import org.jarvis.main.model.parser.template.format.IAimlUppercase;
import org.jarvis.main.model.parser.template.system.IAimlDate;
import org.jarvis.main.model.parser.template.system.IAimlId;
import org.jarvis.main.model.parser.template.system.IAimlJavascript;
import org.jarvis.main.model.parser.template.system.IAimlSize;
import org.jarvis.main.model.parser.template.system.IAimlSystem;
import org.jarvis.main.model.parser.template.system.IAimlVersion;
import org.jarvis.main.model.parser.template.trans.IAimlGender;
import org.jarvis.main.model.parser.template.trans.IAimlPerson;
import org.jarvis.main.model.parser.template.trans.IAimlPerson2;
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

	private final StackAimlElement stackElements = new StackAimlElement();

	private static boolean debugParsing = false;

	public IAimlRepository getRepository() {
		return repository;
	}

	private class StackAimlStructure {
		protected Stack<IAimlElement> stack = new Stack<IAimlElement>();

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

		protected IAimlPattern push(AimlPatternImpl e) {
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
		private final StackAimlStructure filter = new StackAimlStructure();

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
		public IAimlPattern push(AimlPatternImpl e) {
			super.push(e);
			filter.push(new AimlPatternImpl());
			return e;
		}

		public IAimlThat push(AimlThatCategoryImpl e) {
			/**
			 * that is always a category child
			 */
			IAimlCategory cat = (IAimlCategory) stack.lastElement();
			cat.setThat(e);
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

		public IAimlSrai push(AimlSraiImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlGossip push(AimlGossipImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlGet push(AimlGetImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSet push(AimlSetImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlDate push(AimlDateImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlId push(AimlIdImpl e) {
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

		public IAimlPerson2 push(AimlPerson2Impl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSize push(AimlSizeImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlStar push(AimlStarImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlThatStar push(AimlThatStarImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlTopicStar push(AimlTopicStarImpl e) {
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

		public IAimlUppercase push(AimlUppercaseImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlLowercase push(AimlLowercaseImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlSystem push(AimlSystemImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlGender push(AimlGenderImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}

		public IAimlJavascript push(AimlJavascriptImpl e) {
			stack.lastElement().add(e);
			stack.push(e);
			return e;
		}
	}

	private int iParserError = 0;
	private int iLexerError = 0;

	private class DiagnosticErrorListenerImpl extends DiagnosticErrorListener {

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer,
				Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {
			super.syntaxError(recognizer, offendingSymbol, line,
					charPositionInLine, msg, e);
			logger.error("line " + line + " char position "
					+ charPositionInLine + " : " + msg + " : "
					+ (e.getInputStream() + "").split("\n")[line - 1]);
			iLexerError++;
		}

	}

	private class DefaultErrorStrategyImpl extends BailErrorStrategy {

		@Override
		public void reportError(Parser recognizer, RecognitionException e)
				throws RecognitionException {
			super.reportError(recognizer, e);
			logger.error(e.getMessage());
			iParserError++;
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

		lexer.addErrorListener(new DiagnosticErrorListenerImpl());
		setErrorHandler(new DefaultErrorStrategyImpl());
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

			if (parser.getParserErrors() > 0 || parser.getLexerErrors() > 0) { throw new AimlParsingError(
					"Errors, while parsing"); }
			return parser.getRepository();
		} catch (RecognitionException e) {
			throw new AimlParsingError(e);
		}
	}

	private int getParserErrors() {
		return iParserError;
	}

	private int getLexerErrors() {
		return iLexerError;
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

	IAimlCategory category = null;

	@Override
	public void onOpenTag(String value) {
		if (logger.isDebugEnabled() && debugParsing) {
			logger.debug("onOpenTag - " + value);
		}
		switch (decode(value)) {
		case AIML:
			/**
			 * An AIML object is represented by an aiml:aiml element in an XML
			 * document. The aiml:aiml element may contain the following types
			 * of elements: - aiml:topic - aiml:category
			 * 
			 * create default repository on aiml tag detection attributes must
			 * be handle next
			 */
			repository = stackElements.push(new AimlRepository());
			repository.setRoot(root);
			break;
		case TOPIC:
			/**
			 * A topic is an optional top-level element that contains category
			 * elements. A topic element has a required name attribute that must
			 * contain a simple pattern expression. A topic element may contain
			 * one or more category elements.
			 */
			IAimlTopic topic = stackElements.push(new AimlTopic());
			repository.addTopic(topic);
			break;
		case CATEGORY:
			/**
			 * A category is a top-level (or second-level, if contained within a
			 * topic) element that contains exactly one pattern and exactly one
			 * template. A category does not have any attributes.
			 */
			category = stackElements.push(new AimlCategory());
			repository.addCategory(category);
			break;
		case TEMPLATE:
			/**
			 * A template is an element that appears within category elements.
			 * The template must follow the pattern-side that element, if it
			 * exists; otherwise, it follows the pattern element. A template
			 * does not have any attributes.
			 */
			stackElements.push(new AimlTemplateImpl());
			break;
		case PATTERN:
			/**
			 * A pattern is an element whose content is a mixed pattern
			 * expression. Exactly one pattern must appear in each category. The
			 * pattern must always be the first child element of the category. A
			 * pattern does not have any attributes.
			 */
			stackElements.push(new AimlPatternImpl());
			break;
		case THAT:
			/**
			 * The pattern-side that element is a special type of pattern
			 * element used for context matching. The pattern-side that is
			 * optional in a category, but if it occurs it must occur no more
			 * than once, and must immediately follow the pattern and
			 * immediately precede the template. A pattern-side that element
			 * contains a simple pattern expression.
			 * 
			 * allocate that on category side, or template side according to the
			 * parsing context
			 */
			if (stackElements.getFilter().lastElement().getClass() == AimlTemplateImpl.class) {
				stackElements.push(new AimlThatTemplateImpl());
				return;
			}
			if (stackElements.getFilter().lastElement().getClass() == AimlPatternImpl.class) {
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
		case THATSTAR:
			/**
			 * The thatstar element tells the AIML interpreter that it should
			 * substitute the contents of a wildcard from a pattern-side that
			 * element.
			 * 
			 * The thatstar element has an optional integer index attribute that
			 * indicates which wildcard to use; the minimum acceptable value for
			 * the index is "1" (the first wildcard).
			 */
			if (category != null && category.getThat() != null) stackElements
					.push(new AimlThatStarImpl(category.getThat()));
			break;
		case SRAI:
			/**
			 * The srai element instructs the AIML interpreter to pass the
			 * result of processing the contents of the srai element to the AIML
			 * matching loop, as if the input had been produced by the user
			 * (this includes stepping through the entire input normalization
			 * process). The srai element does not have any attributes. It may
			 * contain any AIML template elements.
			 */
			stackElements.push(new AimlSraiImpl());
			break;
		case SR:
			/**
			 * The sr element is a shortcut for:
			 * 
			 * <srai><star/></srai>
			 */
			stackElements.push(new AimlSraiImpl());
			stackElements.push(new AimlStarImpl());
			stackElements.pop(value);
			break;
		case GET:
			/**
			 * The get element tells the AIML interpreter that it should
			 * substitute the contents of a predicate, if that predicate has a
			 * value defined. If the predicate has no value defined, the AIML
			 * interpreter should substitute the empty string "".
			 */
			stackElements.push(new AimlGetImpl());
			break;
		case SET:
			/**
			 * The set element instructs the AIML interpreter to set the value
			 * of a predicate to the result of processing the contents of the
			 * set element. The set element has a required attribute name, which
			 * must be a valid AIML predicate name. If the predicate has not yet
			 * been defined, the AIML interpreter should define it in memory.
			 */
			stackElements.push(new AimlSetImpl());
			break;
		case DATE:
			/**
			 * The date element tells the AIML interpreter that it should
			 * substitute the system local date and time. No formatting
			 * constraints on the output are specified.
			 * 
			 * The date element does not have any content.
			 */
			stackElements.push(new AimlDateImpl());
			break;
		case ID:
			/**
			 * The id element tells the AIML interpreter that it should
			 * substitute the user ID. The determination of the user ID is not
			 * specified, since it will vary by application. A suggested default
			 * return value is "localhost".
			 */
			stackElements.push(new AimlIdImpl());
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
			 * contents of the element such that the first letter of each word
			 * is in uppercase, as defined (if defined) by the locale indicated
			 * by the specified language (if specified). This is similar to
			 * methods that are sometimes called "Title Case".
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
		case TOPICSTAR:
			stackElements.push(new AimlTopicStarImpl());
			break;
		case A:
			stackElements.push(new AimlAImpl());
			break;
		case BOT:
			stackElements.push(new AimlBotImpl());
			break;
		case CONDITION:
			stackElements.push(new AimlConditionImpl());
			break;
		case PERSON2:
			stackElements.push(new AimlPerson2Impl());
			break;
		case SIZE:
			stackElements.push(new AimlSizeImpl());
			break;
		case VERSION:
			stackElements.push(new AimlVersionImpl());
			break;
		case UPPER:
			stackElements.push(new AimlUppercaseImpl());
			break;
		case LOWER:
			stackElements.push(new AimlLowercaseImpl());
			break;
		case SYSTEM:
			stackElements.push(new AimlSystemImpl());
			break;
		case JAVASCRIPT:
			stackElements.push(new AimlJavascriptImpl());
			break;
		case GENDER:
			/**
			 * The gender element instructs the AIML interpreter to:
			 * 
			 * replace male-gendered words in the result of processing the
			 * contents of the gender element with the
			 * grammatically-corresponding female-gendered words; and replace
			 * female-gendered words in the result of processing the contents of
			 * the gender element with the grammatically-corresponding
			 * male-gendered words.
			 */
			stackElements.push(new AimlGenderImpl());
			break;
		case GOSSIP:
			/**
			 * The gossip element instructs the AIML interpreter to capture the
			 * result of processing the contents of the gossip elements and to
			 * store these contents in a manner left up to the implementation.
			 * Most common uses of gossip have been to store captured contents
			 * in a separate file.
			 */
			stackElements.push(new AimlGossipImpl());
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
