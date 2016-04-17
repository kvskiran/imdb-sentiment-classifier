package com.classifier.process;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.classifier.utilities.Util;

public final class Core {
    public static void process(File file, Lemmatizer lem, Stemmer stem)  
    {
        String fileStr = "";
        File processedFile = new File(file.getAbsolutePath()
                                      .replace("resources","resources" 
                                               + File.separator + "processed"));
        
        try {
            fileStr = Util.readFile(file.getAbsolutePath()).toLowerCase();
            processedFile.getParentFile().mkdirs();
            processedFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        
        // lemmatize
        List<String> lemmas = lem.lemmatize(fileStr);
        
        // remove stopwords and stem
        List<String> lemmasWithoutStopwords = StopwordRemover.removeStopwords(lemmas);
        
        List<String> stems 
            = stem.stemStr(Util.removeSpecialCharacters(Util.concatList(lemmasWithoutStopwords)));
        
        // write processed str to new file
        try {
            PrintStream ps = new PrintStream(processedFile.getAbsolutePath());
            ps.println(Util.concatList(stems));
            ps.close();
        }
        catch (Exception e) { }
    }
}
