package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitRandomGenerator;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.SuiteWrapperService;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomGeneratorProcessor implements Processor {

    @Autowired
    private SuiteWrapperService suiteWrapperService;

    @Override
    public void process(Exchange exchange) throws Exception {
        SuiteWrapper suiteWrapper = suiteWrapperService.getLatest(exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class));

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Circuit circuit = CircuitRandomGenerator.randomGenerate(SuiteWrapperUtil.getInputSize(suiteWrapper), random.nextInt(300, 1000), SuiteWrapperUtil.useMemory(suiteWrapper));
        exchange.getIn().setBody(circuit);
    }
}
