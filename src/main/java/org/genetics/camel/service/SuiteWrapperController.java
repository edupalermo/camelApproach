package org.genetics.camel.service;

import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.service.SuiteWrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.concurrent.atomic.AtomicReference;

@Controller
public class SuiteWrapperController {

    public static final String PROBLEM_NAME = "CHAT_TYPE";

    private AtomicReference<SuiteWrapper> cache = null;

    @Autowired
    SuiteWrapperService suiteWrapperService;

    private static Logger logger = LoggerFactory.getLogger(SuiteWrapperController.class);

    public SuiteWrapper getSuiteWrapper() {
        if (cache == null) {
            cache = new AtomicReference<SuiteWrapper>(suiteWrapperService.getLatest(PROBLEM_NAME));
        }

        return cache.get();
    }

    public int getSuiteWrapperId() {
        return getSuiteWrapper().getId();
    }


    public void checkVersion() {
        SuiteWrapper newSuiteWrapper = suiteWrapperService.getLatest(PROBLEM_NAME);
        if (getSuiteWrapperId() != newSuiteWrapper.getId()) {
            logger.info("New Suite Wrapper version detected!");
            cache.lazySet(newSuiteWrapper);
        }
    }



}
