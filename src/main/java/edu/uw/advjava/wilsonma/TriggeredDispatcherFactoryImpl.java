package edu.uw.advjava.wilsonma;

import java.util.Comparator;
import java.util.function.BiPredicate;

import edu.uw.pce.advjava.services.TriggeredDispatcher;
import edu.uw.pce.advjava.services.TriggeredDispatcherFactory;

/**
 * Implementation of the TriggeredDispatherFactory.
 * 
 * @author Michael Wilson
 */
public class TriggeredDispatcherFactoryImpl implements TriggeredDispatcherFactory {
	/**
	 * Default constructor. Used for getting a test instance of TriggeredDispatcher.
	 */
	public TriggeredDispatcherFactoryImpl() {
		
	}
	
	/**
	 * Gets a generic instance of the TriggeredDispatcher.
	 *
	 * @param cmp The comparator.
	 * @param filter The filter function.
	 * @param initialThreshold The threshold to determine dispatching.
	 * 
	 * @return An instance of the triggered dispatcher given the comparator, bipredicate, and threshold.
	 */
	@Override
	public <E, T> TriggeredDispatcher<E, T> getTestInstance(Comparator<E> cmp, BiPredicate<E, T> filter, T initialThreshold) {
		TriggeredDispatcher<E, T> dispatchInstance = new TriggeredDispatcherImpl<E, T>(cmp, filter, initialThreshold);
		return dispatchInstance;
	}

}
