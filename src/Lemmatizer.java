import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer {

    protected StanfordCoreNLP pipeline;

    public Lemmatizer() {
        // set properties of the lemmatizer
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        // pipeline to be used for lemmatization
        this.pipeline = new StanfordCoreNLP(props);
    }
    
    // utility method to read contents of a file into a String
    private static String readFile(String path, Charset encoding)
        throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded,encoding);
    }

    // takes the contents of a document as input
    // outputs a lemmatized version of that document
    public List<String> lemmatize(String documentText)
    {
        // used to store lemmatized vocab
        List<String> lemmas = new LinkedList<String>();

        // Create an empty Annotation
        Annotation document = new Annotation(documentText);

        // annotate the text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences and tokens
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

                // add the lemma for each word to lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas;
    }

    // generate a term-document matrix for a corpus of text files
    // each entry has the following form:
    //   word (String) -> (document (String) -> frequency (integer))
    public HashMap<String, HashMap<String, Integer>> buildVocabulary(List<File> corpus) {
        HashMap<String, HashMap<String, Integer>> vocab = new HashMap<String, HashMap<String, Integer>>();
        // iterate through corpus
        for (File doc : corpus) {
            try {
                String contents = readFile(doc.getAbsolutePath(),
                                       Charset.forName("UTF-8"));
                List<String> words = this.lemmatize(contents);

                // build vocab hashmap
                for (String word : words) {
                    if (!vocab.containsKey(word)) {
                        HashMap<String, Integer> val = new HashMap<String, Integer>();
                        val.put(doc.getName(),1);
                        vocab.put(word, val);
                    } else {
                        HashMap<String, Integer> val = vocab.get(word);
                        if (!val.containsKey(doc.getName())) {
                            val.put(doc.getName(),1);
                        } else {
                            val.put(doc.getName(),val.get(doc.getName()) + 1);
                        }
                        vocab.put(word,val);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return vocab;
    }

    public static void main(String[] args) {
        // testing and demonstration
        // ** change the directory structure to match yours **
        String baseDir = "resources/aclImdb/";
        File[] testPos = new File(baseDir + "test/pos").listFiles();
        //  File[] testNeg = new File(baseDir + "test/neg").listFiles();
        //  File[] trainPos = new File(baseDir + "train/pos").listFiles();
        //  File[] trainNeg = new File(baseDir + "train/neg").listFiles();

        Lemmatizer lem = new Lemmatizer();

        // lemmatization demo
        System.out.println("Starting lemmatization demo...");
        String testFile;
        try {
            testFile = readFile(testPos[6].getAbsolutePath(), Charset.forName("UTF-8")); 
            System.out.println("###### INPUT ######");
            System.out.println(testFile);
            List<String> lemmas = lem.lemmatize(testFile);

            System.out.println("###### OUTPUT ######");
            for (String lemma : lemmas) {
                System.out.print(lemma + " ");
            }
        } catch (IOException e) {}
        

        // vocabulary building demo
        System.out.println("\nStarting vocab building demo...");
        List<File> tester = new ArrayList<File>();

        // add a few test docs
        for (int i = 0; i < 3; i++) {
            tester.add(testPos[i]);
        }
        HashMap<String, HashMap<String, Integer>> vocab = 
            lem.buildVocabulary(tester);

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
}
