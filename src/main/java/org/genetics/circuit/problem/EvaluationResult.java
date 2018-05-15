package org.genetics.circuit.problem;

public interface EvaluationResult<T> extends Comparable<T> {

    public double similarity(T other);

}
