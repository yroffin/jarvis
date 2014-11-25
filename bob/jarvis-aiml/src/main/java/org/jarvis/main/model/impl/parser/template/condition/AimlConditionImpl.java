package org.jarvis.main.model.impl.parser.template.condition;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.transform.AimlTranformImpl;
import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.impl.parser.category.AimlLiImpl;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.category.IAimlLi;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.condition.IAimlCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlConditionImpl extends AimlElementContainer implements
		IAimlCondition {

	protected Logger logger = LoggerFactory.getLogger(AimlConditionImpl.class);
	private final IAimlTransform transformer = new AimlTranformImpl();

	public AimlConditionImpl() {
		super("condition");
	}

	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void add(IAimlProperty p) {
		if (p.getKey().compareTo("name") == 0)
			name = accept(p);
		if (p.getKey().compareTo("value") == 0)
			value = accept(p);
	}

	/**
	 * block like <condition name="gender" value="female">
	 * attractive.</condition>
	 * 
	 * @param engine
	 * @param star
	 * @param that
	 * @param render
	 * @throws AimlParsingError
	 */
	private void answerBlock(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		String eval = (String) engine.get(name);
		if (eval == null && value == null)
			return;
		if (eval == null)
			return;

		if (transformer.compare(eval, value)) {
			super.answer(engine, star, that, render);
		}
	}

	/**
	 * block like <category> <pattern>I AM BLOND</pattern> <template>You sound
	 * very <condition name="gender"> <li value="female">attractive.</li> <li value="male">
	 * handsome.</li> </condition> </template> </category>
	 * 
	 * @param engine
	 * @param star
	 * @param that
	 * @param render
	 * @throws AimlParsingError
	 */
	private void answerSingle(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		String eval = (String) engine.get(name);

		for (IAimlElement element : elements) {
			/**
			 * count AimlLiImpl elements
			 */
			if (element.getClass() == AimlLiImpl.class) {
				IAimlLi li = (IAimlLi) element;
				if (transformer.compare(eval, li.getValue())) {
					li.answer(engine, star, that, render);
					return;
				}
			}
		}
	}

	/**
	 * block like <category> <pattern>I AM BLOND</pattern> <template>You sound
	 * very <condition name="gender"> <li value="female">attractive.</li> <li value="male">
	 * handsome.</li> </condition> </template> </category>
	 * 
	 * @param engine
	 * @param star
	 * @param that
	 * @param render
	 * @throws AimlParsingError
	 */
	private void answerMulti(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		for (IAimlElement element : elements) {
			/**
			 * count AimlLiImpl elements
			 */
			if (element.getClass() == AimlLiImpl.class) {
				IAimlLi li = (IAimlLi) element;
				if (transformer.compare((String) engine.get(li.getName()),
						li.getValue())) {
					li.answer(engine, star, that, render);
				}
			}
		}
	}

	private void answerDefault(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		String eval = (String) engine.get(name);

		if (transformer.compare(eval, value)) {
			for (IAimlElement element : elements) {
				/**
				 * count AimlLiImpl elements
				 */
				if (element.getClass() == AimlLiImpl.class) {
					IAimlLi li = (IAimlLi) element;
					li.answer(engine, star, that, render);
					return;
				}
			}
		}
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		int countLi = 0;
		int valueOnlyListItem = 0;
		int nameValueListItem = 0;
		int defaultListItem = 0;
		/**
		 * analyse sub item
		 */
		for (IAimlElement element : elements) {
			/**
			 * count AimlLiImpl elements
			 */
			if (element.getClass() == AimlLiImpl.class) {
				IAimlLi li = (IAimlLi) element;
				if (li.isValueOnlyListItem())
					valueOnlyListItem++;
				if (li.isNameValueListItem())
					nameValueListItem++;
				if (li.isDefaultListItem())
					defaultListItem++;
				countLi++;
			}
		}
		/**
		 * analyse condition
		 */
		if (countLi == 0) {
			/**
			 * Block Predicate
			 */
			answerBlock(engine, star, that, render);
		} else {
			if (valueOnlyListItem > 0) {
				/**
				 * singlePredicate
				 */
				answerSingle(engine, star, that, render);
			} else {
				/**
				 * multiPredicate
				 */
				if (nameValueListItem > 0) {
					answerMulti(engine, star, that, render);
				} else {
					/**
					 * Default
					 */
					if (defaultListItem == 1) {
						answerDefault(engine, star, that, render);
					} else {
						logger.warn("Grammar error, li element must be alone");
					}
				}
			}
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCondition [elements=" + elements + "]";
	}
}
