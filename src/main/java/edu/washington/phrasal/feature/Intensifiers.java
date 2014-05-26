package edu.washington.phrasal.feature;

import edu.washington.config.FilePaths;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Intensifiers {
    static Set<String> intensifiers;

    static {
        try {
            BufferedReader reader = Files.newBufferedReader(FilePaths.intensifiers, StandardCharsets.UTF_8);
            intensifiers = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                intensifiers.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean ContainsIntensifier(Collection<String> tokens) {
        for(String token : tokens) {
            if(intensifiers.contains(token.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
