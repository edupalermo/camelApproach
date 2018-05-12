package org.genetics.camel;

import org.apache.camel.builder.RouteBuilder;
import org.genetics.camel.configuration.Constants;
import org.springframework.stereotype.Component;

@Component
public class GenericRoute extends RouteBuilder {

    private static final String CONCURRENT_CONSUMERS = "5";
    private static final String QUEUE_SIZE = "20";

    private static final String PROBLEM_NAME = "CHAR_TYPE";

    @Override
    public void configure() throws Exception {

        from(sedaGenerator())
            .setHeader(Constants.HEADER_POPULATION_GENERATION, method("populationService", "getGenerationId"))
            .process("generatorMethodDefinitionProcessor")
            .choice()
                .when(header(Constants.HEADER_GENERATOR_METHOD).isEqualTo(Constants.GENERATOR_METHOD_RANDOM))
                    .process("randomGeneratorProcessor")
                    .endChoice()
                .when(header(Constants.HEADER_GENERATOR_METHOD).isEqualTo(Constants.GENERATOR_METHOD_MIX_EXISTING))
                    .process("mixExistingGeneratorProcessor")
                    .endChoice()
                .when(header(Constants.HEADER_GENERATOR_METHOD).isEqualTo(Constants.GENERATOR_METHOD_ENRICH_EXISTING))
                    .process("enrichExistingGeneratorProcessor")
                    .endChoice()
                .when(header(Constants.HEADER_GENERATOR_METHOD).isEqualTo(Constants.GENERATOR_METHOD_MIGRATE_RANDOM_FROM_DATABASE))
                    .process("migrateRandomFromDatabaseGeneratorProcessor")
                    .endChoice()
                .otherwise()
                    .log("ERROR Unknow method type! ${headers.GENERATOR_METHOD}")
            .end()
            .filter(body().isNotNull())
            .to(sedaEvaluator());

        from(sedaEvaluator())
            .filter(header(Constants.HEADER_POPULATION_GENERATION).isEqualTo(method("populationService", "getGenerationId")))
            .process("evaluatorProcessor")
            .to(sedaSimplifier());

        from(sedaSimplifier())
                .filter(header(Constants.HEADER_POPULATION_GENERATION).isEqualTo(method("populationService", "getGenerationId")))
                .filter().ref("topFilterPredicate")
                .filter().ref("suiteWrapperPredicate")
                .process("simplifierProcessor")
                .to(sedaUpdateMemoryPopulation());

        from(sedaUpdateMemoryPopulation())
            .filter().ref("generationPredicate")
            .filter().ref("suiteWrapperPredicate")
            .process("populationAddProcessor")
            .choice()
                .when(header(Constants.HEADER_POSITION).isEqualTo(Integer.valueOf(0))) // First should go to simplification
                    .log("TOP TOP TOP!")
                    .to(sedaUpdatePersistentPopulation())
                    .endChoice()
            .end();

        from(sedaUpdatePersistentPopulation())
            .process("persistDatabaseProcessor");


        // Heart beat of circuit generation
        from("timer://timer1?delay=-1&fixedRate=false")
            .split(simple("TICK"))
                .to(sedaGenerator())
            .end();

        // Dump Population status
        from("timer://timer2?period=15s")
                .process("populationDumpProcessor");

        from("timer://timer3?delay=1m&period=10m")
                .process("killerProcessor");




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
        sb.append("purgeWhenStopping=true&");
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
        sb.append("purgeWhenStopping=true&");
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
        sb.append("purgeWhenStopping=true&");
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
        sb.append("purgeWhenStopping=true&");
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
        sb.append("purgeWhenStopping=true&");
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
        sb.append("purgeWhenStopping=true&");
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
        sb.append("purgeWhenStopping=true&");
        sb.append("limitConcurrentConsumers=1&");
        sb.append("blockWhenFull=true&");
        sb.append("purgeWhenStopping=true");

        return sb.toString();
    }


}
