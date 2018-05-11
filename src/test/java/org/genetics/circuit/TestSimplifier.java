package org.genetics.circuit;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.circuit.CircuitScramble;
import org.genetics.circuit.circuit.CircuitToString;
import org.genetics.circuit.problem.CircuitComparator;
import org.genetics.circuit.problem.TrainingSet;
import org.genetics.circuit.problem.vowel.VowelSuite;
import org.genetics.circuit.utils.CircuitUtils;
import org.genetics.circuit.utils.IoUtils;
import org.genetics.circuit.utils.SuiteWrapperUtil;
import org.junit.Test;


public class TestSimplifier {

    @Test
    public void testSimplifier() {

        VowelSuite suite = new VowelSuite();
        CircuitComparator comparator = suite.getCircuitComparator();
        TrainingSet trainingSet = suite.getTrainingSet();

        Circuit c0 = IoUtils.base64ToObject("rO0ABXNyACRvcmcuZ2VuZXRpY3MuY2lyY3VpdC5jaXJjdWl0LkNpcmN1aXQAAAAAAAAAAQIAAHhyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAAddwQAAAAdc3IAI29yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydElucHV0AAAAAAAAAAECAAFJAAVpbmRleHhyAB5vcmcuZ2VuZXRpY3MuY2lyY3VpdC5wb3J0LlBvcnQAAAAAAAAAAQIAAHhwAAAAAHNxAH4AAwAAAAFzcQB-AAMAAAACc3EAfgADAAAAA3NxAH4AAwAAAARzcQB-AAMAAAAFc3EAfgADAAAABnNxAH4AAwAAAAdzcQB-AAMAAAAIc3EAfgADAAAACXNxAH4AAwAAAApzcQB-AAMAAAALc3IAIW9yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydE5vcgAAAAAAAAABAgACSQAFbWFqb3JJAAVtaW5vcnhxAH4ABAAAAAgAAAAHc3IAIG9yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydE9yAAAAAAAAAAECAAJJAAVtYWpvckkABW1pbm9yeHEAfgAEAAAACAAAAAdzcgAsb3JnLmdlbmV0aWNzLmNpcmN1aXQucG9ydC5Qb3J0TWVtb3J5U2V0UmVzZXQAAAAAAAAAAQIAA0kABW1ham9ySQAFbWlub3JJAAR0eXBleHEAfgAEAAAACwAAAAMAAAAAc3EAfgAVAAAADAAAAAEAAAAAc3EAfgARAAAADwAAAAJzcQB-ABEAAAALAAAABnNxAH4AFQAAABAAAAAMAAAAAHNxAH4AFQAAAA0AAAANAAAAAXNxAH4AEQAAABAAAAALc3IAIW9yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydE5vdAAAAAAAAAABAgABSQAFaW5kZXh4cQB-AAQAAAATc3EAfgATAAAAEgAAAA9zcQB-ABEAAAARAAAADnNyACFvcmcuZ2VuZXRpY3MuY2lyY3VpdC5wb3J0LlBvcnRBbmQAAAAAAAAAAQIAAkkABW1ham9ySQAFbWlub3J4cQB-AAQAAAAVAAAAFHNxAH4AHQAAABdzcQB-ABUAAAAWAAAAEAAAAABzcQB-ACEAAAATAAAAEXNxAH4AFQAAABkAAAARAAAAAHg=", Circuit.class);
        SuiteWrapperUtil.evaluate(suite, c0);
        Circuit c1 = IoUtils.base64ToObject("rO0ABXNyACRvcmcuZ2VuZXRpY3MuY2lyY3VpdC5jaXJjdWl0LkNpcmN1aXQAAAAAAAAAAQIAAHhyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAAgdwQAAAAgc3IAI29yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydElucHV0AAAAAAAAAAECAAFJAAVpbmRleHhyAB5vcmcuZ2VuZXRpY3MuY2lyY3VpdC5wb3J0LlBvcnQAAAAAAAAAAQIAAHhwAAAAAHNxAH4AAwAAAAFzcQB-AAMAAAACc3EAfgADAAAAA3NxAH4AAwAAAARzcQB-AAMAAAAFc3EAfgADAAAABnNxAH4AAwAAAAdzcQB-AAMAAAAIc3EAfgADAAAACXNxAH4AAwAAAApzcQB-AAMAAAALc3IAIW9yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydE5vcgAAAAAAAAABAgACSQAFbWFqb3JJAAVtaW5vcnhxAH4ABAAAAAgAAAAHc3IAIG9yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydE9yAAAAAAAAAAECAAJJAAVtYWpvckkABW1pbm9yeHEAfgAEAAAACAAAAAdzcgAsb3JnLmdlbmV0aWNzLmNpcmN1aXQucG9ydC5Qb3J0TWVtb3J5U2V0UmVzZXQAAAAAAAAAAQIAA0kABW1ham9ySQAFbWlub3JJAAR0eXBleHEAfgAEAAAADAAAAAEAAAAAc3EAfgARAAAADgAAAAJzcQB-ABEAAAALAAAABnNxAH4AFQAAAA8AAAAMAAAAAHNxAH4AFQAAAA0AAAANAAAAAXNxAH4AEQAAAA8AAAALc3IAIW9yZy5nZW5ldGljcy5jaXJjdWl0LnBvcnQuUG9ydE5vdAAAAAAAAAABAgABSQAFaW5kZXh4cQB-AAQAAAASc3EAfgATAAAAEQAAAA5zcQB-ABEAAAAEAAAAAXNyACFvcmcuZ2VuZXRpY3MuY2lyY3VpdC5wb3J0LlBvcnRBbmQAAAAAAAAAAQIAAkkABW1ham9ySQAFbWlub3J4cQB-AAQAAAAUAAAAE3NxAH4AHAAAAAdzcQB-ABUAAAAVAAAADwAAAABzcQB-ACAAAAASAAAAEHNxAH4AFQAAABgAAAAQAAAAAHNxAH4AFQAAABsAAAAWAAAAAHNxAH4AFQAAABwAAAAJAAAAAXNxAH4AEwAAABgAAAAGc3EAfgAVAAAAHgAAAB0AAAABeA==", Circuit.class);
        SuiteWrapperUtil.evaluate(suite, c1);

        System.out.println(CircuitToString.toSmallString(suite, c0) + " - " + c0.size());
        System.out.println(CircuitToString.toSmallString(suite, c1) + " - " + c1.size());

        Circuit c2 = join(trainingSet, (Circuit) c0.clone(), (Circuit) c1.clone());
        simplify(trainingSet, c2);
        SuiteWrapperUtil.evaluate(suite, c2);
        Circuit c3 = join(trainingSet, (Circuit) c1.clone(), (Circuit) c0.clone());
        simplify(trainingSet, c3);
        SuiteWrapperUtil.evaluate(suite, c3);

        System.out.println(CircuitToString.toSmallString(suite, c2) + " - " + c2.size());
        System.out.println(CircuitToString.toSmallString(suite, c3) + " - " + c3.size());

    }

    private Circuit join(TrainingSet trainingSet, Circuit c1, Circuit c2) {
        return CircuitScramble.join(trainingSet, c1, c2);
    }

    private void simplify(TrainingSet trainingSet, Circuit circuit) {
        if (circuit.size() > 3000) { // This is done in better join, but some time it is better to do it first or we can run out of memory
            CircuitUtils.simplifyByRemovingUnsedPorts(trainingSet, circuit);
        }
        CircuitUtils.betterSimplify(trainingSet, circuit);
    }

}
