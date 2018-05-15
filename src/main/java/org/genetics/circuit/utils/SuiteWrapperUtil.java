package org.genetics.circuit.utils;


import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.EvaluationResult;
import org.genetics.circuit.problem.Evaluator;
import org.genetics.circuit.problem.Suite;
import org.genetics.circuit.problem.TrainingSet;

public class SuiteWrapperUtil {

	public static EvaluationResult evaluate(SuiteWrapper suiteWrapper, Circuit circuit) {
		return evaluate(suiteWrapper.getSuite(), circuit);
	}

	private static EvaluationResult evaluate(Suite suite, Circuit circuit) {
		Evaluator evaluator = suite.getEvaluator();
		TrainingSet trainingSet = suite.getTrainingSet();
		return evaluator.evaluate(trainingSet, circuit);
	}

	public static int getInputSize(SuiteWrapper suiteWrapper) {
		return suiteWrapper.getSuite().getTrainingSet().getInputSize();
	} 
	
	public static boolean useMemory(SuiteWrapper suiteWrapper) {
		return suiteWrapper.getProblem().getUseMemory();
	} 

	public static TrainingSet getTrainingSet(SuiteWrapper suiteWrapper) {
		return suiteWrapper.getSuite().getTrainingSet();
	}
}
