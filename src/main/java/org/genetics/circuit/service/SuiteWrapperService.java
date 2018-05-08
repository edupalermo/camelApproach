package org.genetics.circuit.service;

import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.dao.SuiteWrapperDao;
import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.entity.SuiteWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuiteWrapperService {

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