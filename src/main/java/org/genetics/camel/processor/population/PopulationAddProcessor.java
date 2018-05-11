package org.genetics.camel.processor.population;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.genetics.circuit.service.SuiteWrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PopulationAddProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PopulationAddProcessor.class);

    @Autowired private PopulationService populationService;

    @Autowired private SuiteWrapperService suiteWrapperService;

    public synchronized void process(Exchange exchange) throws Exception {
        Circuit circuit = exchange.getIn().getBody(Circuit.class);
        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);

        SuiteWrapper suiteWrapper = suiteWrapperService.getLatest(problemName);

        exchange.getIn().setHeader(Constants.HEADER_OLD_BETTER, populationService.getFirst());

        int position = populationService.orderedAdd(suiteWrapper, circuit);

        exchange.getIn().setHeader(Constants.HEADER_POSITION, new Integer(position));
    }

}
