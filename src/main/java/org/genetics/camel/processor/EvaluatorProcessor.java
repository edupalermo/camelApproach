package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.circuit.Circuit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluatorProcessor implements Processor {

    public static final String LOOKUP_NAME = "EvaluatorProcessor";

    private static final Logger logger = LoggerFactory.getLogger(EvaluatorProcessor.class);

    public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody(new Circuit("Circuit Evaluated"));
    }
}
