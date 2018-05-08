package org.genetics.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultShutdownStrategy;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.ShutdownStrategy;
import org.genetics.camel.predicate.GenerationPredicate;
import org.genetics.camel.processor.EvaluatorProcessor;
import org.genetics.camel.processor.GeneratorProcessor;
import org.genetics.camel.processor.SimplifierProcessor;
import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.dao.SuiteWrapperDao;
import org.genetics.circuit.dao.jdbc.JdbcProblemDao;
import org.genetics.circuit.dao.jdbc.JdbcSuiteWrapperDao;
import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.problem.vowel.VowelSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.genetics.circuit", "org.genetics.camel"})
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        CamelContext camelContext = applicationContext.getBean("camelContext", CamelContext.class);


        //SuiteWrapperDao suiteWrapperDao = applicationContext.getBean(SuiteWrapperDao.class);
        //ProblemDao problemDao = applicationContext.getBean(ProblemDao.class);
        //Problem vowelProblem = problemDao.findByName("CHAR_TYPE");
        //suiteWrapperDao.create(vowelProblem, new VowelSuite());
    }

    /*

    @Bean
    public ShutdownStrategy shutdownStrategy() {
        DefaultShutdownStrategy strategy = new DefaultShutdownStrategy();
        strategy.setTimeout(30);
        strategy.setSuppressLoggingOnTimeout(true);
        return strategy;
    }
    */

    public static void main2(String args[]) {
        logger.info("START");

        SimpleRegistry registry = new SimpleRegistry();
        /*
        registry.put(GeneratorProcessor.LOOKUP_NAME, new GeneratorProcessor());
        registry.put(SimplifierProcessor.LOOKUP_NAME, new SimplifierProcessor());
        registry.put(EvaluatorProcessor.LOOKUP_NAME, new EvaluatorProcessor());

        registry.put(GenerationPredicate.LOOKUP_NAME, new GenerationPredicate());
        */

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
