package utilities;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public final class Util {

    // convert an array of char primitives to an array of Character objects
    public static Character[] toObject(char[] array) {
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
    public static String readFile(String path, Charset encoding)
        throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded,encoding);
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

    public static void main(String args [ ]) {
        String testStr = "hell there didn't i tell you or not 920359u2 ** I'm not sure 92105u " + 
            " what to ]da say <br /> <br /> hello";

        System.out.println(testStr);
        System.out.println(removeSpecialCharacters(testStr));
    }
}
