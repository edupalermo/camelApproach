package org.genetics.circuit.circuit;


import org.genetics.circuit.port.Port;

public class CircuitRandomGenerator {
	
	public static CircuitImpl randomGenerate(int inputSize, int quantityOfRandomPort, boolean useMemory) {
		CircuitImpl circuit = new CircuitImpl(inputSize);
		for (int i = 0; i < quantityOfRandomPort; i++) {
			circuit.add(Port.random(circuit.size(), useMemory));
		}
		
		return circuit;
	}
	
	public static void randomEnrich(CircuitImpl circuit, int quantityOfRandomPort, boolean useMemory) {
		for (int i = 0; i < quantityOfRandomPort; i++) {
			circuit.add(Port.random(circuit.size(), useMemory));
		}
	}
	
	

}
