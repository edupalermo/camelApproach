package org.genetics.camel.processor.population;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.circuit.CircuitScramble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PopulationAddProcessor implements Processor {

    @Autowired private MemoryPopulationMediator memoryPopulationMediator;

    @Autowired private SuiteWrapperMediator suiteWrapperMediator;

    public synchronized void process(Exchange exchange) throws Exception {
        CircuitContextDecorator circuitContextDecorator = exchange.getIn().getBody(CircuitContextDecorator.class);
        String initialGenerationId = exchange.getIn().getHeader(Constants.HEADER_POPULATION_GENERATION, String.class);

        int position = memoryPopulationMediator.orderedAdd(initialGenerationId, circuitContextDecorator);

        if (position == 0) {
            CircuitImpl newCircuitImpl = CircuitScramble.join(memoryPopulationMediator.getFirst().clone(), circuitContextDecorator.getRootCircuit().clone());
            CircuitContextDecorator simplified = new CircuitContextDecorator(circuitContextDecorator.getSuiteWrapper(), newCircuitImpl);
            simplified = simplified.simplifyAndEvaluate();
            memoryPopulationMediator.orderedAdd(initialGenerationId, simplified);
            exchange.getIn().setBody(simplified);
        }

        exchange.getIn().setHeader(Constants.HEADER_POSITION, new Integer(position));
    }

}
