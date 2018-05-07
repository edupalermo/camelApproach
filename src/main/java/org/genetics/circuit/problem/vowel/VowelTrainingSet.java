package org.genetics.circuit.problem.vowel;

import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.solution.Solution;
import org.genetics.circuit.solution.StringSolution;

import java.util.ArrayList;
import java.util.List;

public class VowelTrainingSet implements TrainingSet {
	
	private static final long serialVersionUID = 1L;

	private List<Solution> solutions = new ArrayList<Solution>();
	
	private final int inputSize;

	private final int outputSize;

	public VowelTrainingSet() {
		solutions.add(new StringSolution("a", "vogal"));
		solutions.add(new StringSolution("b", "consoante"));
		solutions.add(new StringSolution("c", "consoante"));
		solutions.add(new StringSolution("d", "consoante"));
		solutions.add(new StringSolution("e", "vogal"));
		solutions.add(new StringSolution("f", "consoante"));
		solutions.add(new StringSolution("g", "consoante"));
		solutions.add(new StringSolution("h", "consoante"));
		solutions.add(new StringSolution("i", "vogal"));
		
		solutions.add(new StringSolution("l", "consoante"));
		solutions.add(new StringSolution("m", "consoante"));
		solutions.add(new StringSolution("n", "consoante"));
		solutions.add(new StringSolution("o", "vogal"));
		solutions.add(new StringSolution("p", "consoante"));
		solutions.add(new StringSolution("q", "consoante"));
		solutions.add(new StringSolution("u", "vogal"));
		solutions.add(new StringSolution("y", "consoante"));
		solutions.add(new StringSolution("z", "consoante"));
		
		solutions.add(new StringSolution("A", "vogal"));
		solutions.add(new StringSolution("D", "consoante"));
		solutions.add(new StringSolution("E", "vogal"));
		solutions.add(new StringSolution("F", "consoante"));
		solutions.add(new StringSolution("H", "consoante"));
		solutions.add(new StringSolution("I", "vogal"));
		solutions.add(new StringSolution("M", "consoante"));
		solutions.add(new StringSolution("O", "vogal"));
		solutions.add(new StringSolution("Q", "consoante"));
		solutions.add(new StringSolution("U", "vogal"));
		solutions.add(new StringSolution("Y", "consoante"));
		solutions.add(new StringSolution("Z", "consoante"));
		
		solutions.add(new StringSolution("0", "número"));
		solutions.add(new StringSolution("1", "número"));
		solutions.add(new StringSolution("2", "número"));
		solutions.add(new StringSolution("4", "número"));
		solutions.add(new StringSolution("5", "número"));
		solutions.add(new StringSolution("8", "número"));
		solutions.add(new StringSolution("9", "número"));
		
		solutions.add(new StringSolution("á", "vogal"));
		solutions.add(new StringSolution("à", "vogal"));
		solutions.add(new StringSolution("ã", "vogal"));
		solutions.add(new StringSolution("é", "vogal"));
		solutions.add(new StringSolution("ó", "vogal"));
		solutions.add(new StringSolution("ô", "vogal"));
		solutions.add(new StringSolution("ú", "vogal"));
		
		this.inputSize = this.solutions.get(0).get(0).getInput().length ;
		this.outputSize = this.solutions.get(0).get(0).getOutput().length ;;
		
	}

	@Override
	public int getInputSize() {
		return this.inputSize;
	}

	@Override
	public int getOutputSize() {
		return this.outputSize;
	}

	@Override
	public List<Solution> getSolutions() {
		return this.solutions;
	}
	
	

}
