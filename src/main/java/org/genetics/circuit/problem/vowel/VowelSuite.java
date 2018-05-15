package org.genetics.circuit.problem.vowel;

import org.genetics.circuit.problem.Evaluator;
import org.genetics.circuit.problem.Suite;
import org.genetics.circuit.problem.TrainingSet;

public class VowelSuite implements Suite {
	private static final long serialVersionUID = 1L;
	
	private Evaluator evaluator = new VowelEvaluator();
	
	private TrainingSet trainingSet = new VowelTrainingSet();
	
	@Override
	public Evaluator getEvaluator() {
		return this.evaluator;
	}

	@Override
	public TrainingSet getTrainingSet() {
		return this.trainingSet;
	}

}
