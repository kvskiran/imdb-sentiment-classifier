package com.classifier.processing;

import java.util.ArrayList;
import java.util.List;

import com.classifier.utilities.Util;


public class StopwordRemover {    
    public static List<String> removeStopwords(List<String> input) {
        try {
            List<String> stopwords = Util.readFileByLine("resources/aclImdb/stopwords.txt");
            List<String> output = new ArrayList<String>();
            for (String word : input) {
                if (!stopwords.contains(word)) {
                    output.add(word);
                }
            }
            return output;        
        } catch (Exception e) {
            return null;
        }
    }
}
