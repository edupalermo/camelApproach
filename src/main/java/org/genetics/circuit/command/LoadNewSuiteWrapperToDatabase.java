package org.genetics.circuit.command;

import org.genetics.camel.Application;
import org.genetics.camel.GenericRoute;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitContextDecorator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.circuit.CircuitOutputGenerator;
import org.genetics.circuit.dao.CircuitWrapperDao;
import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.dao.SuiteWrapperDao;
import org.genetics.circuit.entity.CircuitWrapper;
import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.vowel.VowelSuite;
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
// @EnableAutoConfiguration(exclude = {GenericRoute.class})
public class LoadNewSuiteWrapperToDatabase {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        SpringApplication springApplication = new SpringApplication(LoadNewSuiteWrapperToDatabase.class);


        Properties properties = new Properties();
        properties.setProperty("camel.springboot.main-run-controller", "false");
        springApplication.setDefaultProperties(properties);

        ApplicationContext applicationContext = springApplication.run(args);

        SuiteWrapperDao suiteWrapperDao = applicationContext.getBean(SuiteWrapperDao.class);
        CircuitService circuitService = applicationContext.getBean(CircuitService.class);
        ProblemDao problemDao = applicationContext.getBean(ProblemDao.class);

        Problem problem = problemDao.findByName("CHAR_TYPE");

        SuiteWrapper oldSuiteWrapper = suiteWrapperDao.findLatest(problem);
        SuiteWrapper newSuiteWrapper = suiteWrapperDao.create(problem, new VowelSuite());

        for (int i = 0; i < circuitService.size(oldSuiteWrapper); i++) {
            CircuitImpl circuitImpl = (CircuitImpl) circuitService.findByPosition(oldSuiteWrapper, i);
            CircuitContextDecorator ccd = new CircuitContextDecorator(newSuiteWrapper, circuitImpl);
            ccd.evaluate();
            circuitService.orderedPersist(ccd);
        }

        int exitCode = SpringApplication.exit(applicationContext, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                // no errors
                return 0;
            }
        });

        System.exit(exitCode);

    }

}
