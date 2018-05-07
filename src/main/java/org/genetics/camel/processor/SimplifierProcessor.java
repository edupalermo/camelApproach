package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimplifierProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SimplifierProcessor.class);

    public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody(new Circuit("Circuit Simplified"));
    }
}
