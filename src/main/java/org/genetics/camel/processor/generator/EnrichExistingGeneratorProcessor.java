package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.circuit.CircuitRandomGenerator;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnrichExistingGeneratorProcessor implements Processor {

    @Autowired
    private MemoryPopulationMediator memoryPopulationMediator;

    @Autowired
    private SuiteWrapperMediator suiteWrapperMediator;

    private static final double ENRICH_PERCENTAGE = 10d;

    @Override
    public void process(Exchange exchange) throws Exception {
        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);
        SuiteWrapper suiteWrapper = suiteWrapperMediator.getSuiteWrapper(problemName);
        CircuitContextDecorator c1 = memoryPopulationMediator.getWeightedRandom();

        if (c1 == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
        else {
            Random random = ThreadLocalRandom.current();

            CircuitImpl newCircuit = c1.clone();
            CircuitRandomGenerator.randomEnrich(newCircuit, 1 + random.nextInt(newCircuit.size()), SuiteWrapperUtil.useMemory(suiteWrapper));
            exchange.getIn().setBody(newCircuit);
        }
    }
}
