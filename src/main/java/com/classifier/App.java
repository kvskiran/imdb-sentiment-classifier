package com.classifier;

import java.io.File;
import java.util.List;

import com.classifier.feature.ChiSquared;
import com.classifier.feature.MutualInformation;
import com.classifier.organize.TermDocumentMatrix;
import com.classifier.process.Core;
import com.classifier.process.Lemmatizer;
import com.classifier.process.Stemmer;
import com.classifier.utilities.Documents;

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

        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        ChiSquared chiFeatureSelector = new ChiSquared();

        List<String> chiFeatures = chiFeatureSelector.select(matrix, 100);

        System.out.println("\nTop 100 chi squared features:");
        for (String feature : chiFeatures) {
            System.out.println(feature);
        }

        MutualInformation miFeatureSelector = new MutualInformation();

        List<String> miFeatures = miFeatureSelector.select(matrix, 100);

        System.out.println("\nTop 100 mutual information features:");
        for (String feature : miFeatures) {
            System.out.println(feature);
        }
    }
}

