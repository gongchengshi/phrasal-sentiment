/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.SentimentTreebank;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickchen
 */
public class StanfordNLPDict {
    private final PhraseIdDict phrase_id;
    private final PhraseIdSentimentDict id_sentiment;

    public StanfordNLPDict(String dict_filename, String sentiment_filename) throws IOException {
        this.phrase_id = new PhraseIdDict(dict_filename);
        this.id_sentiment = new PhraseIdSentimentDict(sentiment_filename);
    }

    public double getPhraseSentiment(String sentence) {
        return id_sentiment.Dict.get(phrase_id.Dict.get(sentence));
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
                formatter.printHelp( "StanfordNLPDict", options );
                return;
            }

            String dict_filename = line.getOptionValue("dict");
            String sentiment_filename = line.getOptionValue("sentiment");
            
            
            StanfordNLPDict snlp = new StanfordNLPDict(dict_filename, sentiment_filename);
            String sentence = "take off";
            System.out.printf("sentence [%1$s] %2$s\n", sentence, String.valueOf(snlp.getPhraseSentiment(sentence)));
            
        } catch (ParseException exp) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "StanfordNLPDict", options );
        } catch (IOException ex) {
            Logger.getLogger(StanfordNLPDict.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
