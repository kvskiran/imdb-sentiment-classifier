package com.classifier.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

class termDocumentMatrix
{
  protected static HashMap<String, Integer> _vocab;
  protected static HashMap<String, Integer> _documents;
  
  protected static HashMap<Integer, HashMap<String, Integer>> _termDocumentMatrix;
  protected static HashMap<String, List<Integer>> _dictionary;
  
  
  public static void main(String[] args)
  {
    System.out.println("Loading vocab...");
    _vocab = loadVocab("src" + File.separator + "main" + File.separator 
                       + "resources" + File.separator + "aclImdb" 
                       + File.separator + "imdb.vocab");
    if(_vocab == null)
    {
      System.out.println("Failed to load vocab.");
    }
    else
    {
      System.out.println("Finished loading vocab. Count : " + _vocab.size());
    }
    
    System.out.println("Loading document names...");
    _documents = loadDocuments("src" + File.separator + "main" 
                               + File.separator + "resources" 
                               + File.separator + "aclImdb" 
                               + File.separator + "train");
    if(_vocab == null)
    {
      System.out.println("Failed to load document names.");
    }
    else
    {
      System.out.println("Finished loading documents. Count : " + _documents.size());
    }
        System.out.println("Initializing term document matrix. This may take up to several minutes...");
        initMatrix("src" + File.separator + "main" + File.separator 
                   + "resources" + File.separator + "aclImdb" 
                   + File.separator + "train");
        System.out.println("Finished initializing matrix.");
            System.out.println("Usage -c <docID 1> <docID 2>");
            System.out.println("Usage -d <term>");
        String command = "";
        Scanner scan = new Scanner(System.in);
        while(!command.equals("quit"))
        {
          try
          {
          command = scan.nextLine();
          String[] cmds = command.split(" ");
          if(cmds[0].equals("-c"))
          {
            System.out.println(cosineSim(_documents.get(cmds[1]),_documents.get(cmds[2])));
          }
          else if(cmds[0].equals("-d"))
            System.out.println(_dictionary.get(cmds[1]));
          else
          {
            System.out.println("Usage -c <docID 1> <docID 2>");
            System.out.println("Usage -d <term>");
          }
          }
          catch(Exception e)
          {
            System.out.println("Usage -c <docID 1> <docID 2>");
            System.out.println("Usage -d <term>");
          }
        }
        scan.close();
        
    return;
  }
  
  
  //loads all vocab terms into a hashmap
  protected static HashMap<String, Integer> loadVocab(String filePath)
  {
      HashMap<String, Integer> vocabList = new HashMap<String, Integer>();
    try
    {
      int vocacID = 0;
      FileReader vocabReader = new FileReader(filePath);

      BufferedReader bufferReader = new BufferedReader(vocabReader);
      String line;

      while ((line = bufferReader.readLine()) != null)   
      {
        
        if(line.trim() != "")
        {
        vocabList.put(line,vocacID);
        vocacID = vocacID + 1;
        }
      }
      bufferReader.close();

     }
     catch(Exception e)
     {
          System.out.println("Error while parsing vocab file. Message : " + e.getMessage());
          return null;
     }
     return vocabList;
 } 
    
  //load all files and assign a unique sequential id to each document
  protected static HashMap<String, Integer> loadDocuments(String filePath)
  {
      HashMap<String, Integer> documentList = new HashMap<String, Integer>();

      try
      {
        int documentID = 0;
        
        File[] files_neg = new File(filePath + File.separator + "neg")
            .listFiles();
        
        for (File file : files_neg) 
        {
          if (file.isFile()) 
          {
            documentList.put("n" + file.getName(), documentID);
            documentID = documentID + 1;
          }
        }
        
        File[] files_pos = new File(filePath + File.separator + "pos")
            .listFiles();
        
        for (File file : files_pos) 
        {
          if (file.isFile()) 
          {
            documentList.put("p" + file.getName(), documentID);
            documentID = documentID + 1;
          }
        }
        
      }
      catch(Exception e)
      {
        System.out.println("Error while reading document names. Message : " + e.getMessage()); 
        return null;
      }
      return documentList;
  }
  
  //build the term document matrix
  protected static HashMap<Integer, HashMap<String, Integer>> initMatrix(String filePath)
  {
    _termDocumentMatrix = new HashMap<Integer, HashMap<String, Integer>>();
    _dictionary = new HashMap<String, List<Integer>>();
    try
    {
        File[] files_neg = new File(filePath + File.separator + "neg").listFiles();
        for (File file : files_neg) 
        {
          if (file.isFile()) 
          {
            addFiletoMatrix(_documents.get("n" + file.getName()), 
                            filePath + File.separator + "neg" 
                            + File.separator + file.getName());
          }
        }
        File[] files_pos = new File(filePath + File.separator + "pos")
            .listFiles();

        for (File file : files_pos) 
        {
          if (file.isFile()) 
          {
            addFiletoMatrix(_documents.get("p" + file.getName()), 
                            filePath + File.separator + "pos" 
                            + File.separator  + file.getName());
          }

        }
      
    }
    catch(Exception e)
    {
      System.out.println("Error while initializing term document matrix. Message : " + e.getMessage());
      return null;
    }
    return _termDocumentMatrix;
  }
  
  //process each document and add it to the term document matrix
  protected static void addFiletoMatrix(Integer docID, String filePath)
  {    
    try
    {
      
    List<String> tokens = tokenizeDocument(filePath);
    HashMap<String, Integer> terms = new HashMap<String, Integer>();
    
    for(String term : _vocab.keySet())
    {
      int count = Collections.frequency(tokens, term);
      
      if(count > 0)
      {
       terms.put(term, count);
       
       //add the docID to the posting list
       if (_dictionary.get(term) == null) { 
         _dictionary.put(term, new ArrayList<Integer>());
       }
       _dictionary.get(term).add(docID);
      }
    }

    _termDocumentMatrix.put(docID,terms);

    
    }
    catch(Exception e)
    {
      System.out.println("Error adding file to matrix. Message : " + e.getMessage());
      throw e;
    }
  }
  //tokenize each document into terms
  protected static List<String> tokenizeDocument(String filePath)
  {
    List<String> tokenized = new ArrayList<String>();
    
              try{

          FileReader inputFile = new FileReader(filePath);

          BufferedReader bufferReader = new BufferedReader(inputFile);

          String line;

          while ((line = bufferReader.readLine()) != null)   
          {
            String[]tokens = line.split(",|\\.|;| |:");
            for (String token : tokens)
            {
              if(token.trim().length() > 0)
                tokenized.add(token);
            }
          }
          bufferReader.close();
          
       }catch(Exception e){
          System.out.println("Error while parsing document :" + e.getMessage());
       }
       return tokenized;
  }
  //compute the cosine simularity between two vectors 
  public static double cosineSim(Integer DocID1, Integer DocID2)
  {
    HashMap<String,Integer> Doc1 = _termDocumentMatrix.get(DocID1);
    HashMap<String,Integer> Doc2 = _termDocumentMatrix.get(DocID2);
    
    double dotProduct = 0;
    double normalDoc1 = 0;
    double normalDoc2 = 0;
    
    for(String word : _vocab.keySet())
    {

      Integer weight1 = Doc1.get(word);
      if(weight1 == null)
        weight1 = 0;
     
      Integer weight2 = Doc2.get(word);
      if(weight2 == null)
        weight2 = 0;

      dotProduct = dotProduct + (weight1 * weight2);

      normalDoc1 = normalDoc1 + (weight1 * weight1);

      normalDoc2 = normalDoc2 + (weight2 * weight2);
      
    }
    
    normalDoc1 = Math.sqrt(normalDoc1);
    
    normalDoc2 = Math.sqrt(normalDoc2);
    
    return (dotProduct) / (normalDoc1 * normalDoc2);
    
    
  }
    
}
