package org.genetics.camel.aggregationstrategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
public class KeepBestAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        if (Integer.valueOf(oldExchange.getIn().getBody(String.class)).intValue() < Integer.valueOf(newExchange.getIn().getBody(String.class)).intValue()) {
            return oldExchange;
        }
        else {
            return newExchange;
        }

    }

}
