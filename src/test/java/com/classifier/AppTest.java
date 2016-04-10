package com.classifier;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.classifier.feature.ChiSquared;
import com.classifier.feature.MutualInformation;
import com.classifier.organize.TermDocumentMatrix;
import com.classifier.process.Core;
import com.classifier.process.Lemmatizer;
import com.classifier.process.Stemmer;
import com.classifier.utilities.Documents;



public class AppTest {

    @Before 
    public void init() {
        String rawDir = "src" + File.separator + "test" + File.separator 
            + "resources" + File.separator + "aclImdb" + File.separator;
        String processedDir = "src" + File.separator + "test" 
            + File.separator + "resources" + File.separator 
            + "processed" + File.separator + "aclImdb" + File.separator;
        
        
        File[] trainPos = new File(rawDir + "train" + File.separator + "pos").listFiles();
        File[] trainNeg = new File(rawDir + "train" + File.separator + "neg").listFiles();

        Lemmatizer lem = new Lemmatizer();
        Stemmer stem = new Stemmer();
        
        for (File trainPosFile : trainPos) {
            Core.process(trainPosFile, lem, stem);
            System.out.println(trainPosFile.getAbsolutePath() + " processed.");
        }
        
        for (File trainNegFile : trainNeg) {
            Core.process(trainNegFile, lem, stem);
            System.out.println(trainNegFile.getAbsolutePath() + " processed.");
        }
        
        System.out.println("Processing complete.");
        
        System.out.println("Building vocabulary...");
        
        // build vocabulary from processed training dat
        Documents.buildVocab(processedDir + "train" + File.separator, 
                             processedDir + "imdb.vocab");    

        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        for (Integer doc : matrix.matrix.keySet()) {
            System.out.println(doc + ":");
            for (String term : matrix.matrix.get(doc).keySet()) {
                System.out.println(term + " -> " + matrix.matrix.get(doc).get(term));
            }
        }

        System.out.println("=======mutual information=======");

        MutualInformation mutualInformationSelector = new MutualInformation();

        List<String> posFeaturesMi = mutualInformationSelector.select(matrix, 10);

        System.out.println("\nPositive features:");
        for (String feature : posFeaturesMi) {
            System.out.println(feature);
        }

        List<String> negFeaturesMi = mutualInformationSelector.select(matrix, 10);

        System.out.println("\nNegative features:");
        for (String feature : negFeaturesMi) {
            System.out.println(feature);
        }

        System.out.println("\n=======chi squared=======");

        ChiSquared chiSquaredSelector = new ChiSquared();

        List<String> posFeaturesChi = chiSquaredSelector.select(matrix, 10);

        System.out.println("\nPositive features:");
        for (String feature : posFeaturesChi) {
            System.out.println(feature);
        }

        List<String> negFeaturesChi = chiSquaredSelector.select(matrix, 10);

        System.out.println("\nNegative features:");
        for (String feature : negFeaturesChi) {
            System.out.println(feature);
        }

    }

    @Test
    public void dummyTest() {
        Assert.assertEquals(1,1);
    }
}
