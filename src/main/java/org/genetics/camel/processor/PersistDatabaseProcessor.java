package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
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
    private SuiteWrapperController suiteWrapperController;

    public void process(Exchange exchange) throws Exception {
        Circuit circuit = exchange.getIn().getBody(Circuit.class);

        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        circuitService.orderedPersist(suiteWrapper, circuit);
    }
}
