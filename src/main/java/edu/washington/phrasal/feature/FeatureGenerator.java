/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import edu.stanford.SentimentTreebank.SentenceList;
import edu.stanford.SentimentTreebank.StanfordNLPDict;
import edu.stanford.SentimentTreebank.StanfordSentimentTreebankInfo;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class FeatureGenerator {

    final private HashMap<Integer, ArrayList<Integer>> sentenceSentiments;

    public FeatureGenerator(String basepath) throws IOException {
        StanfordSentimentTreebankInfo stanford = new StanfordSentimentTreebankInfo(basepath + "supplementary/stanfordSentimentTreebank");
        StanfordNLPDict nlpDict = new StanfordNLPDict(stanford.DictionaryPath, stanford.SentimentLabelsPath);
        SentenceList sentenceList = new SentenceList(stanford.DatasetSentencesPath);
        sentenceSentiments = new HashMap<>();

        populateSentenceSentiments(basepath, nlpDict, sentenceList, basepath + "supplementary/phrasal_verb_lists/fig.txt");
    }

    private void populateSentenceSentiments(String basepath, StanfordNLPDict nlpDict, SentenceList sentenceList, String phrasal_filepath) {
        /* generate initial set of sentences that have phrasal verbs from fig.txt */
        try {
            FigReader f = new FigReader(phrasal_filepath);
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

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(basepath + "project_output/phrasal_verb_id.txt"),
                    StandardCharsets.UTF_8)) {
                Counter c = new Counter();
                keepPhrasalVerb.keySet().stream().forEach((String phrasal_verb) -> {
                    try {
                        StringBuilder append = new StringBuilder();
                        append.append(c.next());
                        append.append("\t");
                        append.append(phrasal_verb);
                        writer.write(append.toString());
                        writer.newLine();
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
    }

    public static void main(String[] args) {
        try {
            FeatureGenerator f = new FeatureGenerator("/Users/nickchen/ling/LING575/phrasal-sentiment/");
        } catch (IOException ex) {
            Logger.getLogger(FeatureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

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
