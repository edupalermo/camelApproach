package org.genetics.camel.mediator;

import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.SuiteWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.TreeMap;

@Controller
public class SuiteWrapperMediator {

    private Map<String, Long> time = new TreeMap<String, Long>();
    private Map<String, SuiteWrapper> cache = new TreeMap<String, SuiteWrapper>();

    private static final long TIMEOUT = 15 * 1000;

    @Autowired
    SuiteWrapperService suiteWrapperService;

    public SuiteWrapper getSuiteWrapper(String problemName) {

        if (problemName == null) {
            throw new RuntimeException("Cannot get suite wrapper of a nul problemName!");
        }

        SuiteWrapper suiteWrapper = cache.get(problemName);

        if (suiteWrapper == null || (System.currentTimeMillis() - time.get(problemName).longValue() > TIMEOUT)) {
            time.put(problemName, Long.valueOf(System.currentTimeMillis()));
            cache.put(problemName, suiteWrapper = suiteWrapperService.getLatest(problemName));
        }

        return suiteWrapper;
    }



}
