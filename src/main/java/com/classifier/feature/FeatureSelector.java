package com.classifier.feature;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.classifier.organize.TermDocumentMatrix;
import com.classifier.utilities.ValueComparator;

public abstract class FeatureSelector {

    // int amt = amount of features to select
    public List<String> select(TermDocumentMatrix matrix, int amt) {
        HashMap<String, Double> featureScores 
            = new HashMap<String, Double>();
        List<String> features = new LinkedList<String>();

        // use the positive class as the 'in' class
        String identifier = "^n.*";

        // total count of documents
        double n = Double.parseDouble(matrix.documentLookup.keySet().size() + "");

        double n1 = 0;
        double n0 = 0;

        // in docs that contain the term
        double n11 = 0;
        
        // not-in docs that contain the term
        double n10 = 0;
        
        // in docs that do not contain the term
        double n01 = 0;
        
        // not-in docs that do not contain the term
        double n00 = 0;
        
        for (String term : matrix.vocab.keySet()) {
            List<Integer> containingDocs = matrix.dictionary.get(term);

            n11 = n10 = n01 = n00 = 0;
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

            Double score = calculateScore(n, n1, n0, n11, n10, n01, n00);

            // print out scores -- for testing
            // System.out.println("\nTerm: " + term 
                               // + "\n   n: " + n
                               // + "\n   n1: " + n1
                               // + "\n   n0: " + n0
                               // + "\n   n11: " + n11
                               // + "\n   n00: " + n00
                               // + "\n   n10: " + n10
                               // + "\n   n01: " + n01
                               // + "\nScore: " + score);

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
    
    // implement this method for different feature selectors
    protected abstract double calculateScore(double n,
                                             double n1, 
                                             double n0,
                                             double n11,
                                             double n10,
                                             double n01,
                                             double n00);
}
