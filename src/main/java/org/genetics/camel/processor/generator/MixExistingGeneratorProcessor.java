package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitRandomGenerator;
import org.genetics.circuit.circuit.CircuitScramble;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.genetics.circuit.service.SuiteWrapperService;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class MixExistingGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperService suiteWrapperService;

    @Autowired
    private PopulationService populationService;

    @Override
    public void process(Exchange exchange) throws Exception {
        SuiteWrapper suiteWrapper = suiteWrapperService.getLatest(exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class));

        Circuit c1 = populationService.getWeightedRandom();
        Circuit c2 = populationService.getWeightedRandom();

        if (c1 == null || c2 == null) {
            exchange.getIn().setBody(null);
        }
        else {
            exchange.getIn().setBody(CircuitScramble.scramble(suiteWrapper.getSuite().getTrainingSet(), (Circuit) c1.clone(), (Circuit) c2.clone()));
        }

    }
}
