package org.genetics.circuit.entity;

import org.genetics.circuit.problem.Evaluator;

import java.time.LocalDateTime;

public class EvaluatorWrapper {
	
	private int id;
	private LocalDateTime created;
	private Problem problem;
	private Evaluator evaluator;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Evaluator getEvaluator() {
		return evaluator;
	}
	
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public LocalDateTime getCreated() {
		return created;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public Problem getProblem() {
		return problem;
	}
	

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
}
