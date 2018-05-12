package org.genetics.circuit.service;

import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.dao.SuiteWrapperDao;
import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.entity.SuiteWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

@Component
public class SuiteWrapperService {

	private static final Logger logger = LoggerFactory.getLogger(SuiteWrapperService.class);

	@Autowired
	private SuiteWrapperDao suiteWrapperDao;

	@Autowired
	private ProblemDao problemDao;

	public SuiteWrapper getLatest(String problemName) {

		Problem problem = problemDao.findByName(problemName);

		SuiteWrapper suiteWrapper = suiteWrapperDao.findLatest(problem);

		if (suiteWrapper == null) {
			throw new RuntimeException("No training set in the database");
		}

		return suiteWrapper;
	}

}