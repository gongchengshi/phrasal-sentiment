/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class Runner {

    public static void main(String[] args) {
        try {
            MaxentTagger tagger = new MaxentTagger(
                    "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

            // The sample string
            String sample = "This is a sample text";

            // The tagged string
            String tagged = tagger.tagString(sample);

            // Output the result
            System.out.println(tagged);
            
            FeatureGenerator f = new FeatureGenerator("/Users/nickchen/ling/LING575/phrasal-sentiment/");
            f.generateFeatureDocument();

        } catch (IOException ex) {
            Logger.getLogger(FeatureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
