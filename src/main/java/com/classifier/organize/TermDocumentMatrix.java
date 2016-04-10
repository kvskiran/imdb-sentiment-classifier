package com.classifier.organize;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.classifier.utilities.Documents;
import com.classifier.utilities.Util;

public class TermDocumentMatrix
{    
    public HashMap<String, Integer> vocab;
    public HashMap<Integer, String> documentLookup;
    public HashMap<String, List<Integer>> dictionary;

    private HashMap<String, Integer> documents;
    private String corpusLocation; 

    public HashMap<Integer, HashMap<String, Integer>> matrix;
 
    public TermDocumentMatrix(String corpusLocationInput) {
        corpusLocation = corpusLocationInput;

        System.out.println("Loading vocab...");
        vocab = loadVocab(corpusLocation + "imdb.vocab");
        System.out.println("Vocab size: " + vocab.size());

        System.out.println("Loading documents...");
        documents = loadDocuments(corpusLocation + "train");
        System.out.println("Document count: " + documents.size());
        
        // invert document map to get document lookup
        documentLookup = new HashMap<Integer, String>();
        for (String docName : documents.keySet()) {
            documentLookup.put(documents.get(docName), docName);
        }
    }
   
    // loads all vocab terms into a hashmap
    private HashMap<String, Integer> loadVocab(String filePath)
    {
        HashMap<String, Integer> vocabList = new HashMap<String, Integer>();
        try {
            List<String> lines = Util.readFileByLine(filePath);
            int vocacID = 0;

            for (String line : lines) {
                if(line.trim() != "") {
                    vocabList.put(line,vocacID);
                    vocacID = vocacID + 1;
                }
            }
        }
        catch(Exception e) {
            System.out.println("Error while parsing vocab file. Message : " + e.getMessage());
            return null;
        }
        return vocabList;
    } 
    
    // load all files and assign a unique sequential id to each document
    private HashMap<String, Integer> loadDocuments(String filePath)
    {
        HashMap<String, Integer> documentList = new HashMap<String, Integer>();

        try {
            int documentID = 0;
          
            File[] files_neg = new File(filePath + File.separator + "neg").listFiles();          
            for (File file : files_neg) {
                if (file.isFile()) {
                    documentList.put("n" + file.getName(), documentID);
                    documentID = documentID + 1;
                }
            }
        
            File[] files_pos = new File(filePath + File.separator + "pos").listFiles();        
            for (File file : files_pos) {
                if (file.isFile()) {
                    documentList.put("p" + file.getName(), documentID);
                    documentID = documentID + 1;
                }
            }
        }
        catch(Exception e) {
            System.out.println("Error while reading document names. Message : " + e.getMessage()); 
            return null;
        }
        return documentList;
    }  
  
    // process each document and add it to the term document matrix
    private void addFiletoMatrix(Integer docID, String filePath)
    {    
        try {
            List<String> tokens = Documents.tokenize(filePath);
            HashMap<String, Integer> terms = new HashMap<String, Integer>();
    
            for(String term : vocab.keySet()) {
                int count = Collections.frequency(tokens, term);

                if (count > 0) {
                    terms.put(term, count);
                
                    //add the docID to the posting list
                    if (dictionary.get(term) == null) { 
                        dictionary.put(term, new ArrayList<Integer>());
                    }
                    dictionary.get(term).add(docID);
                }
            }

            matrix.put(docID,terms);
        }
        catch(Exception e){
            System.out.println("Error adding file to matrix. Message : " + e.toString());
            throw e;
        }
    }

    // build the term document matrix
    public void initMatrix()
    {
        String corpusBase = corpusLocation + "train" + File.separator;
        matrix = new HashMap<Integer, HashMap<String, Integer>>();
        dictionary = new HashMap<String, List<Integer>>();
        
        System.out.println("Beginning initialization of matrix...");
        try {
            // add negative files to matrix
            File[] files_neg = new File(corpusBase + "neg").listFiles();
            for (File file : files_neg) {
                if (file.isFile()) {
                    addFiletoMatrix(documents.get("n" + file.getName()), 
                                    corpusBase + "neg" + File.separator + file.getName());
                    System.out.println(file.getName() + " added to matrix");
                }
            }
          
            // add positive files to matrix
            File[] files_pos = new File(corpusBase + "pos").listFiles();
            for (File file : files_pos) {
                if (file.isFile()) {
                    addFiletoMatrix(documents.get("p" + file.getName()), 
                                    corpusBase + "pos" + File.separator  + file.getName());
                    System.out.println(file.getName() + " added to matrix");
                }
            }
        }
        catch(Exception e) {
            System.out.println("Error while initializing term document matrix. Message : " + e.toString());
        }

        System.out.println("Matrix initialization complete.");
    }
    
    // testing
    public static void main(String[] args) {
        TermDocumentMatrix tdm = new TermDocumentMatrix("src" + File.separator + "main" + File.separator 
                                                        + "resources" + File.separator + "aclImdb" + File.separator);
        tdm.initMatrix();
    }
}
