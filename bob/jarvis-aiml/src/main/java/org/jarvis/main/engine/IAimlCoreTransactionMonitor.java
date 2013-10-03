package org.jarvis.main.engine;

import java.util.List;

public interface IAimlCoreTransactionMonitor {

	/**
	 * commit current transaction
	 */
	void commit();
	
	/**
	 * load message to current transaction
	 * @param message
	 */
	void info(String message);

	/**
	 * retrieve current transaction
	 * @return
	 */
	IAimlCoreTransaction getTransaction();

	/**
	 * retrieve all transactions
	 * @return
	 */
	List<IAimlCoreTransaction> getTransactions();
}
