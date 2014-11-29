package service.business.impl;

import java.io.IOException;

import models.message.DefaultAnswer;
import models.message.DefaultMessage;
import models.profiling.TransactionProfiler;

import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.engine.IAimlCoreTransaction;
import org.jarvis.main.engine.ICategoryStack;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;
import org.jarvis.main.model.parser.history.IAimlHistory;

import service.business.IJarvisBusiness;

public class JarvisBusinessImpl implements IJarvisBusiness {
	IJarvisCoreSystem jarvis;

	public JarvisBusinessImpl() {
		/**
		 * Jarvis system
		 */
		jarvis = new JarvisCoreSystemImpl();
		try {
			jarvis.initialize("jarvis.txt");
		} catch (AimlParsingError | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DefaultAnswer send(DefaultMessage msg) throws AimlParsingError {
		DefaultAnswer result = new DefaultAnswer();

		for(IAimlHistory item : jarvis.chat(msg.getMessage())) {
			result.add(item.getAnswer());
		}
		
		for(IAimlCoreTransaction tx : jarvis.getEngine().getTransactionMonitor().getTransactions()) {
			TransactionProfiler transaction = result.addTransaction();
			for(ICategoryStack category : tx.getCategories()) {
				transaction.add(category);
			}
		}
		return result;
	}
}
