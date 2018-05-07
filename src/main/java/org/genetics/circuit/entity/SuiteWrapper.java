package org.genetics.circuit.entity;

import org.genetics.circuit.problem.Suite;

import java.time.LocalDateTime;


public class SuiteWrapper {
	
	private int id;
	private LocalDateTime created;
	private Suite suite;
	private Problem problem;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public LocalDateTime getCreated() {
		return created;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public Suite getSuite() {
		return suite;
	}

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
}
