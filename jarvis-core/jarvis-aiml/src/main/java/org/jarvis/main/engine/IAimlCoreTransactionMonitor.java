package org.jarvis.main.engine;

import java.util.List;

/**
 * transaction
 */
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
	 * @return IAimlCoreTransaction
	 */
	IAimlCoreTransaction getTransaction();

	/**
	 * retrieve all transactions
	 * @return List<IAimlCoreTransaction>
	 */
	List<IAimlCoreTransaction> getTransactions();
}
