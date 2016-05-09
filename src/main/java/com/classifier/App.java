package com.classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.classifier.feature.ChiSquared;
import com.classifier.feature.InformationGainRatio;
import com.classifier.feature.MutualInformation;
import com.classifier.organize.TermDocumentMatrix;
import com.classifier.process.Core;
import com.classifier.process.Lemmatizer;
import com.classifier.process.Stemmer;
import com.classifier.utilities.Documents;

public class App {

    public static void main(String args[]) {

        // test files
        String rawDir = "src" + File.separator + "main" + File.separator + "resources" 
            + File.separator + "aclImdb" + File.separator;
        String processedDir = "src" + File.separator + "main" + File.separator + "resources" 
            + File.separator + "processed" + File.separator + "aclImdb" + File.separator;

        File[] trainPos = new File(rawDir + "train" + File.separator + "pos").listFiles();
        File[] trainNeg = new File(rawDir + "train" + File.separator + "neg").listFiles();
        File[] testPos = new File(rawDir + "test" + File.separator + "pos").listFiles();
        File[] testNeg = new File(rawDir + "test" + File.separator + "neg").listFiles();
        Lemmatizer lem = new Lemmatizer();
        Stemmer stem = new Stemmer();

        int counter = 0;
        String stopwordsList = "";
        for (File f : trainPos) {
            Core.process(f, lem, stem, stopwordsList);
            counter++;
            if (counter % 500 == 0)
                System.out.println(counter + "/50000 documents processed");
        }

        for (File f : trainNeg) {
            Core.process(f, lem, stem, stopwordsList);
            counter++;
            if (counter % 500 == 0)
                System.out.println(counter + "/50000 documents processed");
        }

        for (File f : testPos) {
            Core.process(f, lem, stem, stopwordsList);
            counter++;
            if (counter % 500 == 0)
                System.out.println(counter + "/50000 documents processed");
        }

        for (File f : testNeg) {
            Core.process(f, lem, stem, stopwordsList);
            counter++;
            if (counter % 500 == 0)
                System.out.println(counter + "/50000 documents processed");
        }

        System.out.println("\nProcessing complete.");

        System.out.println("\nBuilding vocabulary...");
        // build vocabulary from processed training data
        Documents.buildVocab(processedDir + "train" + File.separator, processedDir + "imdb.vocab");

        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        ChiSquared chiFeatureSelector = new ChiSquared();
        MutualInformation MIFeatureSelector = new MutualInformation();

        List<String> features = new ArrayList<String>();


        int[] size = {10000};
        System.out.println("Writing files in progress");
        System.out.println("_______");
        new File("out" + File.separator + "train").mkdirs();
        new File("out" + File.separator + "test").mkdirs();
        for (int method = 1; method < 6; method++) {
            System.out.print("X");
            for (int featuresNo: size) {
                switch (method) {
                case 0:
                    for (String term : matrix.vocab.keySet()) {
                        features.add(term);
                    }
                    break;
                case 1:
                    features = chiFeatureSelector.select(matrix, featuresNo);
                    break;
                case 2:
                    features = MIFeatureSelector.select(matrix, featuresNo);
                    break;
                case 3:
                    features = InformationGainRatio.select(matrix, featuresNo, 1);
                    break;
                case 4:
                    features = InformationGainRatio.select(matrix, featuresNo, 2);
                    break;
                case 5:
                    features = InformationGainRatio.select(matrix, featuresNo, 3);
                    break;
                default:
                    break;
                }

                // base case
                if (method == 0) {
                    matrix.writeText("out" + File.separator
                                     + "train" + File.separator
                                     + "train_" + method + "_" + "0_" + features.size(), features);
                    matrix.writeTextTesting("out" + File.separator
                                            + "test" + File.separator
                                            + "test_" + method + "_" + "0_" + features.size(), features);
                    break;
                }

                matrix.writeText("out" + File.separator 
                                 + "train" + File.separator 
                                 + "train_" + method + "_" + "0_" + featuresNo, features);
                matrix.writeTextTesting("out" + File.separator 
                                        + "test" + File.separator 
                                        + "test_" + method + "_" + "0_" + featuresNo, features);
            }
        }
    }
}
