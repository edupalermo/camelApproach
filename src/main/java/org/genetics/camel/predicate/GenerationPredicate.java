package org.genetics.camel.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class GenerationPredicate implements Predicate {

    public boolean matches(Exchange exchange) {
        return false;
    }
}
