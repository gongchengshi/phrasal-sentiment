/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import com.google.common.base.Joiner;
import edu.stanford.nlp.ie.machinereading.common.SimpleTokenize;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author nickchen
 */
public class PhrasalVerbFeatures {

    public static HashMap<String, Function<SentenceIdPhrasalVerbId, String>> functionToMap = new HashMap<>();

    static Function<SentenceIdPhrasalVerbId, String> phrasalVerbContextualClassification = sp -> {
        /* XXX for now, just return classification base on phrase sentiment, but it should be the classification of the phrasalVerb */
        FeatureGenerator fg = sp.getFG();
        Set<Integer> phrase_ids = fg.phrasalVerbIdToStanfordPhraseId.get(sp.getPhrasalVerbId());
        return fg.getClassBySentiment(phrase_ids.stream().map((spid) -> {
            return fg.nlpDict.getPhraseSentimentById(spid);
        }).reduce((x, y) -> x + y).get() / phrase_ids.size());
    };

    static Function<SentenceIdPhrasalVerbId, String> phraseId = sp -> {
        return sp.getSentenceId() + "_" + String.join("_", sp.getPhrasalVerbTokens().toArray(new String[]{}));
    };

    static Function<SentenceIdPhrasalVerbId, String> phrasalVerbToken = sp -> {
        FeatureGenerator fg = sp.getFG();
        return "token" + FeatureGenerator.FEATURE_VALUE_SEPARATOR + String.join("_", fg.getPhrasalVerbTokensById(sp.getPhrasalVerbId()));
    };

    static Function<SentenceIdPhrasalVerbId, String> phrasalVerbPOS = sp -> {
        FeatureGenerator fg = sp.getFG();
        ArrayList<String> pv = SimpleTokenize.tokenize(fg.getPhrasalVerbById(sp.getPhrasalVerbId()));
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
    };

    static Function<SentenceIdPhrasalVerbId, String> phrasalVerbContext = sp -> {
        FeatureGenerator fg = sp.getFG();
        ArrayList<String> pv = SimpleTokenize.tokenize(fg.getPhrasalVerbById(sp.getPhrasalVerbId()));
        int index = Collections.indexOfSubList(sp.getSentenceTokens(), pv);
        if (index >= 0) {
            StringBuilder sb = new StringBuilder();
            if (index > 0) {
                sb.append("prevWord");
                sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
                sb.append(sp.getSentenceTokens().get(index - 1));
                sb.append(FeatureGenerator.FEATURE_SEPARATOR);
            }
            if ((index + pv.size()) < (sp.getSentenceTokens().size() - 1)) {
                sb.append("nextWord");
                sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
                sb.append(sp.getSentenceTokens().get(index + pv.size()));
            }
            return sb.toString().trim();
        }
        return "";
    };

    static {
        functionToMap.put("ID", phraseId);
        functionToMap.put("phrasalVerbContextualClassification", phrasalVerbContextualClassification);
        functionToMap.put("phrasalVerbToken", phrasalVerbToken);
        functionToMap.put("phrasalVerbPOS", phrasalVerbPOS);
        functionToMap.put("phrasalVerbContext", phrasalVerbContext);
    }

    public static List<Function<SentenceIdPhrasalVerbId, String>> getFeatureFunctions(ArrayList<String> al) {
        List<Function<SentenceIdPhrasalVerbId, String>> fl = new ArrayList<>();
        for (String l : al) {
            fl.add(functionToMap.get(l));
        }
        return fl;
    }
}
