package org.genetics.circuit.utils;


import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.CircuitComparator;
import org.genetics.circuit.problem.Evaluator;
import org.genetics.circuit.problem.Suite;
import org.genetics.circuit.problem.TrainingSet;

public class SuiteWrapperUtil {

	public static void evaluate(SuiteWrapper suiteWrapper, Circuit circuit) {
		evaluate(suiteWrapper.getSuite(), circuit);
	}

	public static void evaluate(Suite suite, Circuit circuit) {
		Evaluator evaluator = suite.getEvaluator();
		TrainingSet trainingSet = suite.getTrainingSet();
		evaluator.evaluate(trainingSet, circuit);
	}

	public static int compare(SuiteWrapper suiteWrapper, Circuit c1, Circuit c2){
		CircuitComparator comparator = suiteWrapper.getSuite().getCircuitComparator();
		return comparator.compare(c1, c2);
	}

	public static int getInputSize(SuiteWrapper suiteWrapper) {
		return suiteWrapper.getSuite().getTrainingSet().getInputSize();
	} 
	
	public static boolean useMemory(SuiteWrapper suiteWrapper) {
		return suiteWrapper.getProblem().getUseMemory();
	} 
	
	public static double similarity(SuiteWrapper suiteWrapper, Circuit c1, Circuit c2) {
		return suiteWrapper.getSuite().getCircuitComparator().similarity(c1, c2);
	}
	
	public static TrainingSet getTrainingSet(SuiteWrapper suiteWrapper) {
		return suiteWrapper.getSuite().getTrainingSet();
	}
}
