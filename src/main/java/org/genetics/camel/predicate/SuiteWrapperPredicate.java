package org.genetics.camel.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.service.SuiteWrapperController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuiteWrapperPredicate implements Predicate {

    private static Logger logger = LoggerFactory.getLogger(SuiteWrapperPredicate.class);

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    public boolean matches(Exchange exchange) {
        return exchange.getIn().getHeader(Constants.HEADER_SUITE_WRAPPER_ID, Integer.class).intValue() == suiteWrapperController.getSuiteWrapperId();
    }
}
