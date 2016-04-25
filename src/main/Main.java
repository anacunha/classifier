package main;

import classifier.Classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;

            while ((input = br.readLine()) != null) {
                String[] line = input.split(" ");
                if (line[0].equals("nbtrain")) {

                    if (line.length != 3)
                        System.out.println("nbtrain <training-directory> <model-file>");
                    else
                        Classifier.train(line[1], line[2]);
                }
                else if (line[0].equals("nbtest")) {
                    if (line.length != 4)
                        System.out.println("nbtest <model-file> <test-directory> <predictions-file>");
//                    else
//                        Classifier.test(line[1], line[2], line[3]);
                }
                else {
                    System.out.println("nbtrain <training-directory> <model-file>");
                    System.out.println("nbtest <model-file> <test-directory> <predictions-file>");
                }
            }

        }catch(IOException io){
            io.printStackTrace();
        }
    }
}
