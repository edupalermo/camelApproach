package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.circuit.CircuitScramble;
import org.genetics.circuit.entity.SuiteWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MixExistingGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperMediator suiteWrapperMediator;

    @Autowired
    private MemoryPopulationMediator memoryPopulationMediator;

    @Override
    public void process(Exchange exchange) throws Exception {
        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);
        SuiteWrapper suiteWrapper = suiteWrapperMediator.getSuiteWrapper(problemName);

        CircuitContextDecorator c1 = memoryPopulationMediator.getWeightedRandom();
        CircuitContextDecorator c2 = memoryPopulationMediator.getWeightedRandom();

        if (c1 == null || c2 == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
        else {
            exchange.getIn().setBody(CircuitScramble.mix(c1.clone(), c2.clone()));
        }

    }
}
