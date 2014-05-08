/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.washington.phrasal.feature;

/**
 *
 * @author nickchen
 */
public class PhrasalVerbFeatures {
    private final FeatureGenerator fg;
    private final Integer sentenceId;
    private final Integer phrasalVerbId;
    
    public PhrasalVerbFeatures(FeatureGenerator fg, Integer sentenceId, Integer phrasalVerbId) {
        this.fg = fg;
        this.sentenceId = sentenceId;
        this.phrasalVerbId = phrasalVerbId;
    }
    
    public String phrasalVerbContextualClassification() {
        /* for now, just return classification base on sentence sentiment, but it should be the classification of the phrasalVerb */
        return fg.getNLPDict().getPhraseSentimentClassById(sentenceId);
    }

    public String phrasalVerbToken() {
        return "token=" + String.join("_", fg.getPhrasalVerbTokensById(phrasalVerbId));
    }

    public String phrasalVerbPOS() {
        String tagged = fg.getTagger().tagString(fg.getPhrasalVerbById(phrasalVerbId));
        return "POS=" + tagged.replace(" ", "_");
    }

    public String phrasalVerbContext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String phrasalVerbPriorPolarity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
