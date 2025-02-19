package edu.uw.advjava.wilsonma;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.uw.pce.advjava.services.Broker;
import edu.uw.pce.advjava.services.OrderManager;
import edu.uw.pce.advjava.support.account.Account;
import edu.uw.pce.advjava.support.account.AccountException;
import edu.uw.pce.advjava.support.account.AccountManager;
import edu.uw.pce.advjava.support.broker.BrokerException;
import edu.uw.pce.advjava.support.broker.Order;
import edu.uw.pce.advjava.support.broker.StopOrder;
import edu.uw.pce.advjava.support.exchange.ExchangeEvent;
import edu.uw.pce.advjava.support.exchange.ExchangeListener;
import edu.uw.pce.advjava.support.exchange.StockExchange;
import edu.uw.pce.advjava.support.exchange.StockQuote;

/**
 * An implementation of the Broker interface.
 * 
 * @author Michael Wilson
 */
@SuppressWarnings("unused")
public class BrokerImpl implements Broker, ExchangeListener {
	private final StockExchange stockExchange;
	private final AccountManager accountManager;

	private boolean canMakeOrders = true;
	private String brokerName;
	private Map<String, OrderManager> orderManagers;
	private TriggeredDispatcherImpl<Order, Boolean> marketOrders;
	
	/**
	 * Constructs an instance of the BrokerImpl class.
	 * 
	 * @param name The name of the broker.
	 * @param acctMngr The account manager associated with the broker.
	 * @param exch The stockexchange that the broker handles.
	 */
	public BrokerImpl(String name, AccountManager acctMngr, StockExchange exch) {
		orderManagers = new HashMap<>();
		brokerName = name;
		accountManager = acctMngr;
		stockExchange = exch;
		
		// listen to price changes
		stockExchange.addExchangeListener(this);
		
		Comparator<Order> marketOrderComparator = Comparator
				.comparing(Order::getNumberOfShares)
				.reversed()
				.thenComparing(Order::compareTo);
		
		// under the assumption that all market orders are dispatchable
		marketOrders = new TriggeredDispatcherImpl<>(marketOrderComparator, (e, t) -> true, true);
		marketOrders.setDispatchAction(this::executeOrder);
		
		// Set up a manger for each stock
		for(String ticker : stockExchange.getStockTickerSymbols()) {
			Optional<StockQuote> oPrice = stockExchange.getQuote(ticker);
			int price = oPrice.get().getPrice();
			OrderManager om = new OrderManagerImpl(price, ticker);
			om.setStopOrderProcessor(marketOrders::enqueue);
			orderManagers.put(ticker, om);
		}				
	}

	/**
	 * Release resources used by the broker.
	 * 
	 * @throws BrokerException If there are no
	 */
	@Override
	public void close() throws BrokerException {
		try {
			accountManager.close();
			stockExchange.removeExchangeListener(this);
			marketOrders = null;
			canMakeOrders = false;				
		} catch (AccountException e) {		
			throw new BrokerException("Could not close the account through the manager");
		}		
	}

	/**
	 * Create an account with the broker.
	 * 
	 * @param accountName The name of the account.
	 * @param password The password of the account.
	 * @param balance The initial balance in the account.
	 * @param holderName The owner of the account.
	 * @return The account generated from the above parameters.
	 * 
	 * @throws BrokerException throws an exception if there is a duplicate broker.
	 */
	@Override
	public Account createAccount(String accountName, String password, int balance, String holderName) throws BrokerException {
		Account newAccount = null;
		try {
			newAccount = accountManager.createAccount(accountName, password, balance, holderName);
		} catch (AccountException e) {
			throw new BrokerException(e.getLocalizedMessage());
		}
		
		return newAccount;
	}

	/**
	 * Deletes an account with the broker.
	 * 
	 * @param accountName The name of the account to delete.
	 * 
	 * @throws BrokerException The account is not associated with this broker.
	 */
	@Override
	public void deleteAccount(String accountName) throws BrokerException {
		try {
			accountManager.deleteAccount(accountName);
		} catch (AccountException e) {
			throw new BrokerException("Could not delete account: " + e.getMessage());
		}
	}

	/**
	 * Gets the associated account with the broker.
	 * 
	 * @param accountName The name of the account.
	 * @param password The password associated with the account.
	 * 
	 * @throws BrokerException The account name and password combination are invalid or the account is not found.
	 */
	@Override
	public Account getAccount(String accountName, String password) throws BrokerException {
		try {
			if (!accountManager.validateLogin(accountName, password)) {
				return null;
			}
		} catch (AccountException e) {
			throw new BrokerException("Failed to validate login: " + e.getMessage());
		}
		
		try {
			return accountManager.getAccount(accountName);
		} catch (AccountException e) {
			throw new BrokerException("Failed to retrieve the account: " + e.getMessage());
		}
	}

	/**
	 * Gets the name of this broker.
	 * 
	 * @return The name of this broker.
	 */
	@Override
	public String getName() {
		return brokerName;
	}

	/**
	 * Place an order with the broker.
	 * 
	 * @param order The order to place.
	 * 
	 * @throws BrokerException The order failed.
	 */
	@Override
	public void placeOrder(Order order) throws BrokerException {
		if (!canMakeOrders) {
			return;
		}
		
		if (order instanceof StopOrder) {
			String stockTicker = order.getStockTicker();
			orderManagers.get(stockTicker).queueOrder((StopOrder)order);
		} else {
			marketOrders.enqueue(order);
		}
	}

	/**
	 * Get a price quote for the stock.
	 * 
	 * @param ticker The stocks ticker symbol.
	 * 
	 * @return The quote if it's available.
	 */
	@Override
	public Optional<StockQuote> requestQuote(String ticker) {
		return stockExchange.getQuote(ticker);
	}

	/**
	 * Function to hanlde an exchange closing.
	 * 
	 * @param event The event that caused the exchange to close.
	 */
	@Override
	public void exchangeClosed(ExchangeEvent event) {
		try {
			this.close();
		} catch (BrokerException e) {
			// Log out the information
		}
	}

	/**
	 * Function to handle an exchange being opened.
	 * 
	 * @param event The event that caused the exchange to open.
	 */
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		canMakeOrders = true;
	}

	/**
	 * Function that handles price changes
	 * 
	 * @param event The event of a price change.
	 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		// adjust the prices of the manager for the stock
		String stockTicker = event.getTicker();
		orderManagers.get(stockTicker).adjustPrice(event.getPrice());
	}
	
	private void executeOrder(final Order order) {
		final int sharePrice = stockExchange.executeTrade(order);
	    Account acct;
		try {
			acct = accountManager.getAccount(order.getAccountId());
		    if (acct != null) {
		        acct.reflectOrder(order, sharePrice);
		    }
		} catch (AccountException ex) {
			System.out.println("Error: " + ex.getMessage());
		}
    }
}
