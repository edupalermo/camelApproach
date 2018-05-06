package org.genetics.camel;

import org.apache.camel.builder.RouteBuilder;
import org.genetics.camel.processor.EvaluatorProcessor;
import org.genetics.camel.processor.GeneratorProcessor;
import org.genetics.camel.processor.SimplifierProcessor;

import java.util.Random;

public class GenericRoute extends RouteBuilder {

    private static final String CONCURRENT_CONSUMERS = "5";
    private static final String QUEUE_SIZE = "10";


    @Override
    public void configure() throws Exception {
        Random random = new Random();

        // This loop is the heart beat of the system
        from("seda:loop")
                // .log("Main loop ${body}")
                .split(simple("TICK"))
                    .to(sedaGenerator())
                .end()
                .to("seda:loop");

        from(sedaGenerator())
                //.delay(simple("${random(100, 500)}"))
                .process(GeneratorProcessor.LOOKUP_NAME)
                //.transform(simple("Complex ${random(0,100000)}"))
                .log("Step 1 - Generated... ${body}")
                .to(sedaSimplifier());

        from(sedaSimplifier())
                //.delay(simple("${random(100, 500)}"))
                .process(SimplifierProcessor.LOOKUP_NAME)
                //.transform(simple("${body.substring(8)}"))
                .log("Step 2 - Simplifying... ${body}")
                .to(sedaEvaluator());

        from(sedaEvaluator())
                //.delay(simple("${random(200, 600)}"))
                .process(EvaluatorProcessor.LOOKUP_NAME)
                //.setHeader("grade", simple("${body}"))
                .log("Step 3 - Evaluating... ${body}")
                .to(sedaPersist());

        from(sedaPersist())
                //.resequence(header("grade")).stream()
                .delay(simple("${random(1000, 2000)}"))
                .log("*********************************")
                .log("Step 4 - Persisted... ${body}")
                .log("*********************************");

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

    public static String sedaPersist() {
        StringBuffer sb = new StringBuffer();

        sb.append("seda:persist?");
        //sb.append("waitForTaskToComplete=Always&");
        // sb.append("queue=generatorQueue&");
        sb.append("size=").append(QUEUE_SIZE).append("&");
         sb.append("multipleConsumers=true&");
        sb.append("concurrentConsumers=1&");
        sb.append("limitConcurrentConsumers=1&");
        sb.append("blockWhenFull=true");

        return sb.toString();
    }


}
