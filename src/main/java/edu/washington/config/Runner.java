/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.config;

import edu.washington.phrasal.feature.FeatureGenerator;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class Runner {

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        String features = "";
        FeatureGenerator f;
        try {
            String basepath = "/Users/nickchen/ling/LING575/phrasal-sentiment/";
            f = new FeatureGenerator(basepath);
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(basepath + "project_output/data.txt"),
                    StandardCharsets.UTF_8)) {
                writer.write(f.generateFeature("ID,phrasalVerbContextualClassification,phrasalVerbToken,phrasalVerbPOS,phrasalVerbContext"));
            }
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
