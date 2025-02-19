package edu.uw.advjava.wilsonma;

import java.util.Comparator;
import java.util.function.Consumer;

import edu.uw.pce.advjava.services.OrderManager;
import edu.uw.pce.advjava.support.broker.StopOrder;

/**
 * Implementation of the OrderManager interface.
 * 
 * @author Michael Wilson
 */
public class OrderManagerImpl implements OrderManager {
	private final String tickerSymbol;
	
	private TriggeredDispatcherImpl<StopOrder, Integer> buyStopOrderDispatcher;
	private TriggeredDispatcherImpl<StopOrder, Integer> sellStopOrderDispatcher;
	
	/**
	 * Constructs a new instance of an OrderManager.
	 * 
	 * @param targetPrice The threshold price for dispatching the orders.
	 * @param tickerSymbol The symbol
	 */
	public OrderManagerImpl(int targetPrice, String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
		Comparator<StopOrder> buyStopComparator = Comparator
				.comparing(StopOrder::getPrice)				
				.thenComparing(StopOrder::compareTo);
		buyStopOrderDispatcher = new TriggeredDispatcherImpl<StopOrder, Integer>(buyStopComparator, 
				(order, threshold) -> order.getPrice() <= threshold,
				targetPrice);
		
		Comparator<StopOrder> sellStopComparator = Comparator
				.comparing(StopOrder::getPrice)
				.reversed()
				.thenComparing(StopOrder::compareTo);
		sellStopOrderDispatcher = new TriggeredDispatcherImpl<StopOrder, Integer>(sellStopComparator, 
				(order, threshold) -> order.getPrice() >= threshold,
				targetPrice);
	}

	/**
	 * Respond to a stock price adjustment by setting the threshold.
	 * 
	 * @param newPrice The new price threshold.
	 */
	@Override
	public void adjustPrice(int newPrice) {
		buyStopOrderDispatcher.setThreshold(newPrice);
		sellStopOrderDispatcher.setThreshold(newPrice);
	}

	/**
	 * Gets the stock ticker symbol for the stock managed by this stock manager.
	 * 
	 * @return The stock ticker symbol.
	 */
	@Override
	public String getSymbol() {
		return tickerSymbol;
	}

	/**
	 * Queues a stop order either in the buy or sell dispatcher.
	 * 
	 * @param order The order to be queued.
	 */
	@Override
	public void queueOrder(StopOrder order) {
		if (order.isBuyOrder()) {
			buyStopOrderDispatcher.enqueue(order);
		} else {
			sellStopOrderDispatcher.enqueue(order);
		}
	}

	/**
	 * Registers the processor to be used during stop order processing. 
	 * 
	 * @param consumer The callback to register to the dispatcher
	 */
	@Override
	public void setStopOrderProcessor(Consumer<StopOrder> consumer) {		
		buyStopOrderDispatcher.setDispatchAction(consumer);
		sellStopOrderDispatcher.setDispatchAction(consumer);
	}

}
