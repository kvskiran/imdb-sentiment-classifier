package com.classifier.organize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.classifier.utilities.Documents;
import com.classifier.utilities.Util;

public class TermDocumentMatrix {
    public HashMap<String, Integer> vocab;
    public HashMap<Integer, String> documentLookup;
    public HashMap<String, List<Integer>> dictionary;
    private HashMap<String, Integer> documents;
    private String corpusLocation;
    public HashMap<Integer, HashMap<String, Double>> matrix;
    public HashMap<String, HashMap<Integer, int[]>> dictionaryMatrix;
	
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
    private HashMap<String, Integer> loadVocab(String filePath) {
        HashMap<String, Integer> vocabList = new HashMap<String, Integer>();
        try {
            List<String> lines = Util.readFileByLine(filePath);
            int vocacID = 0;
            for (String line : lines) {
                if (line.trim() != "") {
                    vocabList.put(line, vocacID);
                    vocacID = vocacID + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error while parsing vocab file. Message : " + e.getMessage());
            return null;
        }
        return vocabList;
    }
	
    // load all files and assign a unique sequential id to each document
    private HashMap<String, Integer> loadDocuments(String filePath) {
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
        } catch (Exception e) {
            System.out.println("Error while reading document names. Message : " + e.getMessage());
            return null;
        }
        return documentList;
    }
	
    /*public float[] getFeatureVectorFromDoc(String filePath, List<String> features) {
        List<String> tokens = Documents.tokenize(filePath);
        float[] featureVec = new float[features.size()];
        for (String token : tokens) {
            int count = Collections.frequency(tokens, token);
            if (features.contains(token)) {
                featureVec[features.indexOf(token)] = count;
            }
        }
        return featureVec;
    }*/
	
    // process each document and add it to the term document matrix
    private void addFiletoMatrix(Integer docID, String filePath) {
        try {
            List<String> tokens = Documents.tokenize(filePath);
            HashMap<String, Double> terms = new HashMap<String, Double>();
            for (String term : vocab.keySet()) {
                int count = Collections.frequency(tokens, term);
                
                if (count > 0) {
                    terms.put(term, (double) count);
                    // add the docID to the posting list
                    if (dictionary.get(term) == null) {
                        dictionary.put(term, new ArrayList<Integer>());
                    }
                    dictionary.get(term).add(docID);

                }
                if (count >= 0) {
                    if (dictionaryMatrix.get(term) == null) {
                        dictionaryMatrix.put(term, new HashMap<Integer, int[]>());
                    }
                    if (dictionaryMatrix.get(term).get(count) == null) {
                        int[] z = { 0, 0 };
                        dictionaryMatrix.get(term).put(count, z);
                    }
                    int[] inc = dictionaryMatrix.get(term).get(count);
                    if (filePath.matches("^src/main/resources/processed/aclImdb/train/p.*")) {
                        inc[0]++;
                    } else {
                        inc[1]++;
                    }

                    dictionaryMatrix.get(term).replace(count, inc);
                }
            }
            matrix.put(docID, terms);
        } catch (Exception e) {
            System.out.println("Error adding file to matrix. Message : " + e.toString());
            throw e;
        }
    }
	
    // convert matrix to a svm-able data structure
    // given a list of features
    /*public float[][] getFeatureMatrix(List<String> features) {
        float[][] featureMatrix = new float[matrix.entrySet().size()][features.size()];
        for (Map.Entry<Integer, HashMap<String, Integer>> document : matrix.entrySet()) {
            float[] docVec = new float[features.size()];
            int termCounter = 0;
            for (Map.Entry<String, Integer> term : document.getValue().entrySet()) {
                if (features.contains(term.getKey())) {
                    docVec[termCounter] = term.getValue();
                    termCounter++;
                }
            }
            featureMatrix[document.getKey()] = docVec;
        }
        return featureMatrix;
    }*/
	
    public void writeText(String fileName, List<String> features) {
        FileOutputStream outputStream;
        OutputStreamWriter outputStreamWriter;
        BufferedWriter bufferedWriter;
        try {
            outputStream = new FileOutputStream(fileName);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            for (Map.Entry<Integer, HashMap<String, Double>> document : matrix.entrySet()) {
                if (documentLookup.get(document.getKey()).matches("^p.*")) {
                    bufferedWriter.write("+1 ");
                } else {
                    bufferedWriter.write("-1 ");
                }
                for (int i = 0; i < features.size(); i++) {
                    if (document.getValue().containsKey(features.get(i)))
                        bufferedWriter.write((i + 1) + ":" + document.getValue().get(features.get(i)) + " ");
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("\nError while writing term document matrix files. Message : " + e.toString());
        }
    }

    public void writeTextTesting(String fileName, List<String> features) {
        FileOutputStream outputStream;
        OutputStreamWriter outputStreamWriter;
        BufferedWriter bufferedWriter;
		String processedDir = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "processed"
				+ File.separator + "aclImdb" + File.separator;
        try {
            outputStream = new FileOutputStream(fileName);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            for (File f : new File(processedDir + "test" + File.separator + "pos").listFiles()) {
                String relativePath = processedDir + "test" + File.separator + "pos" + File.separator + f.getName();
                bufferedWriter.write("+1 ");
                List<String> tokens = Documents.tokenize(relativePath);
                for (int i = 0; i < features.size(); i++) {
                    int count = Collections.frequency(tokens, features.get(i));                    
                    double idf = Math.log(documents.keySet().size()/dictionary.get(features.get(i)).size());
                    if (count > 0)
                        bufferedWriter.write((i + 1) + ":" + (count*idf) + " ");
                }
                bufferedWriter.newLine();
            }
            for (File f : new File(processedDir + "test" + File.separator + "neg").listFiles()) {
                String relativePath = processedDir + "test" + File.separator + "neg" + File.separator + f.getName();
                bufferedWriter.write("-1 ");
                List<String> tokens = Documents.tokenize(relativePath);
                for (int i = 0; i < features.size(); i++) {
                    int count = Collections.frequency(tokens, features.get(i));
                    double idf = Math.log(documents.keySet().size()/dictionary.get(features.get(i)).size());
                    if (count > 0)
                        bufferedWriter.write((i + 1) + ":" + (count*idf) + " ");
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("\nError while writing term document matrix test files. Message : " + e.toString());
        }
    }
	
    public int[] getSentimentVector() {
        int[] sentimentVec = new int[documentLookup.size()];
        for (int i = 0; i < documentLookup.size(); i++) {
            if (documentLookup.get(i).matches("^p.*")) {
                sentimentVec[i] = 1;
            } else
                sentimentVec[i] = -1;
        }
        return sentimentVec;
    }
	
    // build the term document matrix
    public void initMatrix() {
        String corpusBase = corpusLocation + "train" + File.separator;
        matrix = new HashMap<Integer, HashMap<String, Double>>();
        dictionaryMatrix = new HashMap<String, HashMap<Integer, int[]>>();
        dictionary = new HashMap<String, List<Integer>>();
        System.out.println("Beginning initialization of matrix...");
        try {
            // add negative files to matrix
            File[] files_neg = new File(corpusBase + "neg").listFiles();
            // for tracking
            System.out.println("_________________________" + "_________________________");
            int counter = 0;
            for (File file : files_neg) {
                if (file.isFile()) {
                    addFiletoMatrix(documents.get("n" + file.getName()),
                                    corpusBase + "neg" + File.separator + file.getName());
                    counter++;
                    if (counter % 500 == 0)
                        System.out.print("X");
                }
            }
            // add positive files to matrix
            File[] files_pos = new File(corpusBase + "pos").listFiles();
            for (File file : files_pos) {
                if (file.isFile()) {
                    addFiletoMatrix(documents.get("p" + file.getName()),
                                    corpusBase + "pos" + File.separator + file.getName());
                    counter++;
                    if (counter % 500 == 0)
                        System.out.print("X");
                }
            }
            for(Map.Entry<Integer, HashMap<String, Double>> entry: matrix.entrySet()){
            	for(Entry<String, Double> term: entry.getValue().entrySet()){
                    double idf = Math.log(documents.keySet().size()/dictionary.get(term.getKey()).size());
                    matrix.get(entry.getKey()).replace(term.getKey(), term.getValue()*idf);
            	}
            }
            
        } catch (Exception e) {
            System.out.println("\nError while initializing term document matrix. Message : " + e.toString());
        }
        System.out.println("\nMatrix initialization complete.");
    }
	
    // testing
    public static void main(String[] args) {
        TermDocumentMatrix tdm = new TermDocumentMatrix("src" + File.separator + "test" + File.separator + "resources"
                                                        + File.separator + "processed" + File.separator + "aclImdb" + File.separator);
        tdm.initMatrix();
        List<String> features = new ArrayList<String>();
        features.add("good");
        features.add("great");
        /*float[][] featureMatrix = tdm.getFeatureMatrix(features);
        int[] sentimentVec = tdm.getSentimentVector();
        for (int i = 0; i < featureMatrix.length; i++) {
            for (int j = 0; j < featureMatrix[i].length; j++) {
                System.out.print(featureMatrix[i][j] + " ");
            }
            System.out.print(sentimentVec[i]);
            System.out.print("\n");
        }*/
    }
}
