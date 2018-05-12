package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitScramble;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MixExistingGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    @Autowired
    private PopulationService populationService;

    @Override
    public void process(Exchange exchange) throws Exception {
        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        Circuit c1 = populationService.getWeightedRandom();
        Circuit c2 = populationService.getWeightedRandom();

        if (c1 == null || c2 == null) {
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
        else {
            exchange.getIn().setBody(CircuitScramble.mix(suiteWrapper.getSuite().getTrainingSet(), (Circuit) c1.clone(), (Circuit) c2.clone()));
        }

    }
}
