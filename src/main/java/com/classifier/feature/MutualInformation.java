package com.classifier.feature;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.classifier.organize.TermDocumentMatrix;
import com.classifier.utilities.ValueComparator;

public class MutualInformation {

    // int amt = amount of features to select
    // boolean sentiment = true means postiive, otherwise negative
    public static List<String> select(TermDocumentMatrix matrix, int amt, boolean sentiment) {
        HashMap<String, Double> featureScores 
            = new HashMap<String, Double>();
        List<String> features = new LinkedList<String>();
        String identifier;

        if (sentiment) {
            identifier = "^p.*";
        } else identifier = "^n.*";

        // total count of documents
        double n = Double.parseDouble(matrix.documentLookup.keySet().size() + "");

        double n1 = 0;
        double n0 = 0;
        
        for (String term : matrix.vocab.keySet()) {
            List<Integer> containingDocs = matrix.dictionary.get(term);

            // in docs that contain the term
            double n11 = 0;

            // not-in docs that contain the term
            double n10 = 0;
            
            // in docs that do not contain the term
            double n01 = 0;

            // not-in docs that do not contain the term
            double n00 = 0;

            for (Integer doc : containingDocs) {
                if (matrix.documentLookup.get(doc).matches(identifier)) {
                    n11++;
                } else {
                    n10++;
                }
            }

            n01 = n/2 - n11;
            n00 = n/2 - n10;

            n1 = n10 + n11;
            n0 = n01 + n00;

            Double score 
                = ((n11+1/n+1) * Math.log(((n*n11)+1)/((n1*n1)+1)))
                + ((n01+1/n+1) * Math.log(((n*n01)+1)/((n0*n1)+1)))
                + ((n10+1/n+1) * Math.log(((n*n10)+1)/((n1*n0)+1)))
                + ((n00+1/n+1) * Math.log(((n*n00)+1)/((n0*n0)+1)));
            
      /*
            System.out.println("\nTerm: " + term);
            System.out.println(" n11: " + n11);
            System.out.println(" n10: " + n10);
            System.out.println(" n01: " + n01);
            System.out.println(" n00: " + n00);
            System.out.println("Score: " + score);
      */

            featureScores.put(term,score);
        }
        
        // sort the map
        Comparator<String> comparator = new ValueComparator(featureScores);
        TreeMap<String, Double> featureScoresSorted
            = new TreeMap<String,Double>(comparator);
        featureScoresSorted.putAll(featureScores);

        int i = 0;
        for(Map.Entry<String,Double> entry : featureScoresSorted.entrySet()) {

            features.add(entry.getKey());
            i++;
            if (i==amt) break;
        }

        return features;
    }

    public static void main(String[] args) {
        String processedDir = "src" + File.separator + "test" 
            + File.separator + "resources" + File.separator + "processed" 
            + File.separator + "aclImdb" + File.separator;
        TermDocumentMatrix matrix = new TermDocumentMatrix(processedDir);
        matrix.initMatrix();

        select(matrix, 10, true);
    }
}
