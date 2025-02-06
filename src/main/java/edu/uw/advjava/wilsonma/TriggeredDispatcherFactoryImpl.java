package edu.uw.advjava.wilsonma;

import java.util.Comparator;
import java.util.function.BiPredicate;

import edu.uw.pce.advjava.services.TriggeredDispatcher;
import edu.uw.pce.advjava.services.TriggeredDispatcherFactory;

public class TriggeredDispatcherFactoryImpl implements TriggeredDispatcherFactory {

	@Override
	public <E, T> TriggeredDispatcher<E, T> getTestInstance(Comparator<E> cmp, BiPredicate<E, T> filter, T initialThreshold) {
		TriggeredDispatcher<E, T> dispatchInstance = new TriggeredDispatcherImpl<E, T>(cmp, filter, initialThreshold);
		return dispatchInstance;
	}

}
