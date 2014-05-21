/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.cs.mpqa.subjectivity;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class SubjectivityLexicon {

    public final Set<String> strongsubj;
    public final Set<String> weaksubj;

    private static final Splitter EQUAL_SPACE_SPLITTER = Splitter.on(CharMatcher.anyOf("= "))
            .trimResults()
            .omitEmptyStrings();

    public SubjectivityLexicon(Path lexconPath) {
        strongsubj = new HashSet<>();
        weaksubj = new HashSet<>();

        try {
            readLexicon(lexconPath);
        } catch (IOException ex) {
            Logger.getLogger(SubjectivityLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("strong count" + strongsubj.size());
        System.out.println("weak count" + weaksubj.size());
    }

    private void readLexicon(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        /* read the heading */
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            Iterable<String> tokens = EQUAL_SPACE_SPLITTER.split(line);
            Iterator<String> tokensIter = tokens.iterator();
            String prev = tokensIter.next();
            String subjectivity = null;
            String word = null;
            for (String token : tokens) {
                if (prev.equals("type")) {
                    subjectivity = token;
                } else if (prev.equals("word1")) {
                    word = token;
                    break;
                }
                prev = token;
            }

            if (subjectivity != null && word != null) {
                if (subjectivity.equals("strongsubj")) {
                    strongsubj.add(word);
                } else {
                    weaksubj.add(word);
                }
            }
        }
    }
}
