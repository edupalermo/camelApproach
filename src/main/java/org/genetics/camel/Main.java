package org.genetics.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.genetics.camel.processor.EvaluatorProcessor;
import org.genetics.camel.processor.GeneratorProcessor;
import org.genetics.camel.processor.SimplifierProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) {
        logger.info("START");

        SimpleRegistry registry = new SimpleRegistry();
        registry.put(GeneratorProcessor.LOOKUP_NAME, new GeneratorProcessor());
        registry.put(SimplifierProcessor.LOOKUP_NAME, new SimplifierProcessor());
        registry.put(EvaluatorProcessor.LOOKUP_NAME, new EvaluatorProcessor());

        CamelContext camelContext = null;
        try {
            camelContext = new DefaultCamelContext(registry);
            camelContext.addRoutes(new GenericRoute());
            logger.info("Camel Context is going to be started...");
            camelContext.start();

            ProducerTemplate template = camelContext.createProducerTemplate();
            template.sendBody("seda:loop", "tick");

            for (int i = 0; i < 100000; i++) {
                SedaEndpoint generator = (SedaEndpoint) camelContext.getEndpoint(GenericRoute.sedaGenerator());
                SedaEndpoint simplifier = (SedaEndpoint) camelContext.getEndpoint(GenericRoute.sedaSimplifier());
                SedaEndpoint evaluator = (SedaEndpoint) camelContext.getEndpoint(GenericRoute.sedaEvaluator());
                SedaEndpoint persist = (SedaEndpoint) camelContext.getEndpoint(GenericRoute.sedaPersist());
                logger.info(String.format("Queue %d %d %d %d", generator.getCurrentQueueSize(), simplifier.getCurrentQueueSize(), evaluator.getCurrentQueueSize(), persist.getCurrentQueueSize()));
                Thread.sleep(1000);
            }


            Thread.sleep(10000000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            stopItQuitely(camelContext);
        }

    }

    public static final void stopItQuitely(CamelContext camelContext) {
        if (camelContext != null) {
            try {
                camelContext.stop();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }

    }

}
