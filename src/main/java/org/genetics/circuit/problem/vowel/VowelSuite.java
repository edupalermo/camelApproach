package org.genetics.circuit.problem.vowel;

import org.genetics.circuit.problem.CircuitComparator;
import org.genetics.circuit.problem.Evaluator;
import org.genetics.circuit.problem.Suite;
import org.genetics.circuit.problem.TrainingSet;

public class VowelSuite implements Suite {
	private static final long serialVersionUID = 1L;
	
	public final static String GRADE_HIT = "GRADE_HIT";
	public final static String GRADE_CIRCUIT_SIZE = "GRADE_CIRCUIT_SIZE";
	
	private Evaluator evaluator = new VowelEvaluator();
	
	private TrainingSet trainingSet = new VowelTrainingSet();
	
	private CircuitComparator circuitComparator = new VowelComparator();

	@Override
	public Evaluator getEvaluator() {
		return this.evaluator;
	}

	@Override
	public TrainingSet getTrainingSet() {
		return this.trainingSet;
	}

	@Override
	public CircuitComparator getCircuitComparator() {
		return this.circuitComparator;
	}
	
	

}
