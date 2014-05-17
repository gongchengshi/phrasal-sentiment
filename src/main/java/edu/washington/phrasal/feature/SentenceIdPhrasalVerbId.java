/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import edu.stanford.nlp.ie.machinereading.common.SimpleTokenize;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nickchen
 */
public class SentenceIdPhrasalVerbId {

    private final Integer sentence_id;
    private final Integer phrasal_verb_id;
    private final FeatureGenerator fg;
    private final ArrayList<String> sentenceTokens;

    public SentenceIdPhrasalVerbId(Integer sentence_id, Integer phrasal_verb_id, FeatureGenerator fg) {
        this.sentence_id = sentence_id;
        this.phrasal_verb_id = phrasal_verb_id;
        this.fg = fg;
        this.sentenceTokens = SimpleTokenize.tokenize(fg.sentenceList.getSentence(this.sentence_id));
    }

    public Integer getPhrasalVerbId() {
        return phrasal_verb_id;
    }

    public Integer getSentenceId() {
        return sentence_id;
    }

    public FeatureGenerator getFG() {
        return this.fg;
    }

    public List<String> getSentenceTokens() {
        return sentenceTokens;
    }
}
