package org.genetics.camel.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GenerationPredicate implements Predicate {

    private static Logger logger = LoggerFactory.getLogger(GenerationPredicate.class);

    public boolean matches(Exchange exchange) {
        logger.info("Filter!");
        return true;
    }
}
