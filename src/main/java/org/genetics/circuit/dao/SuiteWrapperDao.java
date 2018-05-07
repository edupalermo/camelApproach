package org.genetics.circuit.dao;


import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.Suite;

public interface SuiteWrapperDao {
	
    SuiteWrapper create(Problem problem, Suite suite);

    SuiteWrapper findLatest(Problem problem);

}
