package org.genetics.circuit.dao;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.CircuitWrapper;
import org.genetics.circuit.entity.SuiteWrapper;

public interface CircuitWrapperDao {
	
    CircuitWrapper create(SuiteWrapper suiteWrapper, Circuit circuit, int position);

	Circuit findByPosition(SuiteWrapper suiteWrapper, int position);

	void updatePositions(SuiteWrapper suiteWrapper, int position);

	int getTotal(SuiteWrapper suiteWrapper);

}
