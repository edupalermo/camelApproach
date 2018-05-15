package org.genetics.camel.mediator;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class MemoryPopulationMediator {

    private static final Logger logger = LoggerFactory.getLogger(MemoryPopulationMediator.class);

    private static final List<CircuitContextDecorator> population = new ArrayList<CircuitContextDecorator>();
    private static String generationId = UUID.randomUUID().toString();
    private static int actualSuiteWrapperId = -1;

    private static final int POPULATION_MAX_SIZE = 10000;
    private static final int DUMP_LIMIT_MIN = 20;

    public synchronized void dump(SuiteWrapper suiteWrapper) {
        if (population.size() == 0) {
            logger.warn("No element in the population.");
            return;
        }

        logger.info(String.format("Population identification %s", generationId));

        Circuit betterCircuit = population.get(0);

        for (int i = 0; i < Math.min(DUMP_LIMIT_MIN, population.size()); i++) {
            logger.info(String.format("[%5d] %s", i + 1, population.get(i).toString()));
            // logger.info(String.format("[%5d] %s", i + 1, CircuitToString.toString(evaluator, population.get(i))));
        }

        if (population.size() > 3) {
            int limit = Math.min(POPULATION_MAX_SIZE, population.size());
            for (int i = limit - 3; i < limit; i++) {
                logger.info(String.format("[%5d] %s %.3f", i + 1, population.get(i).toString(), betterCircuit.similarity(population.get(i))));
            }
        }

        int populationSize = population.size();
        int totalHits = CircuitUtils.getTotalOfPossibleHits(suiteWrapper);

        logger.info(String.format("Population [%d] Total Hits [%d]", populationSize, totalHits));
    }

    public synchronized int orderedAdd(String initialGenerationId, CircuitContextDecorator newCircuit) {

        if (!initialGenerationId.equals(this.generationId)) {
            return -1;
        }

        if (newCircuit == null) {
            throw new RuntimeException("Cannot add null circuit!");
        }

        if (population == null) {
            throw new RuntimeException("Population cannot be null!");
        }

        if (!hasTopPosition(newCircuit)) {
            return -1;
        }

        int pos = Collections.binarySearch(population, newCircuit);
        if (pos < 0) {
            pos = ~pos;
            if (pos <= POPULATION_MAX_SIZE) {
                population.add(pos, newCircuit);
            }
        } else {
            pos = -1;
        }

        while (population.size() > POPULATION_MAX_SIZE) {
            population.remove(population.size() - 1);
        }

        return pos;
    }

    private int orderedAdd() {

        return 0;
    }

    public synchronized CircuitContextDecorator getFirst() {
        CircuitContextDecorator circuit = null;
        if (population.size() > 0) {
            circuit = population.get(0);
        }
        return circuit;
    }

    public synchronized Circuit getLast() {
        Circuit circuit = null;
        if (population.size() > 0) {
            circuit = population.get(population.size() - 1);
        }
        return circuit;
    }

    public synchronized CircuitContextDecorator getWeightedRandom() {
        CircuitContextDecorator circuit = null;
        if (population.size() > 0) {
            circuit = population.get(RandomUtils.raffle(population.size()));
        }
        return circuit;
    }

    private boolean hasTopPosition(CircuitContextDecorator newCircuit) {

        boolean hasTopPosition = true;

        if (population.size() >= POPULATION_MAX_SIZE) {
            if (newCircuit.compareTo(population.get(population.size() - 1)) >= 0) {
                hasTopPosition = false;
            }
        }
        return hasTopPosition;
    }

    public synchronized void reset() {
        population.clear();
        this.generationId = UUID.randomUUID().toString();
    }

    public synchronized String getGenerationId() {
        return this.generationId;
    }

    /*
    public synchronized int getSize() {
        return this.population.size();
    }
    */

}