package org.genetics.circuit.circuit;


import org.genetics.circuit.pool.StatePool;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.solution.Solution;
import org.genetics.circuit.solution.TimeSlice;

public class CircuitHitsEvaluator {

	public static int evaluate(TrainingSet trainingSet, CircuitImpl circuit) {

		int score[][] = new int[circuit.size()][trainingSet.getOutputSize()];

		for (Solution solution : trainingSet.getSolutions()) {
			evaluate(circuit, solution, score);
		}

		return sumBetterHits(score);
	}

	private static int sumBetterHits(int score[][]) {
		int sum = 0;
		for (int i = 0; i < score[0].length; i++) {
			int better = 0;
			for (int j = 1; j < score.length; j++) {
				if (score[j][i] > score[better][i]) {
					better = j;
				}
			}
			sum += score[better][i];
		}

		return sum;
	}

	private static void evaluate(CircuitImpl circuit, Solution solution, int[][] score) {
		boolean state[] = null;

		try {
			state = StatePool.borrow(circuit.size());
			circuit.reset();

			for (TimeSlice timeSlice : solution) {
				circuit.assignInputToState(state, timeSlice.getInput());
				circuit.propagate(state);

				for (int i = 0; i < score.length; i++) {
					for (int j = 0; j < timeSlice.getOutput().length; j++) {
						if (state[i] == timeSlice.getOutput()[j]) {
							score[i][j]++;
						}
					}
				}
			}
		} finally {
			StatePool.retrieve(state);
		}
	}

}
