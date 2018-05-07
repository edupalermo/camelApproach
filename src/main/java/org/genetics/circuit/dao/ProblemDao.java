package org.genetics.circuit.dao;

import org.genetics.circuit.entity.Problem;

public interface ProblemDao {
	
    Problem findByName(String name);
    
    Problem getById(int id);

}
