package org.genetics.camel;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.genetics.circuit", "org.genetics.camel"})
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        CamelContext camelContext = applicationContext.getBean("camelContext", CamelContext.class);
    }

}
