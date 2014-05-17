/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nickchen
 */
/* temporary fig.txt reader */
public class FigReader {

    /**
     *
     */
    public final Set<String> phrases;

    public FigReader(String filepath) throws IOException {
        this.phrases = new HashSet<>();
        Path path = Paths.get(filepath);
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        /* read the heading */
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.contains(":") || !line.contains(" ")) {
                continue;
            }
            phrases.add(line);
        }

    }
}
