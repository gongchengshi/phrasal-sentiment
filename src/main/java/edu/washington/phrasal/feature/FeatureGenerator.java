/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import edu.pitt.cs.mpqa.subjectivity.SubjectivityLexicon;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.washington.config.FilePaths;
import edu.washington.config.StanfordSentimentTreebankInfo;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.data.sentimentreebank.StanfordNLPDict;
import edu.washington.phrasal.gold_standard.GoldStandard;
import edu.washington.util.Counter;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Boolean.TRUE;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class FeatureGenerator {

    /* sentence id to phrasal verbs */
    final public Map<Integer, Set<Integer>> sentenceIdToPhrasalVerbId;
    /* phrasal verb to Stanford NLP phrase id */
    final public Map<Integer, Set<Integer>> phrasalVerbIdToStanfordPhraseId;
    final public SentenceList sentenceList;
    final public StanfordNLPDict nlpDict;
    final public StanfordSentimentTreebankInfo stanfordInfo;
    final public Map<Integer, String> phrasalVerbIdToPhrase;
    final public MaxentTagger tagger;
    final public static String FEATURE_SEPARATOR = " ";
    final public static String FEATURE_VALUE_SEPARATOR = "=";
    final public GoldStandard goldStandard = new GoldStandard();
    final public SubjectivityLexicon subjectivityLexicon;

    public FeatureGenerator(String basepath) throws IOException {
        subjectivityLexicon = new SubjectivityLexicon(FilePaths.subjectivityLexicon);
        stanfordInfo = new StanfordSentimentTreebankInfo(basepath + "supplementary/stanfordSentimentTreebank");
        nlpDict = new StanfordNLPDict(stanfordInfo.DictionaryPath, stanfordInfo.SentimentLabelsPath);
        sentenceList = new SentenceList(stanfordInfo.DatasetSentencesPath);
        sentenceIdToPhrasalVerbId = new HashMap<>();
        phrasalVerbIdToPhrase = new HashMap<>();
        phrasalVerbIdToStanfordPhraseId = new HashMap<>();
        tagger = new MaxentTagger(
                "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

        populateSentenceSentiments(basepath);

    }

    /**
     *
     * @param featureList - the desired features in csv format
     * @return
     */
    public String generateFeature(String featureList) {
        ArrayList<SentenceIdPhrasalVerbId> spl = new ArrayList<>();
        sentenceIdToPhrasalVerbId.keySet().stream().sorted().forEach((sentenceId) -> {
            sentenceIdToPhrasalVerbId.get(sentenceId).stream().sorted().forEach((phrasalVerbId) -> {
                spl.add(new SentenceIdPhrasalVerbId(sentenceId, phrasalVerbId, this));
            });
        });

        List<Function<SentenceIdPhrasalVerbId, String>> fs = PhrasalVerbFeatures.getFeatureFunctions(new ArrayList<>(Arrays.asList(featureList.split(","))));

        return generateFunctionalFeatureDocument(fs, spl);
    }

    public String generateFunctionalFeatureDocument(List<Function<SentenceIdPhrasalVerbId, String>> fs, List<SentenceIdPhrasalVerbId> spl) {
        StringBuilder sb = new StringBuilder();
        spl.stream().forEach((sp) -> {
            StringBuilder sb_inner = new StringBuilder();
            fs.stream().forEach((f) -> {
                sb_inner.append(f.apply(sp));
                sb_inner.append(FEATURE_SEPARATOR);
            });
            System.out.println(sb_inner.toString());
            sb.append(sb_inner).append("\n");
        });
        return sb.toString();

    }

    private void populateSentenceSentiments(String basepath) {
        HashMap<String, Set<Integer>> keepPhrasalVerb = new HashMap<>();
        goldStandard.phraseSentiment.keySet().stream().forEach((String phrasal_verb) -> {
            try {
                Set<Integer> sentenceIds = new HashSet<>(sentenceList.findSentencesWithPhrase(phrasal_verb));
                
                if (sentenceIds.size() > 0) {
                    keepPhrasalVerb.merge(phrasal_verb, sentenceIds, (value, newValue) -> {
                        if (value == null) {
                            value = new HashSet<>();
                        }
                        value.addAll(newValue);
                        return value;
                    });
                }
            } catch (IOException inner_ex) {
                System.err.printf("ERROR: %1$s", inner_ex.getMessage());
            }
        });
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(basepath + "project_output/phrasal_verb_id.txt"),
                StandardCharsets.UTF_8)) {
            Counter c = new Counter();
            keepPhrasalVerb.keySet().stream().forEach((String phrasal_verb) -> {
                try {
                    StringBuilder append = new StringBuilder();
                    int id = c.next();
                    
                    phrasalVerbIdToPhrase.put(id, phrasal_verb);
                    append.append(id);
                    append.append("\t");
                    append.append(phrasal_verb);
                    writer.write(append.toString());
                    writer.newLine();
                    ArrayList<Integer> phraseId = nlpDict.findStanfordPhraseIdFromPhasalVerb(phrasal_verb);
                    phrasalVerbIdToStanfordPhraseId.put(id, new HashSet<>(phraseId));
                    keepPhrasalVerb.get(phrasal_verb).stream().forEach((sentence_id) -> {
                        Set<Integer> phrasal_verb_set = new HashSet<>();
                        phrasal_verb_set.add(id);
                        sentenceIdToPhrasalVerbId.merge(sentence_id, phrasal_verb_set, (value, newValue) -> {
                            if (value == null) {
                                value = new HashSet<>();
                            }
                            value.addAll(newValue);
                            return value;
                        });
                    });
                } catch (IOException inner_ex) {
                    System.err.printf("ERROR: %1$s", inner_ex.getMessage());
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public StanfordNLPDict getNLPDict() {
        return nlpDict;
    }

    public static String getClassBySentiment(double value) {
        if (value <= .2) {
            return "very_negative";
        }
        if (value <= .4) {
            return "negative";
        }
        if (value <= .6) {
            return "neutral";
        }
        if (value <= .8) {
            return "positive";
        }
        if (value <= 1) {
            return "very_positive";
        }
        return "UNKNOWN";
    }

    public MaxentTagger getTagger() {
        return tagger;
    }

    public String[] getPhrasalVerbTokensById(Integer phrasalVerbId) {
        String phrase = phrasalVerbIdToPhrase.get(phrasalVerbId);
        return phrase.split("\\s");
    }

    public String getPhrasalVerbById(Integer phrasalVerbId) {
        return phrasalVerbIdToPhrase.get(phrasalVerbId);
    }

    public String getPhrasalVerbClassificationById(Integer phrasalVerbId) {
        return getClassBySentiment(goldStandard.phraseSentiment.get(getPhrasalVerbById(phrasalVerbId)));
    }
}
