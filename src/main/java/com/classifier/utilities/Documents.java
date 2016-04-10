package com.classifier.utilities;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class Documents {

    // tokenize each document into terms
    public static List<String> tokenize(String filePath)
    {
        List<String> tokenized = new ArrayList<String>();
        try {                  
            List<String> lines = Util.readFileByLine(filePath);

            for (String line : lines) {
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    tokenized.add(token);
                }
            }
        } 
        catch(Exception e) {
            System.out.println("Error while parsing document :" + e.getMessage());
        }
        return tokenized;
    }

    // build vocabulary from a corpus of documents
    public static void buildVocab(String corpusLocation, 
                                  String targetLocation) {
        File[] neg = new File(corpusLocation + "neg").listFiles();
        File[] pos = new File(corpusLocation + "pos").listFiles();
        
        List<String> vocab = new ArrayList<String>();
        for (File file : Util.fileArrayConcat(pos, neg)) {
            for (String token : tokenize(file.getAbsolutePath())) {
                if (!vocab.contains(token)) {
                    vocab.add(token);
                }
            }
        }

        Collections.sort(vocab);
        
        try {
            new File(targetLocation).createNewFile();
        
            PrintStream ps = new PrintStream(targetLocation);
            for (String term : vocab) {
                ps.println(term);
            }
            ps.close();
        }
        catch (Exception e) { 
            System.out.println("Invalid target location");
        }
    }

    //compute the cosine simalarity between two vectors
    public static double cosineSim(HashMap<String,Integer> vec1, HashMap<String,Integer> vec2)
    {
        double dotProduct = 0;
        double normalVec1 = 0;
        double normalVec2 = 0;
        
        for(String word : vec1.keySet()) {
            
            Integer weight1 = vec1.get(word);
            if(weight1 == null)
                weight1 = 0;
     
            Integer weight2 = vec2.get(word);
            if(weight2 == null)
                weight2 = 0;

            dotProduct = dotProduct + (weight1 * weight2);
          
            normalVec1 = normalVec1 + (weight1 * weight1);
          
            normalVec2 = normalVec2 + (weight2 * weight2);      
        }
    
        normalVec1 = Math.sqrt(normalVec1);
    
        normalVec2 = Math.sqrt(normalVec2);
    
        return (dotProduct) / (normalVec1 * normalVec2); 
    }
}
