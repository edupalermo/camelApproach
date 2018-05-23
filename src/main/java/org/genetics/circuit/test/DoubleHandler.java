package org.genetics.circuit.test;

import org.genetics.circuit.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class DoubleHandler {

    private boolean open = true;

    List<Double> list = new ArrayList<Double>();

    public void add(double input) {
        if (!this.open) {
            throw new RuntimeException("Cannot add element!");
        }
        list.add(Double.valueOf(input));
    }

    public void close() {
        this.open = false;

        int[] mask = analyzeAsDouble();
        System.out.println(String.format("As double %3d - %s", mask.length, Arrays.toString(mask)));

        mask = analyzeAsLong();
        System.out.println(String.format("As long   %3d - %s", mask.length, Arrays.toString(mask)));

        mask = analyzeAsString();
        System.out.println(String.format("As string %3d - %s", mask.length, Arrays.toString(mask)));

    }

    private int[] analyzeAsString() {
        int[] fixedMask = null;
        DataOutputStream stream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stream = new DataOutputStream(baos);

            int factor = list.stream().mapToInt(v -> {
                String s = Double.toString(v);
                int pos = 0;
                return (pos = s.indexOf('.')) == -1 ? 0 : s.length() - pos - 1;
            }).max().orElseThrow(NoSuchElementException::new);;


            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(factor);
            int longest = list.stream().mapToInt(v -> df.format(v).length()).max().orElseThrow(NoSuchElementException::new);


            for (Double d : list) {
                //System.out.println(leftPad(df.format(d), longest));
                stream.writeChars(leftPad(df.format(d), longest));
                //stream.writeBytes(leftPad(d.toString(), longest));
                //stream.writeUTF(leftPad(d.toString(), longest));
            }
            stream.flush();

            fixedMask = getFixedMask(baos.toByteArray(), list.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(stream);
        }
        return fixedMask;
    }



    private int[] analyzeAsLong() {
        int[] fixedMask = null;
        DataOutputStream stream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stream = new DataOutputStream(baos);

            double lowest = list.stream().mapToDouble(v -> v).min().orElseThrow(NoSuchElementException::new);
            int factor = list.stream().mapToInt(v -> {
                String s = Double.toString(v);
                int pos = 0;
                return (pos = s.indexOf('.')) == -1 ? 0 : s.length() - pos - 1;
            }).max().orElseThrow(NoSuchElementException::new);;

            for (Double d : list) {
                long newValue = Math.round((d.doubleValue() - lowest) * Math.pow(10, factor));
                stream.writeLong(newValue);
            }
            stream.flush();

            fixedMask = getFixedMask(baos.toByteArray(), list.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(stream);
        }
        return fixedMask;
    }

    private int[] analyzeAsDouble() {
        int[] fixedMask = null;
        DataOutputStream stream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stream = new DataOutputStream(baos);

            for (Double d : list) {
                stream.writeDouble(d.doubleValue());
            }
            stream.flush();

            fixedMask = getFixedMask(baos.toByteArray(), list.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IoUtils.closeQuitely(stream);
        }
        return fixedMask;
    }


    public int[] getFixedMask(byte[] array, int totalRecords) {
        List<Integer> list = new ArrayList<Integer>();

        int recordSize = array.length / totalRecords;

        System.out.println(String.format("Record Size %d", recordSize * 8));

        dumpRecord(array, recordSize, 0);

        for (int i = 1; i < totalRecords; i++) {

            dumpRecord(array, recordSize, i);

            if (i == 1) { // First
                for (int j = 0; j < recordSize * 8; j++) {
                    if (getBit(array, recordSize, 0, j) == getBit(array, recordSize, i, j)) {
                        list.add(j);
                    }
                }
            } else {
                for (Iterator<Integer> iterator = list.iterator(); iterator.hasNext();) {
                    int j = iterator.next();

                    if (getBit(array, recordSize, 0, j) != getBit(array, recordSize, i, j)) {
                        iterator.remove();
                    }
                }
            }


            StringBuilder sb = new StringBuilder();
            for (int z = 0; z < list.size(); z++) {
                sb.append(list.get(z)).append(" ");
            }
            // System.out.println(String.format("%2d - %s ", list.size(), sb.toString()));

        }

        int result[] = list.stream().mapToInt(i -> i.intValue()).toArray();

        System.out.println(String.format("Fixed %3d Total %3d Flex %3d", result.length, recordSize * 8, recordSize * 8 - result.length));

        return result;
    }

    public int getBit(byte[] array, int recordSize, int recordIndex, int pos) {


        int shift = pos % 8;
        int offset = pos / 8;

        byte b = array[recordSize * recordIndex + offset];

        return (b >> (7 - shift)) & 1;
    }

    private void dumpRecord(byte array[], int recordSize, int recordIndex) {

        int initial = recordIndex * recordSize;

        StringBuilder sb = new StringBuilder();
        for (int j = initial; j < initial + recordSize; j++) {
            sb.append(String.format("%02X ", array[j]));
        }
        System.out.println(String.format("%d %s" , recordIndex, sb.toString()));
    }

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

    public static void main(String args[]) {

        DoubleHandler doubleHandler = new DoubleHandler();
        doubleHandler.add(8280.02);
        doubleHandler.add(8281.59);
        doubleHandler.add(8281.90);
        doubleHandler.add(8311.63);
        doubleHandler.add(8255.97);

        doubleHandler.close();
    }

}
