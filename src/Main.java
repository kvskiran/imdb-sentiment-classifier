import java.io.File;
import java.io.IOException;
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

    public static void main(String args [ ]) {
        // test files
        String baseDir = "resources/aclImdb/";
        File[] testPos = new File(baseDir + "test/pos").listFiles();
        String testStr = "";

        try {
            testStr = Util.readFile(testPos[6].getAbsolutePath(), Charset.forName("UTF-8")).toLowerCase();
        } catch (IOException e) {}

        // init
        Lemmatizer lem = new Lemmatizer();
        Stemmer stem = new Stemmer();

        // print raw string
        System.out.println("\n###### RAW STR ######");
        System.out.println(testStr);

        // lemmatize
        List<String> lemmas = lem.lemmatize(testStr);

        // print lemmatized string
        System.out.println("\n###### LEMMATIZED ######");
        System.out.println(Util.concatList(lemmas));

        // remove stopwords and stem
        List<String> lemmasWithoutStopwords = StopwordRemover.removeStopwords(lemmas);
        List<String> stems = stem.stemStr(Util.removeSpecialCharacters(Util.concatList(lemmasWithoutStopwords)));

        // print stemmed string
        System.out.println("\n###### STEMMED ######");
        System.out.println(Util.concatList(stems));

        // demoVocabBuild(lem, Arrays.asList(testPos));
    }
}
