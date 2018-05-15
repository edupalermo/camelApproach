package org.genetics.circuit.command;

import org.genetics.camel.Application;
import org.genetics.camel.GenericRoute;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitOutputGenerator;
import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.dao.SuiteWrapperDao;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.vowel.VowelSuite;
import org.genetics.circuit.service.CircuitService;
import org.genetics.circuit.service.SuiteWrapperService;
import org.genetics.circuit.solution.StringSolution;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        ProblemDao problemDao = applicationContext.getBean(ProblemDao.class);
        suiteWrapperDao.create(problemDao.findByName("CHAR_TYPE"), new VowelSuite());
    }

}
