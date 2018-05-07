package org.genetics.circuit.circuit;

import org.apache.commons.lang3.tuple.Pair;
import org.genetics.circuit.entity.SuiteWrapper;

public class CircuitToString {
	
	private static final String BUFFER_TO_SMALL_STRING = "toSmallString";
	private static final String BUFFER_TO_STRING = "toString";
	
	public static String toString(SuiteWrapper suiteWrapper, Circuit circuit) {
		
		String temporary = circuit.getBuffer(BUFFER_TO_STRING, String.class);
		if (temporary != null) {
			return temporary;
		}
		
		StringBuffer sb = new StringBuffer();

		sb.append(toSmallString(suiteWrapper, circuit));
		sb.append(" ");
		
		for (int i = 0; i < circuit.size(); i++) {
			sb.append("[").append(i).append(" ").append(circuit.get(i).toString()).append("] ");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		circuit.setBuffer(BUFFER_TO_STRING, sb.toString());
		
		return sb.toString();
	}

	public static String toSmallString(SuiteWrapper suiteWrapper, Circuit circuit) {
		
		String temporary = circuit.getBuffer(BUFFER_TO_SMALL_STRING, String.class);
		if (temporary != null) {
			return temporary;
		}
		
		StringBuffer sb = new StringBuffer();
		
		for (Pair<String, Boolean> pair : suiteWrapper.getSuite().getCircuitComparator().getOrders()) {
			
			sb.append("[");
			sb.append(pair.getLeft());
			sb.append("=");
			sb.append(circuit.getGrade(pair.getLeft(), Integer.class).toString());
			sb.append("] ");
			
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		circuit.setBuffer(BUFFER_TO_SMALL_STRING, sb.toString());
		
		return sb.toString();
	}

}
