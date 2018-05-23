package org.genetics.circuit.problem.vowel;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitHitsEvaluator;
import org.genetics.circuit.circuit.CircuitImpl;
import org.genetics.circuit.circuit.CircuitNewSimplifier;
import org.genetics.circuit.problem.EvaluationResult;
import org.genetics.circuit.problem.Evaluator;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.utils.CircuitUtils;

import java.io.Serializable;

public class VowelEvaluator implements Evaluator, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public EvaluationResult evaluate(TrainingSet trainingSet, Circuit circuit) {

		Result.ResultBuilder resultBuilder = Result.getBuilder();
		resultBuilder.hit(CircuitHitsEvaluator.evaluate(trainingSet, (CircuitImpl) circuit));
		resultBuilder.circuitSize(((CircuitImpl) circuit).size());

		return resultBuilder.build();
	}

	@Override
	public EvaluationResult simplifyAndEvaluate(TrainingSet trainingSet, Circuit circuit) {

		CircuitImpl circuitImpl = CircuitUtils.getCircuitImpl(circuit);

		int hits = CircuitNewSimplifier.simplify(trainingSet, circuitImpl);

		Result.ResultBuilder resultBuilder = Result.getBuilder();
		resultBuilder.hit(hits);
		resultBuilder.circuitSize(circuitImpl.size());

		return resultBuilder.build();
	}

	public static class Result implements EvaluationResult<Result> {

		private final int hit;
		private final int circuitSize;

		public Result(ResultBuilder resultBuilder) {
			this.hit = resultBuilder.hit;
			this.circuitSize = resultBuilder.circuitSize;

		}

		// Compares this object with the specified object for order. Returns a negative integer, zero, or a positive
		// integer as this object is less than, equal to, or greater than the specified object.
		@Override
		public int compareTo(Result result) {
			int response = (-1) * Integer.compare(this.hit, result.getHit());

			if (response == 0) {
				response = Integer.compare(this.circuitSize, result.getCircuitSize());
			}


			return response;
		}

		@Override
		public double similarity(Result other) {
			return this.hit == other.hit ? 1 : 0;
		}

		public int getHit() {
			return hit;
		}

		public int getCircuitSize() {
			return circuitSize;
		}

		public static ResultBuilder getBuilder() {
			return new ResultBuilder();
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();

			sb.append("[HIT=").append(this.hit).append("] ");
			sb.append("[SIZE=").append(this.circuitSize).append("]");

			return sb.toString();
		}

		public static class ResultBuilder {

			protected int hit;
			protected int circuitSize;

			private ResultBuilder() {}

			public ResultBuilder hit(int hit) {
				this.hit = hit;
				return this;
			}

			public ResultBuilder circuitSize(int circuitSize) {
				this.circuitSize = circuitSize;
				return this;
			}

			public Result build() {
				return new Result(this);
			}

		}
	}
	
}
