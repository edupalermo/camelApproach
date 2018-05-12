package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.CircuitService;
import org.genetics.circuit.service.PopulationService;
import org.genetics.circuit.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrateRandomFromDatabaseGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    @Autowired
    private PopulationService populationService;

    @Autowired
    private CircuitService circuitService;

    @Override
    public void process(Exchange exchange) throws Exception {
        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        int total = circuitService.size(suiteWrapper);

        Circuit newCircuit = circuitService.findByPosition(suiteWrapper, RandomUtils.raffle(total));

        if (newCircuit == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        } else {
            exchange.getIn().setBody(newCircuit);
        }
    }
}
