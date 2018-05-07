package org.genetics.circuit.problem;

import java.io.Serializable;

public interface Suite extends Serializable {

	Evaluator getEvaluator();
	
	TrainingSet getTrainingSet();
	
	CircuitComparator getCircuitComparator();
	
}
