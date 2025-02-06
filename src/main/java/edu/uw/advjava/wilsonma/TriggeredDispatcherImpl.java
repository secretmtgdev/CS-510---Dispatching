package edu.uw.advjava.wilsonma;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import edu.uw.pce.advjava.services.TriggeredDispatcher;

public class TriggeredDispatcherImpl<E, T> implements TriggeredDispatcher<E, T> {
	private TreeSet<E> priorityQueue;
	private T threshold;
	private Consumer<? super E> dispatchAction;
	private BiPredicate<E, T> filter;
	
	public TriggeredDispatcherImpl(Comparator<E> cmp, BiPredicate<E, T> filter, T initialThreshold) {
		this.priorityQueue = new TreeSet<>(cmp);
		this.filter = filter;
		this.threshold = initialThreshold;
	}
	
	@Override
	public E dequeue() {
		// Avoid polling from an empty or non-dispatchable tree
		if (this.priorityQueue.isEmpty() || !this.isDispatchAvailable() || !this.canDispatch()) {
			return null;
		}
		
		// Removes and returns the smallest element: O(log(n))
		// Element should be dispatchable
		return this.priorityQueue.pollFirst(); 
	}

	@Override
	public void enqueue(E element) {
		// Duplicates aren't allowed and sorting will be based off of <E>
		if (this.isDispatchAvailable() && this.canDispatch(element)) {
			// this performs the action (printing content or using for math) on the element
			this.dispatchAction.accept(element);
		} else {
			// Add the element and dispatch the available dispatchable elements
			this.priorityQueue.add(element);
			this.dispatchOrders();	
		}
	}

	@Override
	public T getThreshold() {
		return this.threshold;
	}

	@Override
	public E peek() {
		// avoid throwing an error when the tree is empty
		if (this.priorityQueue.isEmpty()) {
			return null;
		}
		
		// Doesn't remove and gets the topmost element in the tree
		return this.priorityQueue.first();
	}

	@Override
	public void setDispatchAction(Consumer<? super E> consumer) {
		this.dispatchAction = consumer;
	}

	@Override
	public void setThreshold(T newThreshold) {
		this.threshold = newThreshold;
	}

	@Override
	public int size() {
		return this.priorityQueue.size();
	}
	
	private void dispatchOrders() {
		while (!this.priorityQueue.isEmpty()) {
			if (this.isDispatchAvailable() && this.canDispatch()) {
				this.priorityQueue.pollFirst();				
			} else {
				break;
			}
		}
	}
	
	// Want to ensure that we don't attempt to dispatch when a predicate or threshold are invalid
	private boolean isDispatchAvailable() {
		return this.dispatchAction != null && this.filter != null && this.threshold != null;
	}
	
	// Checks to see if the first element in the tree can be dispatched
	// Would have loved for this to be in one function with a defualt parameter
	private boolean canDispatch() {
		return this.filter.test(this.priorityQueue.first(), this.threshold);
	}
	
	// Checks to see if an element can be dispatched
	private boolean canDispatch(E element) {
		return this.filter.test(element, this.threshold);
	}
}
