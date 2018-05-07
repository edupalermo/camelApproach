package org.genetics.circuit.problem;

import org.genetics.circuit.solution.Solution;

import java.io.Serializable;
import java.util.List;

public interface TrainingSet extends Serializable {

	int getInputSize();
	
	int getOutputSize();

	List<Solution> getSolutions();

}
