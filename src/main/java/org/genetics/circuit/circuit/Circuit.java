package org.genetics.circuit.circuit;

import java.io.Serializable;

public interface Circuit extends Cloneable, Serializable {

    public boolean[] generateInitialState();
    public void reset();
    public void assignInputToState(boolean state[], boolean input[]);
    public void propagate(boolean state[]);

    public Circuit clone();

    public double similarity(Circuit other);

}
