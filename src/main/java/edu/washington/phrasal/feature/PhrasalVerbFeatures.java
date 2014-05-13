/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Joiner;

import edu.stanford.nlp.ie.machinereading.common.SimpleTokenize;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;

/**
 *
 * @author nickchen
 */
public class PhrasalVerbFeatures {

    private final FeatureGenerator fg;
    private final Integer sentenceId;
    private final Integer phrasalVerbId;
    private final ArrayList<String> sentenceTokens;

    public PhrasalVerbFeatures(FeatureGenerator fg, Integer sentenceId, Integer phrasalVerbId) {
        this.fg = fg;
        this.sentenceId = sentenceId;
        this.phrasalVerbId = phrasalVerbId;
        this.sentenceTokens = SimpleTokenize.tokenize(fg.sentenceList.getSentence(sentenceId));
    }

    public String phrasalVerbContextualClassification() {
        /* XXX for now, just return classification base on phrase sentiment, but it should be the classification of the phrasalVerb */
        Set<Integer> phrase_ids = fg.phrasalVerbIdToStanfordPhraseId.get(phrasalVerbId);
        return fg.getClassBySentiment(phrase_ids.stream().map((spid) -> {
            return fg.nlpDict.getPhraseSentimentById(spid);
        }).reduce((x, y) -> x + y).get() / phrase_ids.size());
    }

    public String phrasalVerbToken() {
        return "token" + FeatureGenerator.FEATURE_VALUE_SEPARATOR + String.join("_", fg.getPhrasalVerbTokensById(phrasalVerbId));
    }

    public String phrasalVerbPOS() {
        String tagged = fg.getTagger().tagString(fg.getPhrasalVerbById(phrasalVerbId));
        ArrayList<String> pv = SimpleTokenize.tokenize(fg.getPhrasalVerbById(phrasalVerbId));
        ArrayList<HasWord> hw = new ArrayList<>();
        pv.stream().forEach((w) -> {
            hw.add(new Word(w));
        });
        pv.clear();
        ArrayList<TaggedWord> tw = fg.getTagger().apply(hw);
        Joiner joiner = Joiner.on("_").skipNulls();
        tw.forEach((t) -> {
            pv.add(t.tag());
        });
        return "POS" + FeatureGenerator.FEATURE_VALUE_SEPARATOR + joiner.join(pv);
    }

    public String phrasalVerbContext() {
        ArrayList<String> pv = SimpleTokenize.tokenize(fg.getPhrasalVerbById(phrasalVerbId));
        int index = Collections.indexOfSubList(sentenceTokens, pv);
        if (index >= 0) {
            StringBuilder sb = new StringBuilder();
            if (index > 0) {
                sb.append("prevWord");
                sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
                sb.append(sentenceTokens.get(index - 1));
                sb.append(FeatureGenerator.FEATURE_SEPARATOR);
            }
            if (index < sentenceTokens.size()) {
                sb.append("nextWord");
                sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
                sb.append(sentenceTokens.get(index + pv.size()));
            }
            return sb.toString().trim();
        }
        return "";
    }

    public String phrasalVerbPriorPolarity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
