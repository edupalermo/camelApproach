package org.genetics.circuit.circuit;

import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.pool.StatePool;
import org.genetics.circuit.problem.EvaluationResult;
import org.genetics.circuit.problem.Suite;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.problem.vowel.VowelEvaluator;
import org.genetics.circuit.solution.Solution;
import org.genetics.circuit.solution.TimeSlice;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class CircuitContextDecorator implements Circuit, Comparable<CircuitContextDecorator> {

    private static final Logger logger = LoggerFactory.getLogger(CircuitContextDecorator.class);

    private final CircuitImpl circuitImpl;
    private final SuiteWrapper suiteWrapper;
    private final boolean wasSimplified;

    private EvaluationResult evaluationResult = null;

    public CircuitContextDecorator(SuiteWrapper suiteWrapper, CircuitImpl circuitImpl) {
        this.suiteWrapper = suiteWrapper;
        this.circuitImpl = circuitImpl;
        this.wasSimplified = false;
    }

    private CircuitContextDecorator(SuiteWrapper suiteWrapper, CircuitImpl circuitImpl, boolean wasSimplified) {
        this.suiteWrapper = suiteWrapper;
        this.circuitImpl = circuitImpl;
        this.wasSimplified = wasSimplified;
    }

    private CircuitContextDecorator(SuiteWrapper suiteWrapper, CircuitImpl circuitImpl, EvaluationResult evaluationResult, boolean wasSimplified) {
        this.suiteWrapper = suiteWrapper;
        this.circuitImpl = circuitImpl;
        this.evaluationResult = evaluationResult;
        this.wasSimplified = wasSimplified;
    }

    public void evaluate() {
        if (this.evaluationResult == null) {
            this.evaluationResult = SuiteWrapperUtil.evaluate(this.suiteWrapper, this.circuitImpl);

            //System.out.println("Output PRI " + Arrays.toString(CircuitOutputGenerator.generateOutput(suiteWrapper.getSuite().getTrainingSet(), this.circuitImpl)));
        }
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

    private EvaluationResult getEvaluationResult() {
        if (this.evaluationResult == null) {
            //logger.warn("Uncommon call to evaluate!");
            this.evaluate();
        }
        return this.evaluationResult;
    }

    @Override
    public int compareTo(CircuitContextDecorator other) {
        int result = this.getEvaluationResult().compareTo(other.getEvaluationResult());

        if (result == 0) {
            for (int i = 0; i < this.circuitImpl.size() && result == 0; i++) {
                result = this.circuitImpl.get(i).compareTo(other.circuitImpl.get(i));
            }
        }
        return result;
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


    public CircuitContextDecorator simplify() {
        CircuitContextDecorator newCcd = this;

        if (!wasSimplified) {
            TrainingSet trainingSet = getSuiteWrapper().getSuite().getTrainingSet();
            CircuitImpl newCircuit = this.circuitImpl.clone();

            if (newCircuit.size() > 5000) { // This is done in better join, but some time it is better to do it first or we can run out of memory
                CircuitUtils.simplifyByRemovingUnsedPorts(trainingSet, newCircuit);
            }
            CircuitUtils.betterSimplify(trainingSet, newCircuit);

            newCcd = new CircuitContextDecorator(this.suiteWrapper, newCircuit, true);
        }

        return newCcd;
    }

    public boolean wasSimplified() {
        return this.wasSimplified;
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


    public CircuitContextDecorator simplifyAndEvaluate() {
        CircuitContextDecorator newCircuitContextDecorator = this;
        if (!this.wasSimplified) {

            TrainingSet trainingSet = getSuiteWrapper().getSuite().getTrainingSet();
            CircuitImpl newCircuit = this.circuitImpl.clone();

            EvaluationResult evaluationResult = suiteWrapper.getSuite().getEvaluator().simplifyAndEvaluate(trainingSet, newCircuit);

            newCircuitContextDecorator = new CircuitContextDecorator(this.suiteWrapper, newCircuit, evaluationResult, true);
        }

        return newCircuitContextDecorator;
    }
}
