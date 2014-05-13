/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.data.sentimentreebank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.*;

/**
 *
 * @author nickchen
 */
public class StanfordNLPDict {

    private final PhraseIdDict phrase_id_dict;
    private final PhraseIdSentimentDict id_sentiment;

    public StanfordNLPDict(String dict_filename, String sentiment_filename) throws IOException {
        this.phrase_id_dict = new PhraseIdDict(dict_filename);
        this.id_sentiment = new PhraseIdSentimentDict(sentiment_filename);
    }

    public double getPhraseSentiment(String sentence) {
        return id_sentiment.sentimentDict.get(phrase_id_dict.idDict.get(sentence));
    }

    public Set<String> getAllPharses() {
        return phrase_id_dict.getAllPhrase();
    }

    public Integer getPhraseId(String phrase) {
        return phrase_id_dict.getPhraseId(phrase, false);
    }

    public Double getPhraseSentimentById(int sentence_id) {
        return id_sentiment.sentimentDict.get(sentence_id);
    }

    public String getPhraseSentimentClassById(int sentence_id) {
        double value = getPhraseSentimentById(sentence_id);
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

    public String getPhraseById(Integer sentenceId) {
        return phrase_id_dict.getBiDict().inverse().get(sentenceId);
    }

    public static void main(String args[]) {
        Options options = new Options();
        options.addOption("d", "dict", true, "dictionary file.");
        options.addOption("s", "sentiment", true, "sentiment value file.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (!line.hasOption("dict") && !line.hasOption("sentiment")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("StanfordNLPDict", options);
                return;
            }

            String dict_filename = line.getOptionValue("dict");
            String sentiment_filename = line.getOptionValue("sentiment");

            StanfordNLPDict snlp = new StanfordNLPDict(dict_filename, sentiment_filename);
            String sentence = "take off";
            System.out.printf("sentence [%1$s] %2$s\n", sentence, String.valueOf(snlp.getPhraseSentiment(sentence)));

        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("StanfordNLPDict", options);
        } catch (IOException ex) {
            Logger.getLogger(StanfordNLPDict.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Integer> findStanfordPhraseIdFromPhasalVerb(String phrasal_verb) {
        ArrayList<Integer> phrase_ids = new ArrayList<>();
        this.phrase_id_dict.getBiDict().forEach((phrase, phrase_id) -> {
            if (phrase.contains(phrasal_verb)) {
                phrase_ids.add(phrase_id);
            }
        });
        return phrase_ids;
    }
}
