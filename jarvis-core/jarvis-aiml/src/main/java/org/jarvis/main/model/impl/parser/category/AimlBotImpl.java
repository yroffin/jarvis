package org.jarvis.main.model.impl.parser.category;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.category.IAimlBot;
import org.jarvis.main.model.parser.history.IAimlHistory;

/**
 * @author kazoar
 *
 */
public class AimlBotImpl extends AimlElementContainer implements IAimlBot {

	public AimlBotImpl() {
		super("bot");
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("name") == 0)
			name = accept(value);
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		if (name == null) {
			render.append("");
		} else {
			render.append((String) engine.getBot(name));
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlBot [elements=" + elements + "]";
	}
}
