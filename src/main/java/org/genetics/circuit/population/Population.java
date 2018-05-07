package org.genetics.circuit.population;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {

    private static final Logger logger = LoggerFactory.getLogger(Population.class);

    private List<Circuit> population = new ArrayList<Circuit>();
    private final SuiteWrapper suiteWrapper;

    public Population(SuiteWrapper suiteWrapper) {
        this.suiteWrapper = suiteWrapper;
    }

    public void add(Circuit circuit) {
        if (orderedAdd(population, suiteWrapper, circuit) == 0) {
            logger.info("NEW BEST!");
        }
    }

    public int orderedAdd(List<Circuit> population, SuiteWrapper suiteWrapper, Circuit newCircuit) {

        if (newCircuit == null) {
            throw new RuntimeException("Cannot add null circuit!");
        }

        if (suiteWrapper == null) {
            throw new RuntimeException("Comparator cannot be null!");
        }

        if (population == null) {
            throw new RuntimeException("Population cannot be null!");
        }

        int pos = Collections.binarySearch(population, newCircuit, suiteWrapper.getSuite().getCircuitComparator());
        if (pos < 0) {
            pos = ~pos;
            population.add(pos, newCircuit);
        } else {
            pos = -1;
        }
        return pos;
    }

}
