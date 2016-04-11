package com.classifier.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Util {

    // convert an array of char primitives to an array of Character objects
    private static Character[] toObject(char[] array) {
        if (array == null || array.length == 0) {
            return null;
        }

        final Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Character(array[i]);
        }
        return result;
    }
    
    // read contents of a file into a String
    public static String readFile(String path)
        throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.forName("UTF-8"));
    }

    // read lines of file into a list
    public static List<String> readFileByLine(String path) 
        throws FileNotFoundException, IOException
    {
        List<String> lines = new ArrayList<String>();
        File file = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return lines; 
    }

    // remove all special characters from a string
    // replace them with spaces
    public static String removeSpecialCharacters(String str) {
        List<Character> chars = Arrays.asList(toObject(str.toCharArray()));
        String output = "";
        boolean lastCharWasLetter = true;
        for (Character ch : chars) {
            if (!Character.isLetter(ch) && lastCharWasLetter) {
                output += " ";
                lastCharWasLetter = false;
            } else if (Character.isLetter(ch)) {
                output += ch;
                lastCharWasLetter = true;
            }
        }
        return output;
    }

    public static String concatList(List<String> list) {
        String concatenated = "";
        for (String el : list) {
            concatenated += el + " ";
        }
        return concatenated;
    }   

    public static File[] fileArrayConcat(File[] a, File[] b) {
        int aLen = a.length;
        int bLen = b.length;
        File[] c = new File[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
