package org.genetics.circuit.test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

    public static void main(String args[]) {


        Analyzer analyzer = new Analyzer();

        analyzer.addSolution("17/05/2018 09:37", 8280.02);
        analyzer.addSolution("17/05/2018 09:36", 8281.59);
        analyzer.addSolution("17/05/2018 09:35", 8281.90);
        analyzer.addSolution("17/05/2018 09:13", 8311.63);
        analyzer.addSolution("17/05/2018 08:55", 8255.97);

        analyzer.comprare();

        //System.out.println("Discarded: " + analyzer.getMask().length);

    }


    public static class Analyzer {

        private ByteArrayOutputStream baosInput = new ByteArrayOutputStream();
        private DataOutputStream streamInput = new DataOutputStream(baosInput);

        private ByteArrayOutputStream baosOutput = new ByteArrayOutputStream();
        private DataOutputStream streamOutput = new DataOutputStream(baosOutput);

        private int count = 0;

        private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        public void addSolution(String date, double value) {
            try {
                streamInput.writeLong(sdf.parse(date).getTime());
                streamInput.flush();
                streamOutput.writeDouble(value);
                streamOutput.flush();
                this.count++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public void comprare() {
            System.out.println("Input size; " + this.getMask(this.baosInput).length);
            System.out.println("========================================================");
            System.out.println("Output size; " + this.getMask(this.baosOutput).length);
        }

        public int[] getMask(ByteArrayOutputStream baos) {
            List<Integer> list = new ArrayList<Integer>();

            byte[] array = baos.toByteArray();

            int recordSize = array.length / count;

            dumpRecord(array, recordSize, 0);

            for (int i = 1; i < count; i++) {

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
                System.out.println(String.format("%2d - %s ", list.size(), sb.toString()));

            }

            return list.stream().mapToInt(i -> i.intValue()).toArray();
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

    }

}
