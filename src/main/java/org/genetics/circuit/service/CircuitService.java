package org.genetics.circuit.service;


import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.dao.CircuitWrapperDao;
import org.genetics.circuit.dao.LockDao;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CircuitService {

	@Autowired
	private CircuitWrapperDao circuitWrapperDao;
	
	@Autowired
	private LockDao lockDao;
	
	public int orderedPersist(SuiteWrapper suiteWrapper, Circuit circuit) {
		
		String lockKey = null;
		int position = -1;
		try {
			if ((lockKey = lockDao.lock()) != null) {
				
				Limits limits = new Limits(circuitWrapperDao.getTotal(suiteWrapper));
				
				position = searchPositionToAdd(suiteWrapper, circuit, limits);
				
				if (position < 0) {
					int realPosition = ~position;
					circuitWrapperDao.updatePositions(suiteWrapper, realPosition);
					circuitWrapperDao.create(suiteWrapper, circuit, realPosition);
					
				}
			}
			else {
				System.err.println("Fail to lock database!");
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
			if ((lockKey = lockDao.lock()) != null) {
				circuit = circuitWrapperDao.findByPosition(suiteWrapper, position);
			}
			else {
				System.err.println("Fail to lock database!");
			}
			
		} finally {
			lockDao.release(lockKey);
		}
		
		return circuit;
	}
	
	public int size(SuiteWrapper suiteWrapper) {
		int size = 0;
		
		String lockKey = null;
		try {
			if ((lockKey = lockDao.lock()) != null) {
				size = circuitWrapperDao.getTotal(suiteWrapper);
			}
			else {
				System.err.println("Fail to lock database!");
			}
			
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
