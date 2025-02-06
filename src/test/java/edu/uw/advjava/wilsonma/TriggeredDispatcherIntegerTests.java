package edu.uw.advjava.wilsonma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TriggeredDispatcherIntegerTests {
	private final int DEFAULT_THRESHOLD = 5;
	private TriggeredDispatcherImpl<Integer, Integer> dispatcher;
	
	@BeforeEach
	public void init() {
		BiPredicate<Integer, Integer> filter = (num, threshold) -> num >= threshold;		
		this.dispatcher = new TriggeredDispatcherImpl<>(
				Comparator.naturalOrder(),
				filter,
				DEFAULT_THRESHOLD
		);
	}
	
	@Test
	public void verifyConstructor() {
		assertEquals(dispatcher.getThreshold(), DEFAULT_THRESHOLD, "The threshold should be " + DEFAULT_THRESHOLD);
	}
	
	@AfterEach
	public void clean() {
		dispatcher = null;
	}
	
	@Test
	public void verifyEnqueueWithoutDispatchedItem() {
		dispatcher.enqueue(1);
		assertEquals(1, dispatcher.size(), "One item should be in the queue.");
	}
	
	@Test
	public void verifyEnqueueWithDispatchedItem() {
		Consumer<Integer> consumer = num -> System.out.println("Dispatched: " + num);
		dispatcher.setDispatchAction(consumer);
		dispatcher.enqueue(10);
		assertEquals(0, dispatcher.size(), "Nothing should be in the queue.");
	}
	
	@Test
	public void verifyDequeueOnEmpty() {
		assertEquals(null, dispatcher.dequeue(), "Nothing to dispatch, should be null.");
	}
	
	@Test
	public void verifyDequeueOnNoDispatchAction() {
		int val = 1;
		dispatcher.enqueue(val);
		assertEquals(null, dispatcher.dequeue(), "No dispatch acition available, should be null.");
	}
	
	@Test
	public void verifyDequeueOnThresholdChange() {
		Consumer<Integer> consumer = num -> System.out.println("Dispatched: " + num);
		dispatcher.setDispatchAction(consumer);
		int val = 1;
		dispatcher.enqueue(val);
		dispatcher.setThreshold(val);
		assertEquals(val, dispatcher.dequeue(), "Should dispatch one now.");
	}
	
	@Test
	public void verifyGetThreshold() {
		int threshold = dispatcher.getThreshold();
		assertEquals(DEFAULT_THRESHOLD, threshold, "Threhsold expected to be " + DEFAULT_THRESHOLD + " but was instead " + threshold);
	}
	
	@Test
	public void verifySetThreshold() {
		int newThreshold = 1000;
		dispatcher.setThreshold(newThreshold);
		int actualThreshold = dispatcher.getThreshold();
		assertEquals(actualThreshold, newThreshold, "Threshold expected to be " + newThreshold + " but was instead " + actualThreshold);
	}
	
	@Test
	public void verifyPeekReturnsFirst() {
		dispatcher.enqueue(1);
		assertEquals(1, dispatcher.peek(), "Should return 1.");
	}
	
	@Test
	public void verifyPeekReturnsNull() {
		assertEquals(null, dispatcher.peek(), "Should return null value.");
	}
	
	@Test
	public void verifySizeWithoutDispatch() {
		verifySizeHelper(5);
	}
	
	@Test
	public void verifySizeWithAutoDispatch() {
		verifySizeHelper(10);
	}
	
	private void verifySizeHelper(int n) {
		for (int i = 0; i < n; i++) {
			dispatcher.enqueue(i);
		}
		
		assertEquals(n, dispatcher.size(), "There should be " + (n < DEFAULT_THRESHOLD ? n : n - DEFAULT_THRESHOLD) + " items in the queue");
	}
	
}