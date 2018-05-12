package org.genetics.camel.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopFilterPredicate implements Predicate {

    private static Logger logger = LoggerFactory.getLogger(TopFilterPredicate.class);

    @Autowired
    private PopulationService populationService;

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    public boolean matches(Exchange exchange) {
        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        return populationService.hasTopPosition(suiteWrapper, exchange.getIn().getBody(Circuit.class));
    }
}
