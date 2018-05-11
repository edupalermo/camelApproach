package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitToString;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.SuiteWrapperService;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(EvaluatorProcessor.class);

    @Autowired
    private SuiteWrapperService suiteWrapperService;

    public void process(Exchange exchange) throws Exception {
        Circuit circuit = exchange.getIn().getBody(Circuit.class);
        SuiteWrapper suiteWrapper = suiteWrapperService.getLatest(exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class));
        SuiteWrapperUtil.evaluate(suiteWrapper, circuit);
        // logger.info(String.format("%s", CircuitToString.toSmallString(suiteWrapper, circuit)));
    }
}
