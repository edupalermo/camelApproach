package org.genetics.circuit.service;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitToString;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.CircuitComparator;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.RandomUtils;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class PopulationService {

    private static final Logger logger = LoggerFactory.getLogger(PopulationService.class);

    private static final List<Circuit> population = new ArrayList<Circuit>();
    private static final String uuid = UUID.randomUUID().toString();

    private static final int POPULATION_MAX_SIZE = 10000;
    private static final int DUMP_LIMIT_MIN = 10;

    public synchronized void dump(SuiteWrapper suiteWrapper) {
        if (population.size() == 0) {
            logger.warn("No element in the population.");
            return;
        }

        logger.info(String.format("Population identification %s", uuid));

        Circuit betterCircuit = population.get(0);

        for (int i = 0; i < Math.min(DUMP_LIMIT_MIN, population.size()); i++) {
            logger.info(String.format("[%5d] %s", i + 1, CircuitToString.toSmallString(suiteWrapper, population.get(i))));
            // logger.info(String.format("[%5d] %s", i + 1, CircuitToString.toString(evaluator, population.get(i))));
        }

        if (population.size() > 3) {
            int limit = Math.min(POPULATION_MAX_SIZE, population.size());
            for (int i = limit - 3; i < limit; i++) {
                logger.info(String.format("[%5d] %s %.3f", i + 1, CircuitToString.toSmallString(suiteWrapper, population.get(i)), SuiteWrapperUtil.similarity(suiteWrapper, betterCircuit, population.get(i))));
            }
        }

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int populationSize = population.size();
        int totalHits = CircuitUtils.getTotalOfPossibleHits(suiteWrapper);
        int totalOfPorts = sumTotalOfPort(population);
        String quantityOfPorts = myFormatter.format(totalOfPorts);

        double workload = 1000d * ((double)populationSize / (double)totalOfPorts);

        logger.info(String.format("Population [%d] Total Hits [%d]", populationSize, totalHits));
        logger.info(String.format("Total of Ports [%s] Workload [%.2f]", quantityOfPorts, workload));

    }

    private static int sumTotalOfPort(List<Circuit> population) {
        int total = 0;

        for (Circuit circuit : population) {
            total += circuit.size();
        }
        return total;
    }

    public synchronized int orderedAdd(SuiteWrapper suiteWrapper, Circuit newCircuit) {

        if (newCircuit == null) {
            throw new RuntimeException("Cannot add null circuit!");
        }

        CircuitComparator comparator = suiteWrapper.getSuite().getCircuitComparator();
        if (comparator == null) {
            throw new RuntimeException("Comparator cannot be null!");
        }

        if (population == null) {
            throw new RuntimeException("Population cannot be null!");
        }

        if ((population.size() >= POPULATION_MAX_SIZE) && (comparator.compare(newCircuit, population.get(population.size() - 1)) >= 0)) {
            return -1;
        }

        int pos = Collections.binarySearch(population, newCircuit, comparator);
        if (pos < 0) {
            pos = ~pos;
            if (pos <= POPULATION_MAX_SIZE) {
                population.add(pos, newCircuit);
            }
            //else {
            //    logger.warn("Circuit discarted, population limit reached");
            //}
        } else {
            pos = -1;
        }

        while (population.size() > POPULATION_MAX_SIZE) {
            population.remove(population.size() - 1);
        }

        return pos;
    }

    public synchronized Circuit getFirst() {
        Circuit circuit = null;
        if (population.size() > 0) {
            circuit = population.get(0);
        }
        return circuit;
    }

    public synchronized Circuit getWeightedRandom() {
        Circuit circuit = null;
        if (population.size() > 0) {
            circuit = population.get(RandomUtils.raffle(population.size()));
        }
        return circuit;
    }


}
