/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.config;

import edu.washington.phrasal.feature.FeatureGenerator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class Runner {

    public static void main(String[] args) {
        String features = "";
        FeatureGenerator f;
        try {
            f = new FeatureGenerator("/Users/nickchen/ling/LING575/phrasal-sentiment/");
            f.generateFeature("ID,phrasalVerbContextualClassification,phrasalVerbToken,phrasalVerbPOS,phrasalVerbContext");
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
