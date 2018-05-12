package org.genetics.camel.processor.generator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.genetics.camel.configuration.Constants;
import org.genetics.circuit.random.RandomWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class GeneratorMethodDefinitionProcessor implements Processor, InitializingBean {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;

    private static final Logger logger = LoggerFactory.getLogger(GeneratorMethodDefinitionProcessor.class);

    private RandomWeight<String> randomWeight = null;

    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Constants.HEADER_GENERATOR_METHOD, this.randomWeight.next());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        randomWeight = new RandomWeight<String>();

        randomWeight.addByWeight(   1, Constants.GENERATOR_METHOD_RANDOM);
        randomWeight.addByWeight(  50, Constants.GENERATOR_METHOD_ENRICH_EXISTING);
        randomWeight.addByWeight(1000, Constants.GENERATOR_METHOD_MIX_EXISTING);

        randomWeight.addByPeriod(MINUTE, Constants.GENERATOR_METHOD_RANDOM);
        randomWeight.addByPeriod(10 * MINUTE, Constants.GENERATOR_METHOD_MIGRATE_RANDOM_FROM_DATABASE);
    }
}
