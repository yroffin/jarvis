package org.jarvis.main.model.impl.parser.template.system;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.system.IAimlSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlSystemImpl extends AimlElementContainer implements IAimlSystem {

	protected Logger logger = LoggerFactory.getLogger(AimlSystemImpl.class);

	public AimlSystemImpl() {
		super("system");
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) throws AimlParsingError {
		/**
		 * AIML defines two external processor elements, which instruct the AIML
		 * interpreter to pass the contents of the elements to an external
		 * processor. External processor elements may return a value, but are
		 * not required to do so.
		 * 
		 * Contents of external processor elements may consist of character data
		 * as well as AIML template elements. If AIML template elements in the
		 * contents of an external processor element are not enclosed as CDATA,
		 * then the AIML interpreter is required to substitute the results of
		 * processing those elements before passing the contents to the external
		 * processor. As a trivial example, consider:
		 * 
		 * <system>
		 * 
		 * echo '<get name="name"/>'
		 * 
		 * </system>
		 * 
		 * Before passing the contents of this system element to the appropriate
		 * external processor, the AIML interpreter is required to substitute
		 * the results of processing the get element.
		 * 
		 * AIML 1.0.1 does not require that any contents of an external
		 * processor element are enclosed as CDATA. An AIML interpreter should
		 * assume that any unrecognized content is character data, and simply
		 * pass it to the appropriate external processor as-is, following any
		 * processing of AIML template elements not enclosed as CDATA.
		 * 
		 * If an external processor is not available to process the contents of
		 * an external processor element, the AIML interpreter may return an
		 * error, but this is not required.
		 */
		String local = super.answer(engine, star, that, new StringBuilder())
				.toString();
		logger.info("System: " + local);
		render.append("Runnning script ...");
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSystem [elements=" + elements + "]";
	}
}
