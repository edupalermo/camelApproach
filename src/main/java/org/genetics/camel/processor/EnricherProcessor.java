package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
public class EnricherProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(EnricherProcessor.class);

    @Autowired
    private MemoryPopulationMediator memoryPopulationMediator;

    @Autowired
    private SuiteWrapperMediator suiteWrapperController;

    public void process(Exchange exchange) throws Exception {
        CircuitImpl circuitImpl = exchange.getIn().getBody(CircuitImpl.class);

        CircuitContextDecorator oldBetter = memoryPopulationMediator.getFirst();

        CircuitImpl[] array = null;

        if (oldBetter != null) {
            circuitImpl = CircuitScramble.join(circuitImpl, (CircuitImpl) oldBetter.getRootCircuit().clone());
        }

        exchange.getIn().setBody(circuitImpl);
    }
}
