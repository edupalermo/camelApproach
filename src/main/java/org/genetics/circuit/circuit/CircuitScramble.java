package org.genetics.circuit.circuit;

import org.genetics.circuit.port.Port;
import org.genetics.circuit.port.PortInput;
import org.genetics.circuit.problem.TrainingSet;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class CircuitScramble {

	// Circuit c1 is modified!
	public static CircuitImpl join(CircuitImpl c1, CircuitImpl c2) {
		if (c1 == c2) {
			c2 = c1.clone();
		}
		CircuitImpl answer = realJoin(c1, c2);
		
		return answer;
	}
	
	public static CircuitImpl mix(CircuitImpl c1, CircuitImpl c2) {
		if (c1 == c2) {
			c2 = c1.clone();
		}
		CircuitImpl answer = realMix(c1, c2);
		
		return answer;
	}
	
	
	private static CircuitImpl realMix(CircuitImpl c1, CircuitImpl c2) {
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		Map<Integer, Integer> translation = new TreeMap<Integer, Integer>();
		
		final int inputSize = getInputSize(c1);
		
		for (int i = 0; i < inputSize; i++) {
			translation.put(i, random.nextInt(c1.size()));
		}
		
		for (int i = inputSize; i < c2.size(); i++) {
			
			Port port = (Port) c2.get(i).clone();
			
			port.translate(translation);
			
			translation.put(i, c1.size());
			
			c1.add(port);
		}
		return c1;
	}

	private static CircuitImpl realJoin(CircuitImpl c1, CircuitImpl c2) {
		
		Map<Integer, Integer> translation = new TreeMap<Integer, Integer>();
		
		final int inputSize = getInputSize(c1);
		
		for (int i = 0; i < inputSize; i++) {
			translation.put(i, i);
		}
		
		for (int i = inputSize; i < c2.size(); i++) {
			Port port = (Port) c2.get(i).clone();
			port.translate(translation);
			translation.put(i, c1.size());
			c1.add(port);
		}
		return c1;
	}

	
	
	private static int getInputSize(CircuitImpl c) {
		int i = 0;
		
		while ((i < c.size()) && (c.get(i) instanceof PortInput)) {
			i++;
		};
		
		return i;
	}
	
	
	

}
