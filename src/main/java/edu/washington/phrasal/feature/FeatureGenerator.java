/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import edu.stanford.SentimentTreebank.SentenceList;
import edu.stanford.SentimentTreebank.StanfordNLPDict;
import edu.stanford.SentimentTreebank.StanfordSentimentTreebankInfo;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
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
import java.util.List;
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
    final private Map<Integer, Set<Integer>> sentenceIdToPhrasalId;
    final private SentenceList sentenceList;
    final private StanfordNLPDict nlpDict;
    final private StanfordSentimentTreebankInfo stanfordInfo;
    final private Map<Integer, String> phrasalVerbIdToPhrase;
    final private MaxentTagger tagger;

    public FeatureGenerator(String basepath) throws IOException {
        stanfordInfo = new StanfordSentimentTreebankInfo(basepath + "supplementary/stanfordSentimentTreebank");
        nlpDict = new StanfordNLPDict(stanfordInfo.DictionaryPath, stanfordInfo.SentimentLabelsPath);
        sentenceList = new SentenceList(stanfordInfo.DatasetSentencesPath);
        sentenceIdToPhrasalId = new HashMap<>();
        phrasalVerbIdToPhrase = new HashMap<>();
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
                        keepPhrasalVerb.get(phrasal_verb).stream().forEach((Integer sentence_id) -> {
                            Set<Integer> phrasal_verb_set = new HashSet<>();
                            phrasal_verb_set.add(id);
                            sentenceIdToPhrasalId.merge(sentence_id, phrasal_verb_set, (value, newValue) -> {
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
        sentenceIdToPhrasalId.keySet().stream().sorted().forEach((sentenceId) -> {
            sentenceIdToPhrasalId.get(sentenceId).stream().sorted().forEach((phrasalVerbId) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(sentenceId);
                sb.append("_");
                sb.append(phrasalVerbId);
                sb.append(" ");
                
                PhrasalVerbFeatures pvf = new PhrasalVerbFeatures(this, sentenceId, phrasalVerbId);
                sb.append(pvf.phrasalVerbContextualClassification()).append(" ");
                sb.append(pvf.phrasalVerbToken()).append(" ");
                sb.append(pvf.phrasalVerbPOS()).append(" ");
//                sb.append(pvf.phrasalVerbContext()).append(" ");
//                sb.append(pvf.phrasalVerbPriorPolarity()).append(" ");

                System.out.println(sb.toString());
            });

        });

    }

    public StanfordNLPDict getNLPDict() {
        return nlpDict;
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
