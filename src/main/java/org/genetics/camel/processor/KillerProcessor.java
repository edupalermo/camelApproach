package org.genetics.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.mediator.MemoryPopulationMediator;
import org.genetics.camel.mediator.SuiteWrapperMediator;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KillerProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(KillerProcessor.class);

    @Autowired
    private SuiteWrapperMediator suiteWrapperController;

    @Autowired
    private MemoryPopulationMediator memoryPopulationMediator;

    private CircuitContextDecorator lastWorst;

    @Override
    public void process(Exchange exchange) throws Exception {

        CircuitContextDecorator newWorst = memoryPopulationMediator.getWorst();

        if (this.lastWorst == null) {
            logger.info("## Populating the worst individual of the population to further evaluation.");
            this.lastWorst = newWorst;
        }
        else {
            if (newWorst == null) {
                logger.info("## New worst individual is null.");
            }
            else {
                int compare = 0;
                if ((compare = this.lastWorst.compareTo(newWorst)) < 0) {
                    logger.info("## Probable generation changed. Setting new last Worst.");
                    this.lastWorst = newWorst;
                }
                else if (compare > 0) {
                    logger.info("## New Worst is better than last worst. We are evolving. Setting new last Worst.");
                    this.lastWorst = newWorst;
                }
                else if (memoryPopulationMediator.isFull()) {
                    logger.info("## KILLING ALL POPULATION! ##");
                    memoryPopulationMediator.reset();
                }
                else {
                    logger.info("## Population is not full kill was aborted!");
                }

            }

        }

    }

    /*

    private static final Logger logger = LoggerFactory.getLogger(KillerProcessor.class);

    @Autowired
    private SuiteWrapperMediator suiteWrapperController;

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
    */
}
