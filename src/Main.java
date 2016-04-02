import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import processing.Lemmatizer;
import processing.Stemmer;
import processing.StopwordRemover;

import utilities.Util;

public class Main {

    public static void demoVocabBuild(Lemmatizer lem, List<File> files) {
        System.out.println("\nStarting vocab building demo...");

        // build vocab
        HashMap<String, HashMap<String, Integer>> vocab = 
            lem.buildVocabulary(files);

        // print the vocab
        for (String word : vocab.keySet()) {
            HashMap<String, Integer> docs = vocab.get(word);
            String docList = "";            

            for (String doc : docs.keySet()) {
                docList += "(" + doc + ", " + docs.get(doc) + ") ";
            }

            System.out.println(word + " -> " + docList);                   
        }
    }

    public static void process(File file, Lemmatizer lem, Stemmer stem)  
    {
        String fileStr = "";
        File processedFile = new File(file.getAbsolutePath().replace("resources","resources/processed"));

        try {
            fileStr = Util.readFile(file.getAbsolutePath(), Charset.forName("UTF-8")).toLowerCase();
            processedFile.getParentFile().mkdirs();
            processedFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
                
        // lemmatize
        List<String> lemmas = lem.lemmatize(fileStr);

        // remove stopwords and stem
        List<String> lemmasWithoutStopwords = StopwordRemover.removeStopwords(lemmas);
        List<String> stems = stem.stemStr(Util.removeSpecialCharacters(Util.concatList(lemmasWithoutStopwords)));

        // write processed str to new file
        try {
            PrintStream ps = new PrintStream(processedFile.getAbsolutePath());
            ps.println(Util.concatList(stems));
        }
        catch (Exception e) { }
    }
        
    public static void main(String args [ ]) {
        // test files
        String baseDir = "resources/aclImdb/";
        File[] trainPos = new File(baseDir + "train/pos").listFiles();
        File[] trainNeg = new File(baseDir + "train/neg").listFiles();
        File[] testPos = new File(baseDir + "train/pos").listFiles();
        File[] testNeg = new File(baseDir + "train/neg").listFiles();        

        Lemmatizer lem = new Lemmatizer();
        Stemmer stem = new Stemmer();

        for (File f : trainPos) {
            process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        for (File f : trainNeg) {
            process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        for (File f : testPos) {
            process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }

        for (File f : testNeg) {
            process(f, lem, stem);
            System.out.println(f.getAbsolutePath() + " processed.");
        }
    }
}