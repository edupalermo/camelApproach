package org.genetics.circuit.circuit;

import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.port.Port;
import org.genetics.circuit.port.PortInput;

import java.util.*;

import static org.genetics.circuit.configuration.Configuration.CHECK_CONSISTENCY;

public class CircuitImpl extends ArrayList<Port> implements Circuit {

	private static final long serialVersionUID = 1L;

	private CircuitImpl() {
	}

	public CircuitImpl(int size) {
		for (int i = 0; i < size; i++) {
			this.add(new PortInput(i));
		}
	}
	public boolean[] generateInitialState() {
		return new boolean[size()];
	}

	public void assignInputToState(boolean state[], boolean input[]) {
		for (int i = 0; i < input.length; i++) {
			state[i] = input[i];
		}
	}

	public void propagate(boolean state[]) {
		for (int i = 0; i < this.size(); i++) {
			state[i] = this.get(i).evaluate(state);
		}
	}

	public void reset() {
		for (Port port : this) {
			port.reset();
		}
	}

	public void removePort(int index) {
		
		if (CHECK_CONSISTENCY) {
			//logger.info(String.format("Checking [%d] size [%d]", index, size()));
			for (int i = size()-1; i >= index + 1; i--) {
				//logger.info(String.format("Checking [%d] %s", index, get(i).toString()));
				if (get(i).references(index)) {
					//logger.info(String.format("Recurring on [%d]", i));
					throw new RuntimeException("Inconsistency");
				}
			}
		}
		
		for (int i = index + 1; i < size(); i++) {
			this.get(i).adustLeft(index);
		}
		//logger.info("Removing port: " + this.get(index).toString());
		this.remove(index);
	}

	public void removePorts(List<Integer> ports) {
		if (ports.size() == 0) {
			return;
		}

		int indexed[] = transform(ports);

		int ri = ports.size() - 1;
		for (int i = size()-1; i >=0; i--) {
			Port port = this.get(i);
			if (!(port instanceof PortInput)) {
				this.get(i).adustLeft(indexed);

				if (i == ports.get(ri)) {
					this.remove(i);

					if (ri > 0) {
						ri--;
					}
					else {
						break;
					}
				}
			}
		}
	}

	private int[] transform(List<Integer> ports) {
		int output[] = new int[this.size()];

		int count = 0;

		for (int i = 0; i < output.length; i++) {
			output[i] = count;
			if ((count < ports.size()) && (i == ports.get(count).intValue())) {
				count++;
			}
		}

		return output;
	}

	@Override
	public CircuitImpl clone() {
		CircuitImpl circuit = new CircuitImpl();
		for (Port port : this) {
			circuit.add((Port) port.clone());
		}

		return circuit;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < this.size(); i++) {
			sb.append("[").append(i).append(" ").append(this.get(i).toString()).append("] ");
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	@Override
	public double similarity(Circuit other) {

		if (!(other instanceof CircuitImpl)) {
			throw new RuntimeException(String.format("Impossible to comprare similarity of different types! %s", other.getClass().getName()));
		}

		int dist = getLevenshteinDistance(this, (CircuitImpl) other);
		int max = Math.max(this.size(), ((CircuitImpl)other).size());

		return ((double)max - (double)dist) / (double)max;

	}

	private int getLevenshteinDistance (CircuitImpl lhs, CircuitImpl rhs) {
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

	public void assignInputToState(boolean state[], boolean input[], boolean[] output, int[][] outputStat) {
		for (int i = 0; i < input.length; i++) {
			state[i] = input[i];

			for (int j = 0; j < output.length; j++) {
				if (output[j] == state[i]) {
					outputStat[i][j]++;
				}
			}
		}
	}

	public void propagate(boolean state[], boolean[] output, int[][] outputStat) {
		for (int i = 0; i < this.size(); i++) {
			if (!(this.get(i) instanceof PortInput)) {
				state[i] = this.get(i).evaluate(state);

				for (int j = 0; j < output.length; j++) {
					if (output[j] == state[i]) {
						outputStat[i][j]++;
					}
				}
			}
		}
	}



}
