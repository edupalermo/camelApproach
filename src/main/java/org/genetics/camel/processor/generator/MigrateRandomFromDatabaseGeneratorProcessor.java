package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.CircuitService;
import org.genetics.circuit.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrateRandomFromDatabaseGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperMediator suiteWrapperMediator;

    @Autowired
    private MemoryPopulationMediator memoryPopulationMediator;

    @Autowired
    private CircuitService circuitService;

    @Override
    public void process(Exchange exchange) throws Exception {
        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);
        SuiteWrapper suiteWrapper = suiteWrapperMediator.getSuiteWrapper(problemName);


        Circuit newCircuit = null;

        int total = circuitService.size(suiteWrapper);

        if (total > 0) {
            newCircuit = circuitService.findByPosition(suiteWrapper, RandomUtils.raffle(total));

            if (newCircuit != null) {
                exchange.getIn().setBody(newCircuit);
            }
        }

        if (newCircuit == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
    }
}
