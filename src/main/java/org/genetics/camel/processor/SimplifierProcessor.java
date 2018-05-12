package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitScramble;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.CircuitComparator;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.service.PopulationService;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimplifierProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SimplifierProcessor.class);

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    @Autowired
    private PopulationService populationService;

    // exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);

    public void process(Exchange exchange) throws Exception {

        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();
        TrainingSet trainingSet = suiteWrapper.getSuite().getTrainingSet();
        CircuitComparator circuitComparator = suiteWrapper.getSuite().getCircuitComparator();

        Circuit newBetter = exchange.getIn().getBody(Circuit.class);
        Circuit oldBetter = populationService.getFirst();


        if (oldBetter != null) {
            Circuit c1 = join(trainingSet, (Circuit) newBetter.clone(), (Circuit) oldBetter.clone());
            simplify(trainingSet, c1);
            SuiteWrapperUtil.evaluate(suiteWrapper, c1);
            Circuit c2 = join(trainingSet, (Circuit) oldBetter.clone(), (Circuit) newBetter.clone());
            simplify(trainingSet, c2);
            SuiteWrapperUtil.evaluate(suiteWrapper, c2);

            Circuit newCircuit = getBetter(circuitComparator, c1, c2);
            exchange.getOut().setBody(newCircuit);
        }
        else {
            Circuit c1 = (Circuit) newBetter.clone();
            simplify(trainingSet, c1);
            SuiteWrapperUtil.evaluate(suiteWrapper, c1);

            exchange.getOut().setBody(c1);
        }

        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }

    private Circuit join(TrainingSet trainingSet, Circuit c1, Circuit c2) {
        return CircuitScramble.join(trainingSet, c1, c2);
    }

    private void simplify(TrainingSet trainingSet, Circuit circuit) {
        if (circuit.size() > 3000) { // This is done in better join, but some time it is better to do it first or we can run out of memory
            CircuitUtils.simplifyByRemovingUnsedPorts(trainingSet, circuit);
        }
        CircuitUtils.betterSimplify(trainingSet, circuit);
    }

    private Circuit getBetter(CircuitComparator comparator, Circuit ... c) {

        int index = 0;

        for (int i = 1; i < c.length; i++) {
            if (comparator.compare(c[i], c[index]) <= 0) {
                index = i;
            }
        }

        //logger.info(String.format("Better index [%d]", index));

        return c[index];

    }
}
