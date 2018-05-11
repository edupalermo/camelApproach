package org.genetics.camel;

import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.genetics.camel.aggregationstrategy.KeepBestAggregationStrategy;
import org.genetics.camel.configuration.Constants;
import org.genetics.camel.predicate.GenerationPredicate;
import org.genetics.camel.processor.EvaluatorProcessor;
import org.genetics.camel.processor.GeneratorProcessor;
import org.genetics.camel.processor.SimplifierProcessor;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.apache.camel.builder.PredicateBuilder.not;

@Component
public class GenericRoute extends RouteBuilder {

    private static final String CONCURRENT_CONSUMERS = "5";
    private static final String QUEUE_SIZE = "10";

    private static final String PROBLEM_NAME = "CHAR_TYPE";

    @Override
    public void configure() throws Exception {

        from(sedaGenerator())
            //.delay(simple("${random(100, 500)}"))
            .process("generatorMethodDefinitionProcessor")
            .choice()
                .when(header(Constants.HEADER_GENERATOR_METHOD).isEqualTo(Constants.GENERATOR_METHOD_RANDOM))
                    .process("randomGeneratorProcessor")
                    .endChoice()
                .when(header(Constants.HEADER_GENERATOR_METHOD).isEqualTo(Constants.GENERATOR_METHOD_MIX_EXISTING))
                    .process("mixExistingGeneratorProcessor")
                    .endChoice()
                .otherwise()
                    .log("ERROR Unknow method type! ${headers.GENERATOR_METHOD}")
                    .stop()
            .end()
                .filter(body().isNotNull())
            //.log("Step 1 - Generated... ${body} ${headers.GENERATOR_METHOD}")
            .to(sedaEvaluator());

        from(sedaEvaluator())
            //.delay(simple("${random(200, 600)}"))
            .filter().ref("generationPredicate")
            .process("evaluatorProcessor")
            //.setHeader("grade", simple("${body}"))
            //.log("Step 3 - Evaluating... ${body}")
            .to(sedaUpdateMemoryPopulation());

        from(sedaUpdateMemoryPopulation())
            .filter().ref("generationPredicate")
            .process("populationAddProcessor")
            .choice()
                .when(header(Constants.HEADER_POSITION).isEqualTo(Integer.valueOf(0))) // First should go to simplification
                    .to(sedaSimplifier())
                    .endChoice()
                .when(header(Constants.HEADER_SIMPLIFIED).isEqualTo("1")) // If the circuit was simplified then send to DB
                    .to(sedaUpdatePersistentPopulation())
                    .endChoice()
            .end();

        from(sedaSimplifier())
            //.delay(simple("${random(100, 500)}"))
            .filter().ref("generationPredicate")
            .process("simplifierProcessor")
            .setHeader(Constants.HEADER_SIMPLIFIED, constant("1"))
            //.transform(simple("${body.substring(8)}"))
            //.log("Step 2 - Simplifying... ${body}")
            .to(sedaEvaluator());

        from(sedaUpdatePersistentPopulation())
            .log("TO DATABASE!");


        // Heat beat of circuit generation
        from("timer://timer1?delay=-1&fixedRate=false")
            .split(simple("TICK"))
                .setHeader(Constants.HEADER_PROBLEM_NAME, constant(PROBLEM_NAME))
                .to(sedaGenerator())
            .end();


        // Dump Population status
        from("timer://timer2?fixedRate=true&period=15000")
            .setHeader(Constants.HEADER_PROBLEM_NAME, constant(PROBLEM_NAME))
            .process("populationDumpProcessor");

        /*
        from("timer://timerTest?delay=150&fixedRate=false&repeatCount=20")
                .setBody(simple("${random(1, 100)}"))
                .log("g - ${body}")
                .to(sedaTest());

        from(sedaTest())
                .aggregate(constant("true"), new KeepBestAggregationStrategy())
                .log("pp - ${body}")
                .delay(1000);
         */

    }

    public static String sedaGenerator() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:generator?");
        //sb.append("waitForTaskToComplete=Always&");
        // sb.append("queue=generatorQueue&");
        sb.append("size=").append(QUEUE_SIZE).append("&");
        sb.append("multipleConsumers=true&");
        sb.append("concurrentConsumers=").append(CONCURRENT_CONSUMERS).append("&");
        sb.append("limitConcurrentConsumers=").append(CONCURRENT_CONSUMERS).append("&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }

    public static String sedaSimplifier() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:simplifier?");
        //sb.append("waitForTaskToComplete=Always&");
        // sb.append("queue=generatorQueue&");
        sb.append("size=").append(QUEUE_SIZE).append("&");
        sb.append("multipleConsumers=true&");
        sb.append("concurrentConsumers=").append(CONCURRENT_CONSUMERS).append("&");
        sb.append("limitConcurrentConsumers=").append(CONCURRENT_CONSUMERS).append("&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }

    public static String sedaEvaluator() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:evaluator?");
        //sb.append("waitForTaskToComplete=Always&");
        // sb.append("queue=generatorQueue&");
        sb.append("size=").append(QUEUE_SIZE).append("&");
        sb.append("multipleConsumers=true&");
        sb.append("concurrentConsumers=").append(CONCURRENT_CONSUMERS).append("&");
        sb.append("limitConcurrentConsumers=").append(CONCURRENT_CONSUMERS).append("&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }

    public static String sedaUpdateMemoryPopulation() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:updateMemoryPopulation?");
        sb.append("size=").append(QUEUE_SIZE).append("&");
        sb.append("multipleConsumers=false&");
        sb.append("concurrentConsumers=1&");
        sb.append("limitConcurrentConsumers=1&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }

    public static String sedaPersist() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:persist?");
        //sb.append("waitForTaskToComplete=Always&");
        // sb.append("queue=generatorQueue&");
        sb.append("size=").append(QUEUE_SIZE).append("&");
         sb.append("multipleConsumers=false&");
        sb.append("concurrentConsumers=1&");
        sb.append("limitConcurrentConsumers=1&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }

    public static String sedaUpdatePersistentPopulation() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:updatePersistentPopulation?");
        sb.append("size=").append(QUEUE_SIZE).append("&");
        sb.append("multipleConsumers=false&");
        sb.append("concurrentConsumers=1&");
        sb.append("limitConcurrentConsumers=1&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }

    public static String sedaTest() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:Test?");
        sb.append("multipleConsumers=false&");
        sb.append("concurrentConsumers=1&");
        sb.append("limitConcurrentConsumers=1&");
        sb.append("blockWhenFull=true&");
        sb.append("purgeWhenStopping=true");

        return sb.toString();
    }


}
