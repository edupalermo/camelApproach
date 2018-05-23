package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.utils.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SimplifierProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SimplifierProcessor.class);

    @Autowired
    private SuiteWrapperMediator suiteWrapperMediator;

    @Autowired
    private MemoryPopulationMediator memoryPopulationMediator;

    public void process(Exchange exchange) throws Exception {

        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);
        SuiteWrapper suiteWrapper = suiteWrapperMediator.getSuiteWrapper(problemName);

        String uuid = UUID.randomUUID().toString();
        // logger.info(String.format("[%s] Simplification start", uuid));

        CircuitImpl circuitImpl = exchange.getIn().getBody(CircuitImpl.class);

        // logger.info(String.format("[%s] Original Size %d", uuid, circuitImpl.size()));

        CircuitContextDecorator circuitContextDecorator = new CircuitContextDecorator(suiteWrapper, circuitImpl);

        int limit = 2 * memoryPopulationMediator.getMediumSize();
        if (circuitImpl.size() > limit) {
            //logger.info(String.format("Simplifying  %4d - %4d", circuitImpl.size(), limit));
            circuitContextDecorator  = circuitContextDecorator.simplifyAndEvaluate();
            //logger.info(String.format("[%s] After Simplification %d", uuid, circuitContextDecorator.getRootCircuit().size()));
        }
        else {
            //logger.info(String.format("Skiping simp %4d - %4d", circuitImpl.size(), limit));
        }
        //circuitContextDecorator.evaluate();

        //logger.info(String.format("[%s] Evaluation finished", uuid));

        exchange.getIn().setBody(circuitContextDecorator);
    }

}
