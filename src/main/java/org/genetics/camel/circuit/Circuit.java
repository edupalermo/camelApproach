package org.genetics.camel.circuit;

public class Circuit {

    private final String state;

    public Circuit(String state) {
        this.state = state;
    }

    public Circuit evolve(String state) {
        return new Circuit(state);
    }

    @Override
    public String toString() {
        return this.state;
    }
}
