package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.CircuitService;
import org.genetics.circuit.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrateRandomFromDatabaseGeneratorProcessor implements Processor {

    private final Logger logger = LoggerFactory.getLogger(MigrateRandomFromDatabaseGeneratorProcessor.class);

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


        CircuitImpl newCircuit = null;

        int total = circuitService.size(suiteWrapper);
        int position = 0;
        if (total > 0) {
            position = RandomUtils.raffle(total);
            newCircuit = (CircuitImpl) circuitService.findByPosition(suiteWrapper, position);

            if (newCircuit != null) {
                exchange.getIn().setBody(newCircuit);
            }
        }

        if (newCircuit == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
        else {
            logger.info(String.format("Bringing circuit %d from database [%d]", position, newCircuit.size()));
        }
    }
}
