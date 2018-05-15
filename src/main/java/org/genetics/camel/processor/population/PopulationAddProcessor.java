package org.genetics.camel.processor.population;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.entity.SuiteWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PopulationAddProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PopulationAddProcessor.class);

    @Autowired private MemoryPopulationMediator memoryPopulationMediator;

    @Autowired private SuiteWrapperMediator suiteWrapperMediator;

    public synchronized void process(Exchange exchange) throws Exception {
        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);
        CircuitContextDecorator circuitContextDecorator = exchange.getIn().getBody(CircuitContextDecorator.class);
        String initialGenerationId = exchange.getIn().getHeader(Constants.HEADER_POPULATION_GENERATION, String.class);

        SuiteWrapper suiteWrapper = suiteWrapperMediator.getSuiteWrapper(problemName);

        int position = memoryPopulationMediator.orderedAdd(initialGenerationId, circuitContextDecorator);

        exchange.getIn().setHeader(Constants.HEADER_POSITION, new Integer(position));
    }

}
