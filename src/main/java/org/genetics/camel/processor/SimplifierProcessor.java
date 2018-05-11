package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitScramble;
import org.genetics.circuit.circuit.CircuitToString;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.CircuitComparator;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.service.SuiteWrapperService;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.IoUtils;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimplifierProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SimplifierProcessor.class);

    @Autowired
    private SuiteWrapperService suiteWrapperService;

    public void process(Exchange exchange) throws Exception {
        Circuit newBetter = exchange.getIn().getBody(Circuit.class);
        SuiteWrapper suiteWrapper = suiteWrapperService.getLatest(exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class));
        TrainingSet trainingSet = suiteWrapper.getSuite().getTrainingSet();
        Circuit oldBetter = (Circuit) exchange.getIn().getHeader(Constants.HEADER_OLD_BETTER, Circuit.class);


        if (oldBetter != null) {
            Circuit c1 = join(trainingSet, (Circuit) newBetter.clone(), (Circuit) oldBetter.clone());
            simplify(trainingSet, c1);
            SuiteWrapperUtil.evaluate(suiteWrapper, c1);
            Circuit c2 = join(trainingSet, (Circuit) oldBetter.clone(), (Circuit) newBetter.clone());
            simplify(trainingSet, c2);
            SuiteWrapperUtil.evaluate(suiteWrapper, c2);

            logger.info(String.format("OldBetter: %s", CircuitToString.toSmallString(suiteWrapper, oldBetter)));
            logger.info(String.format("NewBetter: %s", CircuitToString.toSmallString(suiteWrapper, newBetter)));
            logger.info(String.format("n - o    : %s", CircuitToString.toSmallString(suiteWrapper, c1)));
            logger.info(String.format("o - n    : %s", CircuitToString.toSmallString(suiteWrapper, c2)));


            CircuitComparator comparator = suiteWrapper.getSuite().getCircuitComparator();
            if ((comparator.compare(oldBetter, c1) < 0) || (comparator.compare(oldBetter, c2) < 0) ||
                    (comparator.compare(newBetter, c1) < 0) || (comparator.compare(newBetter, c2) < 0)) {
                logger.info("Inconsistency!");
                logger.info("OldBetter: " + IoUtils.objectToBase64(oldBetter));
                logger.info("NewBetter: " + IoUtils.objectToBase64(newBetter));

            }


            exchange.getOut().setBody(getBetter(suiteWrapper.getSuite().getCircuitComparator(), oldBetter, newBetter, c1, c2).clone());
        }
        else {
            Circuit c1 = (Circuit) newBetter.clone();
            simplify(trainingSet, c1);
            SuiteWrapperUtil.evaluate(suiteWrapper, c1);

            logger.info(String.format("NewBetter: %s", CircuitToString.toSmallString(suiteWrapper, newBetter)));
            logger.info(String.format("n - o    : %s", CircuitToString.toSmallString(suiteWrapper, c1)));

            exchange.getOut().setBody(getBetter(suiteWrapper.getSuite().getCircuitComparator(), newBetter, c1).clone());
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

        logger.info(String.format("Better index [%d]", index));

        return c[index];

    }
}
