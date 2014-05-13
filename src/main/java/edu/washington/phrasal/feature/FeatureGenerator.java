/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.data.sentimentreebank.StanfordNLPDict;
import edu.washington.data.sentimentreebank.StanfordSentimentTreebankInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    public FeatureGenerator(String basepath) throws IOException {
        stanfordInfo = new StanfordSentimentTreebankInfo(basepath + "supplementary/stanfordSentimentTreebank");
        nlpDict = new StanfordNLPDict(stanfordInfo.DictionaryPath, stanfordInfo.SentimentLabelsPath);
        sentenceList = new SentenceList(stanfordInfo.DatasetSentencesPath);
        sentenceIdToPhrasalVerbId = new HashMap<>();
        phrasalVerbIdToPhrase = new HashMap<>();
        phrasalVerbIdToStanfordPhraseId = new HashMap<>();
        tagger = new MaxentTagger(
                    "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

        populateSentenceSentiments(basepath, basepath + "supplementary/phrasal_verb_lists/fig.txt");
    }

    private void populateSentenceSentiments(String basepath, String phrasal_filepath) {
        /* generate initial set of sentences that have phrasal verbs from fig.txt */
        try {
            FigReader f = new FigReader(phrasal_filepath);
            /* phrasal verb to sentence ids */
            HashMap<String, Set<Integer>> keepPhrasalVerb = new HashMap<>();

            f.phrases.stream().forEach((String phrasal_verb) -> {
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

            /* generate id for phrasal verb */
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
        } catch (IOException ex) {
            Logger.getLogger(FeatureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* print out feature in mallet format to */
    public void generateFeatureDocument() {
        /* format is id classification key:value key:value */
        sentenceIdToPhrasalVerbId.keySet().stream().sorted().forEach((sentenceId) -> {
            sentenceIdToPhrasalVerbId.get(sentenceId).stream().sorted().forEach((phrasalVerbId) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(sentenceId);
                sb.append("_");
                sb.append(phrasalVerbId);
                sb.append(" ");
                
                PhrasalVerbFeatures pvf = new PhrasalVerbFeatures(this, sentenceId, phrasalVerbId);
                sb.append(pvf.phrasalVerbContextualClassification()).append(FEATURE_SEPARATOR);
                sb.append(pvf.phrasalVerbToken()).append(FEATURE_SEPARATOR);
                sb.append(pvf.phrasalVerbPOS()).append(FEATURE_SEPARATOR);
                sb.append(pvf.phrasalVerbContext()).append(FEATURE_SEPARATOR);
//                sb.append(pvf.phrasalVerbPriorPolarity()).append(" ");

                System.out.println(sb.toString());
            });

        });

    }

    public StanfordNLPDict getNLPDict() {
        return nlpDict;
    }

    public String getClassBySentiment(double value) {
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
            return "very";
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

    public class Counter {

        private int count;

        public Counter() {
            this(0);
        }

        public Counter(int count) {
            this.count = count;
        }

        public int next() {
            return ++count;
        }
    }
}
