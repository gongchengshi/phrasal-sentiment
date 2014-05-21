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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author nickchen
 */
public class PhrasalVerbFeatures {

    public static HashMap<String, Function<SentenceIdPhrasalVerbId, String>> functionToMap = new HashMap<>();

    public final static Joiner NON_NULL_JOINER = Joiner.on("_").skipNulls();

    static final Function<SentenceIdPhrasalVerbId, String> phrasalVerbContextualClassification = sp -> {
        /* XXX for now, just return classification base on phrase sentiment, but it should be the classification of the phrasalVerb */
        FeatureGenerator fg = sp.getFG();
        Set<Integer> phrase_ids = fg.phrasalVerbIdToStanfordPhraseId.get(sp.getPhrasalVerbId());
        return fg.getClassBySentiment(phrase_ids.stream().map((spid) -> {
            return fg.nlpDict.getPhraseSentimentById(spid);
        }).reduce((x, y) -> x + y).get() / phrase_ids.size());
    };

    static final Function<SentenceIdPhrasalVerbId, String> phraseId = sp -> {
        return sp.getSentenceId() + "_" + String.join("_", sp.getPhrasalVerbTokens().toArray(new String[]{}));
    };

    static final Function<SentenceIdPhrasalVerbId, String> phrasalVerbToken = sp -> {
        FeatureGenerator fg = sp.getFG();
        return "token" + FeatureGenerator.FEATURE_VALUE_SEPARATOR + String.join("_", fg.getPhrasalVerbTokensById(sp.getPhrasalVerbId()));
    };

    static final Function<SentenceIdPhrasalVerbId, String> phrasalVerbPOS = sp -> {
        return "POS" + FeatureGenerator.FEATURE_VALUE_SEPARATOR + NON_NULL_JOINER.join(sp.getPhraseVerbPOS());
    };

    static final Function<SentenceIdPhrasalVerbId, String> phrasalVerbContext = sp -> {
        int start = sp.pvStartIndex;
        int end = sp.pvEndIndex;
        StringBuilder sb = new StringBuilder();
        if (start > 0) {
            sb.append("prevWord");
            sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
            sb.append(sp.getSentenceTokens().get(start - 1));
            sb.append(FeatureGenerator.FEATURE_SEPARATOR);
        }
        if (end < (sp.getSentenceTokens().size() - 1)) {
            sb.append("nextWord");
            sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
            sb.append(sp.getSentenceTokens().get(end));
            sb.append(FeatureGenerator.FEATURE_SEPARATOR);
        }
        if (start > 1) {
            sb.append("prev2Word");
            sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
            sb.append(sp.getSentenceTokens().get(start - 2));
            sb.append(FeatureGenerator.FEATURE_SEPARATOR);
        }
        if (end < (sp.getSentenceTokens().size() - 2)) {
            sb.append("next2Word");
            sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
            sb.append(sp.getSentenceTokens().get(end + 1));
            sb.append(FeatureGenerator.FEATURE_SEPARATOR);

        }
        return sb.toString().trim();
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentencePOSContext = sp -> {
        int start = sp.pvStartIndex;
        int end = sp.pvEndIndex;
        StringBuilder sb = new StringBuilder();
        if (start > 0) {
            sb.append("prevPOS");
            sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
            sb.append(sp.getSentencePOS().get(start - 1));
            sb.append(FeatureGenerator.FEATURE_SEPARATOR);
        }
        if (end < (sp.getSentencePOS().size() - 1)) {
            sb.append("nextPOS");
            sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
            sb.append(sp.getSentencePOS().get(end));
        }

        return "";
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentenceAdjectCount = sp -> {
        StringBuilder sb = new StringBuilder();
        sb.append("adjectiveCount");
        sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
        sb.append(
                (sp.getSentencePOS().stream()
                .filter(pos -> pos.startsWith("JJ")).count())
                - (sp.getSentenceTokens().stream()
                .filter(tok -> tok.equals("not")).count())
        );
        return sb.toString();
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentenceAdverbCount = sp -> {
        StringBuilder sb = new StringBuilder();
        sb.append("adverbCount");
        sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
        sb.append(sp.getSentencePOS().stream()
                .filter(pos -> pos.startsWith("RB")).count());
        return sb.toString();
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentenceHasPronoun = sp -> {
        StringBuilder sb = new StringBuilder();
        sb.append("pronounInSentence");
        sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
        sb.append(sp.getSentencePOS().stream()
                .filter(pos -> pos.startsWith("PR")).count() > 0);
        return sb.toString();
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentenceHasModal = sp -> {
        StringBuilder sb = new StringBuilder();
        sb.append("modalInSentence");
        sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
        sb.append(sp.getSentencePOS().stream()
                .filter(pos -> pos.equals("MD"))
                .count() > 0);
        return sb.toString();
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentenceStrongCount = sp -> {
        StringBuilder sb = new StringBuilder();
        sb.append("strongsubjCount");
        sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
        Set<String> intersection = new HashSet<>(sp.getSentenceTokens());
        intersection.retainAll(sp.getFG().subjectivityLexicon.strongsubj);
        sb.append(intersection.size());
        return sb.toString();
    };

    static final Function<SentenceIdPhrasalVerbId, String> sentenceWeakCount = sp -> {
        StringBuilder sb = new StringBuilder();
        sb.append("weaksubjCount");
        sb.append(FeatureGenerator.FEATURE_VALUE_SEPARATOR);
        Set<String> intersection = new HashSet<>(sp.getSentenceTokens());
        intersection.retainAll(sp.getFG().subjectivityLexicon.weaksubj);
        sb.append(intersection.size());
        return sb.toString();
    };

    static {
        functionToMap.put("ID", phraseId);
        functionToMap.put("phrasalVerbContextualClassification", phrasalVerbContextualClassification);
        functionToMap.put("phrasalVerbToken", phrasalVerbToken);
        functionToMap.put("phrasalVerbPOS", phrasalVerbPOS);
        functionToMap.put("phrasalVerbContext", phrasalVerbContext);

        functionToMap.put("sentencePOSContext", sentencePOSContext);
        functionToMap.put("sentenceAdjectCount", sentenceAdjectCount);
        functionToMap.put("sentenceAdverbCount", sentenceAdverbCount);
        functionToMap.put("sentenceHasPronoun", sentenceHasPronoun);
        functionToMap.put("sentenceHasModal", sentenceHasModal);
        functionToMap.put("sentenceWeakCount", sentenceWeakCount);
        functionToMap.put("sentenceStrongCount", sentenceStrongCount);

    }

    public static List<Function<SentenceIdPhrasalVerbId, String>> getFeatureFunctions(ArrayList<String> al) {
        List<Function<SentenceIdPhrasalVerbId, String>> fl = new ArrayList<>();
        for (String l : al) {
            fl.add(functionToMap.get(l));
        }
        return fl;
    }
}
