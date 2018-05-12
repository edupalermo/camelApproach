package org.genetics.circuit.service;


import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.dao.CircuitWrapperDao;
import org.genetics.circuit.dao.LockDao;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CircuitService {

	private static final Logger logger = LoggerFactory.getLogger(CircuitService.class);

	private static final int POPULATION_LIMIT = 10000;

	@Autowired
	private CircuitWrapperDao circuitWrapperDao;
	
	@Autowired
	private LockDao lockDao;
	
	public int orderedPersist(SuiteWrapper suiteWrapper, Circuit circuit) {
		
		String lockKey = null;
		int position = -1;
		try {
			lockKey = lock();

			Limits limits = new Limits(circuitWrapperDao.getTotal(suiteWrapper));

			position = searchPositionToAdd(suiteWrapper, circuit, limits);

			if (position < 0) {
				int realPosition = ~position;
				circuitWrapperDao.updatePositions(suiteWrapper, realPosition);
				circuitWrapperDao.create(suiteWrapper, circuit, realPosition);

			}

			int size = 0;
			while ((size = circuitWrapperDao.getTotal(suiteWrapper)) > POPULATION_LIMIT) {
				circuitWrapperDao.delete(suiteWrapper, size - 1);
			}

		} finally {
			lockDao.release(lockKey);
		}
		
		return position;
	}
	
	public Circuit findByPosition(SuiteWrapper suiteWrapper, int position) {
		Circuit circuit = null;
		
		String lockKey = null;
		try {
			lockKey = lock();
			circuit = circuitWrapperDao.findByPosition(suiteWrapper, position);

		} finally {
			lockDao.release(lockKey);
		}
		
		return circuit;
	}
	
	public int size(SuiteWrapper suiteWrapper) {
		int size = 0;
		
		String lockKey = null;
		try {
			lockKey = lock();
			size = circuitWrapperDao.getTotal(suiteWrapper);

		} finally {
			lockDao.release(lockKey);
		}
		
		return size;
	}
	
	
	private int searchPositionToAdd(SuiteWrapper suiteWrapper, Circuit circuit, Limits limits) {
		int position = 0; 
		
		if (!limits.shouldContinue()) {
			 position = limits.getSearchResult();
		}
		else {
			int positionToEvaluate = limits.getPositionToEvaluate();
			Circuit evaluate = circuitWrapperDao.findByPosition(suiteWrapper, positionToEvaluate);
			SuiteWrapperUtil.evaluate(suiteWrapper, evaluate);
			
			limits.setComparationResult(positionToEvaluate, SuiteWrapperUtil.compare(suiteWrapper, evaluate, circuit));
			
			position = searchPositionToAdd(suiteWrapper, circuit, limits);
		}
		
		return position;
	}

	private String lock() {
		String lockKey = null;

		int count = 0;

		while ((lockKey = lockDao.lock()) == null) {
			logger.warn("Fail to lock database access!");
			count++;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			if (lockKey == null && count >= 10) {
				throw new RuntimeException(String.format("Fail to obtain lock after %d attempts", count));
			}
		}

		return lockKey;
	}
	
	public static final class Limits {
		
		private boolean found = false;
		
		private int min;
		private int max;
		
		public Limits(int size) {
			this.min = -1;
			this.max = size;
		}
		
		public boolean shouldContinue() {
			return !found && (max - min) > 1;
		}
		
		public int getSearchResult() {
			if (found) {
				return this.max;
			}
			else {
				return (-(this.max) - 1);
			}
		}
		
		public int getPositionToEvaluate() {
			return min + ((max - min)/2);
			
		}
		
		public void setComparationResult(int positionEvaluated, int comparationResult) {
			if (comparationResult == 0) {
				found = true;
				this.max = positionEvaluated;
			}
			else if (comparationResult < 0) {
				this.min = positionEvaluated;
			}
			else { // comparationResult > 0
				this.max = positionEvaluated;
			}
		}
		
	}
	
}
