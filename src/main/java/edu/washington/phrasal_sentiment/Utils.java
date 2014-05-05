package edu.washington.phrasal_sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static int CharCount(String text, char character) {
        int charCount = 0;
        for(int i = 0; i<text.length(); ++i) {
            if(text.charAt(i) == character) {
                ++charCount;
            }
        }
        return charCount;
    }

    // Creates a new file from all of the two word lines in the input file
    public static void GetTwoWordLines(String inPath, String outPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inPath));
        PrintWriter writer = new PrintWriter(outPath);

        String line;

        while((line = reader.readLine()) != null) {
            if (CharCount(line, ' ') == 1) {
                writer.println(line);
            }
        }

        writer.close();
        reader.close();
    }

    // Creates a new file from all of the two word lines in the input file
    public static List<String> GetTwoWordLines(Path inPath) throws IOException {
        BufferedReader reader = Files.newBufferedReader(inPath, StandardCharsets.UTF_8);
        ArrayList<String> rtn = new ArrayList<String>();
        String line;

        while((line = reader.readLine()) != null) {
            if (CharCount(line, ' ') == 1) {
                rtn.add(line);
            }
        }

        return rtn;
    }

    public static List<String> ReadLines(Path inPath) throws IOException {
        BufferedReader reader = Files.newBufferedReader(inPath, StandardCharsets.UTF_8);
        ArrayList<String> rtn = new ArrayList<String>();
        String line;
        while((line = reader.readLine()) != null) {
            rtn.add(line);
        }

        return rtn;
    }
}
