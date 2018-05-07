package org.genetics.circuit.problem.vowel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.problem.CircuitComparator;

public class VowelComparator implements CircuitComparator {
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(VowelComparator.class);
	
	private final List<Pair<String, Boolean>> orders;
	
	public VowelComparator() {
		this.orders = new ArrayList<Pair<String, Boolean>>();
		orders.add(Pair.of(VowelSuite.GRADE_HIT, Boolean.FALSE));
		orders.add(Pair.of(VowelSuite.GRADE_CIRCUIT_SIZE, Boolean.TRUE));
	}
	
	@Override
	public int compare(Circuit c1, Circuit c2) {
		
		int answer = 0;
		
		for (Pair<String, Boolean> pair : this.orders) {
			
			if (pair.getRight().booleanValue()) {
				answer = c1.getGrade(pair.getLeft(), Integer.class).compareTo(c2.getGrade(pair.getLeft(), Integer.class));
				if (answer != 0) {
					return answer;
				}
				
			}
			else {
				answer = c2.getGrade(pair.getLeft(), Integer.class).compareTo(c1.getGrade(pair.getLeft(), Integer.class));
				if (answer != 0) {
					return answer;
				}
				
			}
			
		}
		
		
		if (c1.size() != c2.size()) {
			logger.error(String.format("They should have the same size! [%d,%d]", c1.size(), c2.size()));
			System.exit(1);
			//logger.warn(CircuitToString.toString(getOuter(), c1));
			//logger.warn(CircuitToString.toString(getOuter(), c2));
		}
		
		int i = c1.size() - 1;
		while ((answer == 0) && (i >= 0)) {
			answer = c1.get(i).toString().compareTo(c2.get(i).toString());
			i--;
		}
		
		return answer;
	}

	public List<Pair<String, Boolean>> getOrders() {
		return this.orders;
	} 
	
	@Override
	public double similarity(Circuit c1, Circuit c2) {
		
		if (c1.getGrade(VowelSuite.GRADE_HIT, Integer.class).intValue() != c2.getGrade(VowelSuite.GRADE_HIT, Integer.class).intValue()) {
			return 0d;
		}
		
		int dist = getLevenshteinDistance(c1, c2);
		int max = Math.max(c1.size(), c2.size());
		
		return ((double)max - (double)dist) / (double)max;
	}
	
	public int getLevenshteinDistance (Circuit lhs, Circuit rhs) {                          
	    int len0 = lhs.size() + 1;                                                     
	    int len1 = rhs.size() + 1;                                                     
	                                                                                    
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	                                                                                    
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	                                                                                    
	    // dynamically computing the array of distances                                  
	                                                                                    
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {                                                
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;                                                             
	                                                                                    
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (lhs.get(i - 1).equals(rhs.get(j-1))) ? 0 : 1;             
	                                                                                    
	            // computing cost for each transformation                               
	            int cost_replace = cost[i - 1] + match;                                 
	            int cost_insert  = cost[i] + 1;                                         
	            int cost_delete  = newcost[i - 1] + 1;                                  
	                                                                                    
	            // keep minimum cost                                                    
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }                                                                           
	                                                                                    
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	                                                                                    
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];                                                          
	}

}
