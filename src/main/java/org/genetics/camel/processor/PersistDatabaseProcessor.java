package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.service.CircuitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistDatabaseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PersistDatabaseProcessor.class);

    @Autowired
    private CircuitService circuitService;

    @Autowired
    private SuiteWrapperMediator suiteWrapperController;

    public void process(Exchange exchange) throws Exception {
        CircuitContextDecorator circuitContextDecorator = exchange.getIn().getBody(CircuitContextDecorator.class);
        circuitService.orderedPersist(circuitContextDecorator);
    }
}
