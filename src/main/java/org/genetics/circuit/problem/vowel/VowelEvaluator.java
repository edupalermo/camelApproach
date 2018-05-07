package org.genetics.circuit.problem.vowel;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitHitsEvaluator;
import org.genetics.circuit.problem.Evaluator;
import org.genetics.circuit.problem.TrainingSet;

import java.io.Serializable;

public class VowelEvaluator implements Evaluator, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public void evaluate(TrainingSet trainingSet, Circuit circuit) {
		circuit.setGrade(VowelSuite.GRADE_HIT, CircuitHitsEvaluator.evaluate(trainingSet, circuit));
		circuit.setGrade(VowelSuite.GRADE_CIRCUIT_SIZE, Integer.valueOf(circuit.size()));
	}
	
}
