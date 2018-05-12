package org.genetics.camel.processor.population;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.seda.SedaEndpoint;
import org.genetics.camel.GenericRoute;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PopulationDumpProcessor  implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PopulationDumpProcessor.class);

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    @Autowired private PopulationService populationService;

    @Override
    public void process(Exchange exchange) throws Exception {

        String problemName = exchange.getIn().getHeader(Constants.HEADER_PROBLEM_NAME, String.class);

        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        logger.info("=====================================================");

        populationService.dump(suiteWrapper);

        logger.info(String.format("To generate   [%3d] Memory push   [%3d]", camelContext.getEndpoint(GenericRoute.sedaGenerator(), SedaEndpoint.class).getCurrentQueueSize(), camelContext.getEndpoint(GenericRoute.sedaUpdateMemoryPopulation(), SedaEndpoint.class).getCurrentQueueSize()));
        logger.info(String.format("To evaluate   [%3d] Database push [%3d]", camelContext.getEndpoint(GenericRoute.sedaEvaluator(), SedaEndpoint.class).getCurrentQueueSize(), camelContext.getEndpoint(GenericRoute.sedaUpdatePersistentPopulation(), SedaEndpoint.class).getCurrentQueueSize()));
        logger.info(String.format("To simplifier [%3d]", camelContext.getEndpoint(GenericRoute.sedaSimplifier(), SedaEndpoint.class).getCurrentQueueSize()));

    }
}
