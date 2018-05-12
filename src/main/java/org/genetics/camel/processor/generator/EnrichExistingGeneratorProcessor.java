package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitRandomGenerator;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnrichExistingGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    @Autowired
    private PopulationService populationService;

    private static final double ENRICH_PERCENTAGE = 10d;

    @Override
    public void process(Exchange exchange) throws Exception {
        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        Circuit c1 = populationService.getWeightedRandom();

        if (c1 == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
        else {
            Circuit newCircuit = (Circuit) c1.clone();
            CircuitRandomGenerator.randomEnrich(newCircuit, (int)(1 + ((ENRICH_PERCENTAGE * newCircuit.size()) / 100)), SuiteWrapperUtil.useMemory(suiteWrapper));
            exchange.getIn().setBody(newCircuit);
        }
    }
}
