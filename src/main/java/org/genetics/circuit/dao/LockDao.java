package org.genetics.circuit.dao;

public interface LockDao {
	
    String lock();
    
    void release(String key);

}
