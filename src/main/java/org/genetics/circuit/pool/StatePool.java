package org.genetics.circuit.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class StatePool {
	
	private static final Logger logger = Logger.getLogger(StatePool.class);
	
	private static final List<Integer> indexList = new ArrayList<Integer>();
	private static final List<List<boolean[]>> statePool = new ArrayList<List<boolean[]>>();

	private static final int MULTIPLE = 20000;
	
	public synchronized static boolean[] borrow(int size) {
		boolean[] result = null;
		
		int transaformedSize = transformSize(size);
		int searchIndex = Collections.binarySearch(indexList, Integer.valueOf(transaformedSize));
		
		if (searchIndex < 0) {
			result = instantiate(transaformedSize);
		}
		else {
			List<boolean[]> l = statePool.get(searchIndex); 
			
			if (l.size() > 0) {
				result = l.remove(0);
				for (int i = 0; i < result.length; i++) {
					result[i] = false;
				}
			}
			else {
				result = instantiate(transaformedSize);
			}
		}
		
		if (result.length < size) {
			throw new RuntimeException("Inconsistency");
		}
		
		return result;
	}
	
	public synchronized static void retrieve(boolean array[]) {
		int searchIndex = Collections.binarySearch(indexList, Integer.valueOf(array.length));
		
		if (searchIndex < 0) {
			int pos = ~searchIndex;
			indexList.add(pos, array.length);
			
			List<boolean[]> l =new ArrayList<boolean[]>();
			l.add(array);
			statePool.add(pos, l);
		}
		else {
			statePool.get(searchIndex).add(array);
		}

	}

	
	private static boolean[] instantiate(int size) {
		logger.info(String.format("Instantiating StatePool [%d]...", size));
		return new boolean[size];
	}



	private static int transformSize(int size) {
		return MULTIPLE * (1 + (size / MULTIPLE));
	}
	
	public static void main(String[] args) {
		boolean[] b1 = borrow(100);
		boolean[] b2 = borrow(100);
		
		retrieve(b1);
		retrieve(b2);
		
		b1 = borrow(100);
		b2 = borrow(100);
		
		
	}
	

}
