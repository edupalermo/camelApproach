package org.genetics.circuit.circuit;

import org.genetics.circuit.port.*;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.solution.Solution;
import org.genetics.circuit.solution.TimeSlice;
import org.genetics.circuit.utils.CircuitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

public class CircuitNewSimplifier {

    public static boolean USE_STREAM = false;

    private static final Logger logger = LoggerFactory.getLogger(CircuitNewSimplifier.class);


    public static int simplify(TrainingSet trainingSet, CircuitImpl circuitImpl) {

        int maxHits = CircuitUtils.getTotalOfPossibleHitsPerPort(trainingSet);

        boolean state[][] = new boolean[getOutputsPerPort(trainingSet)][circuitImpl.size()];
        //logger.info("Populating state...");

        int outputStat[][] = populateState(trainingSet, circuitImpl, state); // count how many matches

        //logger.info("Getting output...");

        int[] output = getOutput(trainingSet, outputStat, maxHits);

        //logger.info("Removing unused port...");

        outputStat = simplifyByRemovingUnsedPorts(trainingSet, circuitImpl, state, output, outputStat);

        //logger.info("Lowering ports...");

        lowerPorts(trainingSet, circuitImpl, state);

        //logger.info("Getting output...");

        output = getOutput(trainingSet, outputStat, maxHits);

        //logger.info("Removing unused port...");

        outputStat = simplifyByRemovingUnsedPorts(trainingSet, circuitImpl, state, output, outputStat);

        //logger.info("Finished...");

        return getHits(outputStat, output);
    }

    private static int getOutputsPerPort(TrainingSet trainingSet) {
        int total = 0;

        for (Solution solution : trainingSet.getSolutions()) {
            total += solution.size();
        }

        return total;
    }

    private static int[][] populateState(TrainingSet trainingSet, CircuitImpl circuitImpl, boolean[][] state) {

        int counter = 0;

        int outputSize = trainingSet.getOutputSize();
        int outputStat[][] = new int[circuitImpl.size()][outputSize];

        for (int s = 0; s < trainingSet.getSolutions().size(); s++) {
            Solution solution = trainingSet.getSolutions().get(s);
            circuitImpl.reset();

            for (int t = 0; t < solution.size(); t++) {
                TimeSlice timeSlice = solution.get(t);
                boolean aux[] = state[counter++];
                circuitImpl.assignInputToState(aux, timeSlice.getInput(), timeSlice.getOutput(), outputStat);
                circuitImpl.propagate(aux, timeSlice.getOutput(), outputStat);
            }
        }

        return outputStat;
    }

    private static int[] getOutput(TrainingSet trainingSet, int outputStat[][], int maxValue) { // new int[circuitImpl.size()][outputSize];
        int output[] = new int[trainingSet.getOutputSize()];

        if (USE_STREAM) {
            IntStream.range(0, outputStat[0].length).parallel().forEach(i -> {
                for (int j = 0; j < outputStat.length; j++) { // circuit
                    if (outputStat[j][i] > outputStat[output[i]][i]) {
                        output[i] = j;
                        //logger.info(String.format("%d %d", outputStat[j][i], maxValue));
                        if (outputStat[j][i] == maxValue) {
                            //logger.info("0000000000000000000000 BREAK 000000000000000000000");
                            break;
                        }
                    }
                }
            });
        }
        else {
            for (int i = 0; i < outputStat[0].length; i++) { // output
                for (int j = 0; j < outputStat.length; j++) { // circuit
                    if (outputStat[j][i] > outputStat[output[i]][i]) {
                        output[i] = j;
                        if (outputStat[j][i] == maxValue) {
                            //logger.info("0000000000000000000000 BREAK 000000000000000000000");
                            break;
                        }
                    }
                }
            }
        }

        return output;

    }

    private static void lowerPorts(TrainingSet trainingSet, CircuitImpl circuitImpl, boolean[][] state) {

        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();


        TreeMap<Integer, Integer> orderedArrayMap = new TreeMap<Integer, Integer>(new StateComparator(state));

        // Prepare a Map with the state output
        for (int i = 0; i < circuitImpl.size(); i++) { // Higher
            Integer lowest = orderedArrayMap.get(Integer.valueOf(i));

            if (lowest == null) {
                orderedArrayMap.put(Integer.valueOf(i), Integer.valueOf(i));
            }
            else {
                map.put(i , lowest);
            }
        }


        Map<Integer, List<Integer>> reference = new TreeMap<Integer, List<Integer>>();
        for (int i = trainingSet.getInputSize(); i < circuitImpl.size(); i++) {
            for (int j : circuitImpl.get(i).getReferences()) {

                List<Integer> list = reference.get(j);

                if (list == null) {
                    reference.put(j, list = new ArrayList<Integer>());
                }

                list.add(i);
            }
        }


        // System.out.println(String.format("Simplifications %d", map.size()));

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            List<Integer> list = reference.get(entry.getKey());
            if (list != null) {
                if (USE_STREAM) {
                    list.parallelStream().forEach(i -> {
                        Port port = circuitImpl.get(i.intValue());
                        synchronized(port) {
                            port.adjust(entry.getKey().intValue(), entry.getValue().intValue());
                        }
                    });
                }
                else {
                    for (Integer i : list) {
                        circuitImpl.get(i.intValue()).adjust(entry.getKey().intValue(), entry.getValue().intValue());
                    }
                }

            }
        }

        //logger.info("Ajustes concluidos.");

    }

    private static int[][] simplifyByRemovingUnsedPorts(TrainingSet trainingSet, CircuitImpl circuit, boolean state[][], int[] output, int outputStat[][]) {

        List<Integer> canRemove = new ArrayList<Integer>();

        // Adding all ports!
        for (int i = trainingSet.getInputSize(); i < circuit.size(); i++) {
            canRemove.add(i);
        }

        // Removing port that can't be removed
        for (int i = 0; i < output.length; i++) {
            removeFromList(circuit, canRemove, output[i]);
        }

        //System.out.println("1");

        // 20012
        circuit.removePorts(canRemove);

        //System.out.println("2");

        if (USE_STREAM) {
            Arrays.parallelSetAll(output, i -> output[i] - countBelow(canRemove, output[i]));
        }
        else {
            for (int i = 0; i < output.length; i++) {
                output[i] -= countBelow(canRemove, output[i]);
            }
        }

        if (USE_STREAM) {
            Arrays.parallelSetAll(state, i -> removeFromList(canRemove, state[i]));
        }
        else {
            for (int i = 0; i < state.length; i++) {
                state[i] = removeFromList(canRemove, state[i]);
            }
        }

        return removeFromOutputStat(canRemove, outputStat);
    }


    private static void removeFromList(CircuitImpl circuit, List<Integer> canRemove, int index) {
        Port port = circuit.get(index);
        if (!(port instanceof PortInput)) {

            if (canRemove.isEmpty()) {
                return;
            }

            int pos = 0;
            if ((pos = Collections.binarySearch(canRemove, index)) >= 0) {
                canRemove.remove(pos);
            }
            else {
                return; // If did not have then it has been removed before and don't need to continue
            }

            if (port instanceof PortAnd) {
                removeFromList(circuit, canRemove, ((PortAnd) port).getMinor());
                removeFromList(circuit, canRemove, ((PortAnd) port).getMajor());
            } else if (port instanceof PortOr) {
                removeFromList(circuit, canRemove, ((PortOr) port).getMinor());
                removeFromList(circuit, canRemove, ((PortOr) port).getMajor());
            } else if (port instanceof PortNand) {
                removeFromList(circuit, canRemove, ((PortNand) port).getMinor());
                removeFromList(circuit, canRemove, ((PortNand) port).getMajor());
            } else if (port instanceof PortNor) {
                removeFromList(circuit, canRemove, ((PortNor) port).getMinor());
                removeFromList(circuit, canRemove, ((PortNor) port).getMajor());
            } else if (port instanceof PortNot) {
                removeFromList(circuit, canRemove, ((PortNot) port).getIndex());
            } else if (port instanceof PortMemorySetReset) {
                removeFromList(circuit, canRemove, ((PortMemorySetReset) port).getMinor());
                removeFromList(circuit, canRemove, ((PortMemorySetReset) port).getMajor());
            } else {
                throw new RuntimeException("Inconsistency!");
            }

        }
    }

    private static int countBelow(List<Integer> list, int index) {
        int total = 0;
        for (int i : list) {
            if (i < index) {
                total++;
            }
            if (i >= index) {
                break;
            }
        }
        return total;
    }

    private static boolean[] removeFromList(List<Integer> list, boolean input[]) {
        if ((list == null) || (list.size() == 0)) {
            return input;
        }

        boolean output[] = new boolean[input.length - list.size()];

        int oi = 0;
        int ri = 0;
        for (int i = 0; i < input.length; i++) {
            if (list.get(ri) == i) {
                ri = inc(list, ri);
            }
            else {
                output[oi++] = input[i];
            }
        }
        return output;
    }

    private static int[][] removeFromOutputStat(List<Integer> list, int input[][]) {
        if ((list == null) || (list.size() == 0)) {
            return input;
        }

        int output[][] = new int[input.length - list.size()][];

        int oi = 0;
        int ri = 0;
        for (int i = 0; i < input.length; i++) {
            if (list.get(ri) == i) {
                ri = inc(list, ri);
            }
            else {
                output[oi++] = input[i];
            }
        }
        return output;
    }

    private static int inc(List<Integer> list, int actual) {
        int output = actual + 1;

        if (output >= list.size()) {
            output = list.size() -1;
        }

        return output;
    }

    private static int getHits(int outputStat[][], int[] output) {
        int total = 0;

        //if (USE_STREAM) {
        //    total = Arrays.stream(output).map(i -> outputStat[output[i]][i]).sum();
        //}
        //else {
            for (int i = 0; i < output.length; i++) {
                total += outputStat[output[i]][i];
            }
        //}
        return total;
    }


    private static class StateComparator implements Comparator<Integer> {

        private final boolean[][] state;

        public StateComparator(boolean[][] state) {
            this.state = state;
        }

        @Override
        public int compare(Integer i1, Integer i2) {
            int result = 0;

            for (int i = 0; i < state.length; i++) {
                if (state[i][i1.intValue()] != state[i][i2.intValue()]) {
                    result = !state[i][i1.intValue()] ? -1 : 1;
                }
            }

            return result;
        }

    }

}
