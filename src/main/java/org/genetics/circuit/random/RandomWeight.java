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
		long now = System.currentTimeMillis();
		E entity = null;
		long longestGap = 0;
		for (Map.Entry<E, Configuration> entry : tableTimeControl.entrySet()) {
			long last = entry.getValue().getLastOccurrence();
			if (last == -1)  {
				longestGap = Long.MAX_VALUE - entry.getValue().getPeriod();
				entity = entry.getKey();
			}
			else {
				long gap = now - last;
				if ((gap >= entry.getValue().getPeriod()) && (gap > longestGap)) {
					longestGap = gap;
					entity = entry.getKey();
				}
			}
		}
		if (entity == null) {
			entity = getByWeight(now);
		}

		Configuration configuration = tableTimeControl.get(entity);
		if (configuration != null) {
			configuration.setLastOccurrence(now);
		}

		return entity;
	}

	private E getByWeight(long now) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
	}

	public double getTotal() {
		return this.total;
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

	public static void main(String arg[]) throws Exception {

		RandomWeight<String> randomWeight = new RandomWeight<String>();

		randomWeight.addByWeight(100, "Comum");
		randomWeight.addByWeight(5, "Raro");
		randomWeight.addByWeight(1, "Lendario");

		/*
		randomWeight.addByPeriod(10, "Lendario");
		randomWeight.addByPeriod(1, "Raro");
		randomWeight.addByPeriod(100, "Impossible");
		*/

		Map<String, Long> stat = new TreeMap<String, Long>();

		int loop = 10000000;

		for (int i = 0; i < loop; i++) {

			String item = randomWeight.next();

			if (stat.containsKey(item)) {
				stat.put(item, stat.get(item).longValue() + 1);
			}
			else {
				stat.put(item, 1l);
			}

			//System.out.println(randomWeight.next());
			//Thread.currentThread().sleep(1);
		}

		for (String item : stat.keySet()) {
			double perc = randomWeight.getTotal()  * stat.get(item).doubleValue() / (double)loop;
			System.out.println(String.format("%10s - %10d - %3.3f", item, stat.get(item), perc));
		}


	}

}
