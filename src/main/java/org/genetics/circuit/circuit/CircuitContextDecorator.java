package org.genetics.circuit.circuit;

import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.EvaluationResult;
import org.genetics.circuit.utils.SuiteWrapperUtil;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class CircuitContextDecorator implements Circuit, Comparable<CircuitContextDecorator> {

    private final CircuitImpl circuitImpl;

    private SuiteWrapper suiteWrapper = null;
    private EvaluationResult evaluationResult = null;

    public CircuitContextDecorator(CircuitImpl circuitImpl) {
        this.circuitImpl = circuitImpl;
    }

    public void evaluate(SuiteWrapper suiteWrapper) {
        this.suiteWrapper = suiteWrapper;
        this.evaluationResult = SuiteWrapperUtil.evaluate(suiteWrapper, this.circuitImpl);
    }

    public CircuitImpl clone() {
        return this.circuitImpl.clone();
    }

    @Override
    public String toString() {
        String toString = null;

        if (evaluationResult == null) {
            toString = "Not evaluated!";
        } else {
            toString = this.evaluationResult.toString();
        }

        return toString;
    }

    public EvaluationResult getEvaluationResult() {
        return this.evaluationResult;
    }

    @Override
    public int compareTo(CircuitContextDecorator other) {
        return evaluationResult.compareTo(other.getEvaluationResult());
    }

    public CircuitImpl getRootCircuit() {
        return this.circuitImpl;
    }

    @Override
    public double similarity(Circuit other) {
        if (!(other instanceof CircuitContextDecorator)) {
            throw new RuntimeException("It should be a CircuitContextDecorator!");
        }

        double similarity = evaluationResult.similarity(((CircuitContextDecorator)other).getEvaluationResult());

        if (similarity == 1) {
            similarity = this.circuitImpl.similarity(((CircuitContextDecorator)other).getRootCircuit());
        }

        return similarity;
    }

    public SuiteWrapper getSuiteWrapper() {
        return this.suiteWrapper;
    }


    @Override
    public boolean[] generateInitialState() {
        return this.circuitImpl.generateInitialState();
    }

    @Override
    public void reset() {
        this.circuitImpl.reset();
    }

    @Override
    public void assignInputToState(boolean[] state, boolean[] input) {
        this.circuitImpl.assignInputToState(state, input);
    }

    @Override
    public void propagate(boolean[] state) {
        this.circuitImpl.propagate(state);
    }

}
