package edu.uw.advjava.wilsonma;

import edu.uw.pce.advjava.services.Broker;
import edu.uw.pce.advjava.services.BrokerFactory;
import edu.uw.pce.advjava.support.account.AccountManager;
import edu.uw.pce.advjava.support.exchange.StockExchange;

/**
 * Simple implementation for the BrokerFactory.
 * 
 * @author Michael Wilson
 */
public class BrokerFactoryImpl implements BrokerFactory {
	/**
	 * Default constructor - should not be used.
	 */
	public BrokerFactoryImpl() {}

	/**
	 * Instantiates a new broker instance.
	 * 
	 * @param name The name of the broker.
	 * @param acctMngr The account manager.
	 * @param exch The associated stock exchange.
	 * @return The broker with the associated properties.
	 */
	@Override
	public Broker newBroker(String name, AccountManager acctMngr, StockExchange exch) {
		return new BrokerImpl(name, acctMngr, exch);
	}

}
