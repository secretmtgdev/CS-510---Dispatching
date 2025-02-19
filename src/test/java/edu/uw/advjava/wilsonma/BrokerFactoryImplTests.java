package edu.uw.advjava.wilsonma;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uw.pce.advjava.services.Broker;
import edu.uw.pce.advjava.support.account.AccountManager;
import edu.uw.pce.advjava.support.account.MapAccountDao;
import edu.uw.pce.advjava.support.broker.BrokerException;
import edu.uw.pce.advjava.support.broker.ClientOrder;
import edu.uw.pce.advjava.support.broker.MarketBuyOrder;
import edu.uw.pce.advjava.support.broker.MarketSellOrder;
import edu.uw.pce.advjava.support.broker.StopBuyOrder;
import edu.uw.pce.advjava.support.broker.StopSellOrder;
import edu.uw.pce.advjava.support.exchange.FauxMarket;
import edu.uw.pce.advjava.support.exchange.MarketException;
import edu.uw.pce.advjava.support.exchange.SimpleExchange;
import edu.uw.pce.advjava.support.exchange.StockExchange;
import edu.uw.pce.advjava.support.exchange.StockQuote;

public class BrokerFactoryImplTests {
	private final FauxMarket DEFAULT_FAUX_MARKET = new FauxMarket();
	private final String DEFAULT_NAME = "Michael";
	private final AccountManager DEFAULT_MANAGER = new AccountManager(new MapAccountDao());
	private final StockExchange DEFAULT_STOCK_EXCHANGE = new SimpleExchange(DEFAULT_FAUX_MARKET);
	
	private final String DEFAULT_ACCOUNT_NAME = "test_account_name";
	private final String DEFAULT_PASSWORD = "test_password";
	private final int DEFAULT_BALANCE = 1000000;
	
	private final int DEFAULT_NUMBER_OF_SHARES = 10;
	private final String DEFAULT_STOCK_TICKER = DEFAULT_FAUX_MARKET.SYMBOL_UMBL;
	private final int DEFAULT_STOP_PRICE = 5;
	
	private final MarketBuyOrder marketBuyOrder = new MarketBuyOrder(DEFAULT_ACCOUNT_NAME, DEFAULT_NUMBER_OF_SHARES, DEFAULT_STOCK_TICKER);
	private final MarketSellOrder marketSellOrder = new MarketSellOrder(DEFAULT_ACCOUNT_NAME, DEFAULT_NUMBER_OF_SHARES, DEFAULT_STOCK_TICKER);
	private final StopBuyOrder stopBuyOrder = new StopBuyOrder(DEFAULT_ACCOUNT_NAME, DEFAULT_NUMBER_OF_SHARES, DEFAULT_STOCK_TICKER, DEFAULT_STOP_PRICE);
	private final StopSellOrder stopSellOrder = new StopSellOrder(DEFAULT_ACCOUNT_NAME, DEFAULT_NUMBER_OF_SHARES, DEFAULT_STOCK_TICKER, DEFAULT_STOP_PRICE);
	
	private Broker defaultBroker;
	
	// This should not throw an exception
	@BeforeEach
	public void init() throws BrokerException {
		BrokerFactoryImpl brokerFactory = new BrokerFactoryImpl();
		defaultBroker = brokerFactory.newBroker(DEFAULT_NAME, DEFAULT_MANAGER, DEFAULT_STOCK_EXCHANGE);
		if (defaultBroker.getAccount(DEFAULT_ACCOUNT_NAME, DEFAULT_PASSWORD) == null) {			
			defaultBroker.createAccount(DEFAULT_ACCOUNT_NAME, DEFAULT_PASSWORD, DEFAULT_BALANCE, DEFAULT_NAME);
		}
	}
	
	@AfterEach
	public void clean() {
		defaultBroker = null;
	}
	
	@Test
	public void testConstructor() {
		Broker testBroker = new BrokerImpl(DEFAULT_NAME, DEFAULT_MANAGER, DEFAULT_STOCK_EXCHANGE);
		assertEquals(defaultBroker.getName(), testBroker.getName(), "Expected the name to be " + DEFAULT_NAME);
	}
	
	// This should not throw an exception
	@Test	
	public void testClose() throws BrokerException {
		assertDoesNotThrow(() -> defaultBroker.close());
	}
	
	@Test
	public void testCloseNullAccount() throws BrokerException {
		assertDoesNotThrow(() -> defaultBroker.close());
		assertThrows(NullPointerException.class, () -> defaultBroker.close());
	}
	
	@Test
	public void testCreateAccountDuplicate() {
		assertThrows(BrokerException.class, () ->
			defaultBroker.createAccount(DEFAULT_ACCOUNT_NAME, DEFAULT_PASSWORD, DEFAULT_BALANCE, DEFAULT_NAME)
		);
	}
	
	// Should not throw an error as these are valid cases
	@Test
	public void testDeleteAccount() throws BrokerException {
		defaultBroker.deleteAccount(DEFAULT_ACCOUNT_NAME);
	}
	
	// Should not throw an error
	@Test
	public void testGetAccount() throws BrokerException {
		defaultBroker.getAccount(DEFAULT_ACCOUNT_NAME, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testGetName() {
		assertEquals(DEFAULT_NAME, defaultBroker.getName(), "Expected the broker name to be " + DEFAULT_NAME);
	}
	
	// Should not throw an exception assuming that these are valid orders
	@Test
	public void placeOrder() throws BrokerException {
		defaultBroker.placeOrder(marketBuyOrder);
		defaultBroker.placeOrder(marketSellOrder);
		defaultBroker.placeOrder(stopBuyOrder);
		defaultBroker.placeOrder(stopSellOrder);
	}
	
	// Should not throw an error
	@Test
	public void requestQuote() throws MarketException {
		DEFAULT_FAUX_MARKET.adjustPrice(DEFAULT_STOCK_TICKER, DEFAULT_STOP_PRICE);
		Optional<StockQuote> requestedQuote = defaultBroker.requestQuote(DEFAULT_STOCK_TICKER);
		assertTrue(requestedQuote.isPresent());
		StockQuote actualQuote = requestedQuote.get();
		assertEquals(DEFAULT_STOP_PRICE, actualQuote.getPrice(), "Expected stock price to be " + DEFAULT_STOP_PRICE);
		assertEquals(DEFAULT_STOCK_TICKER, actualQuote.getTicker(), "Expected ticker to be " + DEFAULT_STOCK_TICKER);
	}
	
	@Test
	public void requestQuoteEmpty() {
		final String INVALID_STOCK_TICKER = "BIONICLES_INC";
		Optional<StockQuote> requestedQuote = defaultBroker.requestQuote(INVALID_STOCK_TICKER);
		assertTrue(requestedQuote.isEmpty());
	}
}

