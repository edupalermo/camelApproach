package org.genetics.circuit.datahandler;

import org.genetics.circuit.utils.IoUtils;

import java.io.*;
import java.util.*;

public class DoubleAsDouble {

    private final List<Double> list;

    //private final int[] fixedBits;
    private final int[] variableBits;


    private final byte[] rawData;

    private final int bitRecordSize;

    public DoubleAsDouble(List<Double> list) {
        this.list = list;

        this.rawData = getRawData();

        this.bitRecordSize = 8 * (this.rawData.length / list.size());

        List<Integer> fixedBits = getFixedMask(this.rawData, list.size());

        this.variableBits  = invert(fixedBits, bitRecordSize);
        //this.fixedBits = fixedBits.stream().mapToInt(i -> i.intValue()).toArray();
    }

    private byte[] getRawData() {
        DataOutputStream stream = null;

        byte[] output = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stream = new DataOutputStream(baos);

            for (double d : list) {
                stream.writeDouble(d);
            }

            stream.flush();

            output = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(stream);
        }
        return output;
    }


    public boolean[] getVariableBits(double input) {
        DataOutputStream stream = null;

        boolean[] output = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stream = new DataOutputStream(baos);

            stream.writeDouble(input);
            stream.flush();

            byte[] array = baos.toByteArray();
            output = new boolean[variableBits.length];

            for (int i = 0; i < output.length; i++) {
                output[i] = getBit(array, this.variableBits[i]) == 1;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(stream);
        }
        return output;
    }

    public double getValue(boolean[] input) {
        if (input.length != variableBits.length) {
            throw new RuntimeException("Inconsistency");
        }

        byte[] array = getFirstDataByte();

        System.out.println("First");
        dumpRecord(array, array.length, 0);

        for (int i = 0; i < variableBits.length; i++) {
            setBit(array, variableBits[i], input[i] ? 1 : 0);
        }

        System.out.println("Output");
        dumpRecord(array, array.length, 0);

        double output = 0;
        DataInputStream inputStream = null;

        try {
            inputStream = new DataInputStream(new ByteArrayInputStream(array));
            output = inputStream.readDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(inputStream);
        }

        return output;
    }

    private byte[] getFirstDataByte() {
        DataOutputStream stream = null;

        byte[] output = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stream = new DataOutputStream(baos);

            stream.writeDouble(list.get(0).doubleValue());
            stream.flush();

            output = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(stream);
        }
        dumpRecord(output, output.length, 0);

        return output;
    }

    private List<Integer> getFixedMask(byte[] array, int totalRecords) {
        List<Integer> fixedMask = new ArrayList<Integer>();

        int recordSize = array.length / totalRecords;

        System.out.println(String.format("Record Size %d", recordSize * 8));

        dumpRecord(array, recordSize, 0);

        // this.bitRecordSize = recordSize * 8;

        for (int i = 1; i < totalRecords; i++) {

            dumpRecord(array, recordSize, i);

            if (i == 1) { // First
                for (int j = 0; j < recordSize * 8; j++) {
                    if (getBit(array, recordSize, 0, j) == getBit(array, recordSize, i, j)) {
                        fixedMask.add(j);
                    }
                }
            } else {
                for (Iterator<Integer> iterator = fixedMask.iterator(); iterator.hasNext();) {
                    int j = iterator.next();

                    if (getBit(array, recordSize, 0, j) != getBit(array, recordSize, i, j)) {
                        iterator.remove();
                    }
                }
            }


            StringBuilder sb = new StringBuilder();
            for (int z = 0; z < fixedMask.size(); z++) {
                sb.append(fixedMask.get(z)).append(" ");
            }

            if (fixedMask.size() == 0) {
                break;
            }
            // System.out.println(String.format("%2d - %s ", fixedMask.size(), sb.toString()));

        }

        //int result[] = fixedMask.stream().mapToInt(i -> i.intValue()).toArray();

        //System.out.println(String.format("Fixed %3d Total %3d Flex %3d", result.length, recordSize * 8, recordSize * 8 - result.length));

        return fixedMask;
    }


    private int[] invert(List<Integer> fixed, int total) {

        List<Integer> variable = new ArrayList<Integer>();

        for (int i = 0; i < total; i++) {
            if (Collections.binarySearch(fixed, i) < 0) {
                variable.add(i);
            }
        }

        return variable.stream().mapToInt(i -> i.intValue()).toArray();
    }

    private int getBit(byte[] array, int recordSize, int recordIndex, int pos) {


        int shift = pos % 8;
        int offset = pos / 8;

        byte b = array[recordSize * recordIndex + offset];

        return (b >> (7 - shift)) & 1;
    }

    private int getBit(byte[] array, int pos) {
        int shift = pos % 8;
        int offset = pos / 8;

        byte b = array[offset];

        return (b >> (7 - shift)) & 1;
    }

    private void setBit(byte[] array, int pos, int value) {
        int shift = pos % 8;
        int offset = pos / 8;

        if (value == 1) {
            array[offset] |= (1 << (7 - shift));
        }
        else {
            // bits & ~(1 << n)
            array[offset] &= ~(1 << (7 - shift));
        }
    }

    private void dumpRecord(byte array[], int recordSize, int recordIndex) {

        int initial = recordIndex * recordSize;

        StringBuilder sb = new StringBuilder();
        for (int j = initial; j < initial + recordSize; j++) {
            sb.append(String.format("%02X ", array[j]));
        }
        System.out.println(String.format("%d %s" , recordIndex, sb.toString()));
    }

    /*
    private String leftPad(String input, int length) {

        if (length < input.length()) {
            throw new RuntimeException("Fail");
        }

        StringBuffer sb = new StringBuffer(input);

        for (int i = input.length(); i < length; i++) {
            sb.insert(0, " ");
        }

        return sb.toString();
    }
    */
    public int getVariableSize() {
        return this.variableBits.length;
    }

    public int getBitRecordSize() {
        return this.bitRecordSize;
    }


    public static void main(String arg[]){
        List<Double> list = new ArrayList<Double>();
        // list.add(0.1);
        list.add(8280.02);
        list.add(8281.59);
        list.add(8281.90);
        list.add(8311.63);
        list.add(8255.97);

        DoubleAsDouble t = new DoubleAsDouble(list);

        boolean[] b = t.getVariableBits(8281.59);

        System.out.println(String.format("Array: %d %s ", b.length, Arrays.toString(b)));

        System.out.println(String.format("%d %d %.3f", t.getVariableSize(), t.getBitRecordSize(), ((double)t.getVariableSize() / (double)t.getBitRecordSize())));

        System.out.println(t.getValue(b));

    }

}
