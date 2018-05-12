package org.genetics.circuit.circuit;

public class CircuitContextDecorator extends Circuit {

    private final Circuit circuit;

    public CircuitContextDecorator(Circuit circuit) {
        this.circuit = circuit;
    }

}
