package com.classifier;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.classifier.classify.SupportVectorMachine;
import com.classifier.feature.ChiSquared;
import com.classifier.organize.TermDocumentMatrix;
import com.classifier.process.Core;
import com.classifier.process.Lemmatizer;
import com.classifier.process.Stemmer;
import com.classifier.utilities.Documents;
import com.classifier.utilities.Util;

public class App {
        
    public static void main(String args [ ]) {
        // test files
        String rawDir = "src" + File.separator + "main" + File.separator 
            + "resources" + File.separator + "aclImdb" + File.separator;
        String processedDir = "src" + File.separator + "main" 
            + File.separator + "resources" + File.separator 
            + "processed" + File.separator + "aclImdb" + File.separator;

        File[] trainPos = new File(rawDir + "train" + File.separator + "pos").listFiles();
        File[] trainNeg = new File(rawDir + "train" + File.separator + "neg").listFiles();
        File[] testPos = new File(rawDir + "test" + File.separator + "pos").listFiles();
        File[] testNeg = new File(rawDir + "test" + File.separator + "neg").listFiles();        
        Lemmatizer lem = new Lemmatizer();
        Stemmer stem = new Stemmer();
/*
        int counter = 0;
        for (File f : trainPos) {
            Core.process(f, lem, stem);
            counter++;
            if (counter % 500 == 0) System.out.println(counter + "/50000 documents processed");
        }

        for (File f : trainNeg) {
            Core.process(f, lem, stem);
            counter++;
            if (counter % 500 == 0) System.out.println(counter + "/50000 documents processed");
        }

        for (File f : testPos) {
            Core.process(f, lem, stem);
            counter++;
            if (counter % 500 == 0) System.out.println(counter + "/50000 documents processed");
        }

        for (File f : testNeg) {
            Core.process(f, lem, stem);
            counter++;
            if (counter % 500 == 0) System.out.println(counter + "/50000 documents processed");
        }

        System.out.println("\nProcessing complete.");

        System.out.println("\nBuilding vocabulary...");

        // build vocabulary from processed training data
        Documents.buildVocab(processedDir + "train" + File.separator, 
                             processedDir + "imdb.vocab");
*/

        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        ChiSquared chiFeatureSelector = new ChiSquared();

        List<String> chiFeatures = chiFeatureSelector.select(matrix, 10);

        List<String> infoGainRatioFeatures = chiFeatureSelector.select(matrix, 100);

        matrix.writeText("mtrain_igr_100", infoGainRatioFeatures);
        matrix.writeTextTesting("mtest_igr_100", infoGainRatioFeatures);

        infoGainRatioFeatures = chiFeatureSelector.select(matrix, 500);

        matrix.writeText("mtrain_igr_500", infoGainRatioFeatures);
        matrix.writeTextTesting("mtest_igr_500", infoGainRatioFeatures);
        
        infoGainRatioFeatures = chiFeatureSelector.select(matrix, 1000);

        matrix.writeText("mtrain_igr_1000", infoGainRatioFeatures);
        matrix.writeTextTesting("mtest_igr_1000", infoGainRatioFeatures);
        
        float[][] featureMatrix = matrix.getFeatureMatrix(chiFeatures);
        int[] sentimentVector = matrix.getSentimentVector();

        SupportVectorMachine svm = new SupportVectorMachine(sentimentVector, featureMatrix);

        int incorrect = 0;
        for (File f : new File(processedDir + "test" + File.separator + "pos").listFiles()) {
            String relativePath = processedDir + "test" + File.separator + "pos" 
                + File.separator + f.getName();
            
            float[] testVec = matrix.getFeatureVectorFromDoc(relativePath,chiFeatures);
            
            if (svm.test(testVec) == -1) incorrect++;
        }
        System.out.println(incorrect + "/12500 positive documents misclassified.");

        incorrect = 0;
        for (File f : new File(processedDir + "test" + File.separator + "neg").listFiles()) {
            String relativePath = processedDir + "test" + File.separator + "neg" 
                + File.separator + f.getName();
            
            float[] testVec = matrix.getFeatureVectorFromDoc(relativePath,chiFeatures);
            
            if (svm.test(testVec) == 1) incorrect++;
        }
        System.out.println(incorrect + "/12500 negative documents misclassified.");
    }
}

