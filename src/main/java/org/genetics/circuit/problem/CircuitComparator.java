package org.genetics.circuit.problem;

import org.apache.commons.lang3.tuple.Pair;
import org.genetics.circuit.circuit.Circuit;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public interface CircuitComparator extends Comparator<Circuit>, Serializable {

	List<Pair<String, Boolean>> getOrders();
	
	double similarity(Circuit c1, Circuit c2);

}
