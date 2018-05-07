package org.genetics.circuit.entity;

import org.genetics.circuit.circuit.Circuit;

import java.time.LocalDateTime;

public class CircuitWrapper {
	
	private int id;
	private LocalDateTime created;
	private Problem problem;
	private int position;
	private Circuit circuit;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Circuit getCircuit() {
		return circuit;
	}
	
	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
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

	public int getPosition() {
		return position;
	}
	

	public void setPosition(int position) {
		this.position = position;
	}
	
}
