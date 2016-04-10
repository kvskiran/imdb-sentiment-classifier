package com.classifier;

import java.io.File;
import java.util.List;

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

        for (File f : trainPos) {
            Core.process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        for (File f : trainNeg) {
            Core.process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        for (File f : testPos) {
            Core.process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        for (File f : testNeg) {
            Core.process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        System.out.println("Processing complete.");

        System.out.println("Building vocabulary...");

        // build vocabulary from processed training dat
        Documents.buildVocab(processedDir + "train" + File.separator, 
                             processedDir + "imdb.vocab");

        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        List<String> posFeatures = MutualInformation.select(matrix, 100, true);
        List<String> negFeatures = MutualInformation.select(matrix, 100, false);

        System.out.println("\nTop 100 positive features:");
        for (String feature : posFeatures) {
            System.out.println(feature);
        }

        System.out.println("\nTop 100 negative features:");
        for (String feature : negFeatures) {
            System.out.println(feature);
        }
    }
}
