package org.genetics.circuit.random;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomWeight<E> {
	
	private Map<E, Configuration> tableTimeControl =  new HashMap<E, Configuration>();
	
	private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	private double total = 0;

	public void addByWeight(double weight, E result) {
		if (weight <= 0)
			return;
		total += weight;
		map.put(total, result);
	}

	public void addByPeriod(long period, E result) {
		if (this.tableTimeControl.containsKey(result)) {
			throw new RuntimeException("Not possible to have multiple configuration for an element.");
		}
		this.tableTimeControl.put(result, new Configuration(period));
	}

	public E next() {
		// Evaluate the period problem
		for (Map.Entry<E, Configuration> entry : tableTimeControl.entrySet()) {
			long now = System.currentTimeMillis();
			long last = entry.getValue().getLastOccurrence();
			if ((last == -1) || (now - last > entry.getValue().getPeriod())) {
				// entry.getValue().set(1, Long.valueOf(System.currentTimeMillis()));
				entry.getValue().setLastOccurrence(System.currentTimeMillis());
				return entry.getKey();
			}
		}
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		double value = random.nextDouble() * total;
		E chosenOne = map.ceilingEntry(value).getValue();
		Configuration configuration = tableTimeControl.get(chosenOne);
		if (configuration != null) {
			configuration.setLastOccurrence(System.currentTimeMillis());
		}
		return chosenOne;
	}

	private static class Configuration {
		private final long period;
		private long lastOccurrence = -1;

		public Configuration(long period) {
			this.period = period;
		}

		public void setLastOccurrence(long lastOccurrence) {
			this.lastOccurrence = lastOccurrence;
		}

		public long getLastOccurrence() {
			return lastOccurrence;
		}

		public long getPeriod() {
			return period;
		}
	}

}
