package org.jarvis.main.engine.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreTransaction;
import org.jarvis.main.engine.IAimlCoreTransactionMonitor;

/**
 * @author kazoar
 *
 */
public class AimlCoreTransactionMonitorImpl implements
		IAimlCoreTransactionMonitor {

	IAimlCoreTransaction current = null;
	List<IAimlCoreTransaction> transactions = new ArrayList<IAimlCoreTransaction>();

	@Override
	public IAimlCoreTransaction getTransaction() {
		if(current == null) {
			current = new AimlCoreTransactionImpl();
			transactions.add(current);
		}
		return current;		
	}

	@Override
	public void info(String message) {
		getTransaction().message(message);		
	}

	@Override
	public void commit() {
		current = null;		
	}

	@Override
	public List<IAimlCoreTransaction> getTransactions() {
		return transactions;
	}
}
