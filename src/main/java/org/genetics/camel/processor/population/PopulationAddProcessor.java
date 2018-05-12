package org.genetics.camel.processor.population;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PopulationAddProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PopulationAddProcessor.class);

    @Autowired private PopulationService populationService;

    @Autowired private SuiteWrapperController suiteWrapperController;

    public synchronized void process(Exchange exchange) throws Exception {
        Circuit circuit = exchange.getIn().getBody(Circuit.class);
        String initialGenerationId = exchange.getIn().getHeader(Constants.HEADER_POPULATION_GENERATION, String.class);

        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        int position = populationService.orderedAdd(suiteWrapper, initialGenerationId, circuit);

        exchange.getIn().setHeader(Constants.HEADER_POSITION, new Integer(position));
    }

}
