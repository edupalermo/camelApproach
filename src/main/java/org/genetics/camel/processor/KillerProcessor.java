package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.service.SuiteWrapperController;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.PopulationService;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KillerProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(KillerProcessor.class);

    @Autowired
    private SuiteWrapperController suiteWrapperController;

    @Autowired private PopulationService populationService;

    private Circuit oldLast;
    private String oldGenerationId;
    private int oldSize;

    @Override
    public void process(Exchange exchange) throws Exception {

        SuiteWrapper suiteWrapper = suiteWrapperController.getSuiteWrapper();

        if ((oldLast == null)
                || !oldGenerationId.equals(populationService.getGenerationId())
                || oldSize != populationService.getSize()) {
            this.oldLast = populationService.getLast();
            this.oldGenerationId = populationService.getGenerationId();
            this.oldSize = populationService.getSize();

        }
        else {
            Circuit actualLast = populationService.getLast();

            if (SuiteWrapperUtil.compare(suiteWrapper, actualLast, oldLast) >= 0) {
                logger.info("Killing all population.");
                populationService.kill();
            }
            else {
                oldLast = actualLast;
            }
        }

    }
}