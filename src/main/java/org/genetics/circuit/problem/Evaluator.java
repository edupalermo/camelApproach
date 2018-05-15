package org.genetics.circuit.problem;

import org.genetics.circuit.circuit.Circuit;

import java.io.Serializable;


public interface Evaluator extends Serializable {

	EvaluationResult evaluate(TrainingSet trainingSet, Circuit circuit);
	
}
