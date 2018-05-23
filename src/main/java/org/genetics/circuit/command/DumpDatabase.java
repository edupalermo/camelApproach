package org.genetics.circuit.command;

import org.genetics.camel.Application;
import org.genetics.camel.GenericRoute;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.circuit.CircuitOutputGenerator;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.CircuitService;
import org.genetics.circuit.service.SuiteWrapperService;
import org.genetics.circuit.solution.StringSolution;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Properties;

@SpringBootApplication
@ComponentScan(basePackages = {"org.genetics.circuit"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GenericRoute.class)})
public class DumpDatabase {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        SpringApplication springApplication = new SpringApplication(DumpDatabase.class);


        Properties properties = new Properties();
        properties.setProperty("camel.springboot.main-run-controller", "false");
        springApplication.setDefaultProperties(properties);

        ApplicationContext applicationContext = springApplication.run(args);

        CircuitService circuitService = applicationContext.getBean(CircuitService.class);
        SuiteWrapperService suiteWrapperService = applicationContext.getBean(SuiteWrapperService.class);

        SuiteWrapper suiteWrapper = suiteWrapperService.getLatest("CHAR_TYPE");

        for (int i = 0; i < Math.min(circuitService.size(suiteWrapper), 20); i++) {

            CircuitContextDecorator ccd = new CircuitContextDecorator(suiteWrapper, (CircuitImpl) circuitService.findByPosition(suiteWrapper, i));
            ccd.evaluate();

            logger.info(String.format("%d - %s", i, ccd.toString()));
        }

        //test(circuitService, suiteWrapper);

        int exitCode = SpringApplication.exit(applicationContext, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                // no errors
                return 0;
            }
        });

        System.exit(exitCode);


    }

    private static void test(CircuitService circuitService, SuiteWrapper suiteWrapper) {
        Circuit circuit = circuitService.findByPosition(suiteWrapper, 0);

        if (circuit != null) {

            int[] output = CircuitOutputGenerator.generateOutput(SuiteWrapperUtil.getTrainingSet(suiteWrapper), circuit);

            for (char c = 'a'; c <= 'z'; c++) {
                dump(c, circuit, output);
            }

            for (char c = 'A'; c <= 'Z'; c++) {
                dump(c, circuit, output);
            }

            for (char c = '0'; c <= '9'; c++) {
                dump(c, circuit, output);
            }

            dump('!', circuit, output);
            dump('@', circuit, output);
            dump('#', circuit, output);
            dump('$', circuit, output);
            dump('%', circuit, output);
            dump('&', circuit, output);
            dump('*', circuit, output);
            dump('(', circuit, output);
            dump(')', circuit, output);
            dump('-', circuit, output);
            dump('_', circuit, output);
            dump('+', circuit, output);
            dump('=', circuit, output);

            dump('^', circuit, output);


            dump('á', circuit, output);
            dump('é', circuit, output);
            dump('í', circuit, output);
            dump('ó', circuit, output);
            dump('ú', circuit, output);

            dump('ã', circuit, output);
            dump('õ', circuit, output);

            dump('Á', circuit, output);
            dump('É', circuit, output);
            dump('Í', circuit, output);
            dump('Ó', circuit, output);
            dump('Ú', circuit, output);

            dump('Ã', circuit, output);
            dump('Õ', circuit, output);

            logger.info("======================================");

            dump('a', circuit, output);
            dump('b', circuit, output);
            dump('d', circuit, output);
            dump('e', circuit, output);
            dump('f', circuit, output);
            dump('i', circuit, output);
            dump('k', circuit, output);
            dump('l', circuit, output);
            dump('o', circuit, output);
            dump('u', circuit, output);
            dump('v', circuit, output);
            dump('z', circuit, output);
            dump('A', circuit, output);
            dump('B', circuit, output);
            dump('Y', circuit, output);
            dump('0', circuit, output);
            dump('2', circuit, output);
            dump('6', circuit, output);
            dump('9', circuit, output);
            dump('á', circuit, output);
            dump('é', circuit, output);

        }

    }

    private static void dump(char c, Circuit circuit, int[] output) {

        String answer = null;

        try {
            answer = StringSolution.evaluate(circuit, output, Character.toString(c));
        } catch (Exception e) {
            answer = "error";
        }
        logger.info(c + " [" + answer + "]");

    }


}
