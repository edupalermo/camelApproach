package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimplifierProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SimplifierProcessor.class);

    @Autowired
    private SuiteWrapperMediator suiteWrapperMediator;

    public void process(Exchange exchange) throws Exception {

        CircuitImpl backup = null;

        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);
        SuiteWrapper suiteWrapper = suiteWrapperMediator.getSuiteWrapper(problemName);

        try {
            CircuitImpl circuitImpl = exchange.getIn().getBody(CircuitImpl.class);
            backup = circuitImpl.clone();

            if (circuitImpl.size() > 100000) {
                simplify(suiteWrapper.getSuite().getTrainingSet(), circuitImpl);
            }

            CircuitContextDecorator circuitContextDecorator = new CircuitContextDecorator(circuitImpl);
            circuitContextDecorator.evaluate(suiteWrapper);

            exchange.getIn().setBody(circuitContextDecorator);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info(String.format("Circuit: [%s]", IoUtils.objectToBase64(backup)));
            logger.info(String.format("Suite: [%s]", IoUtils.objectToBase64(suiteWrapper.getSuite())));
            logger.error(e.getMessage(), e);
            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
        }
    }


    private void simplify(TrainingSet trainingSet, CircuitImpl circuit) {
        //if (circuit.size() > 5000) { // This is done in better join, but some time it is better to do it first or we can run out of memory
            CircuitUtils.simplifyByRemovingUnsedPorts(trainingSet, circuit);
        //}
        CircuitUtils.betterSimplify(trainingSet, circuit);
    }
}
