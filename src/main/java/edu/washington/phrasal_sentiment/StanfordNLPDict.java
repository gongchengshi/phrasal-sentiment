/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.washington.phrasal_sentiment;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author nickchen
 */
public class StanfordNLPDict {
    private final BiMap<String,Integer> sentence_id;
    private final HashMap<Integer,Double> id_sentiment = new HashMap<>();
    private static final Splitter PIPE_SPLITTER = Splitter.on('|')
           .trimResults()
           .omitEmptyStrings();
    public StanfordNLPDict(String dict_filename, String sentiment_filename) {
        this.sentence_id = HashBiMap.create();
        this.read_dictionary(dict_filename);
        this.read_sentiment(sentiment_filename);
    }
    
    public double getSentenceSentiment(String sentence) {
        return id_sentiment.get(sentence_id.get(sentence));
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
            System.out.printf("sentence [%1$s] %2$s\n", sentence, String.valueOf(snlp.getSentenceSentiment(sentence)));
            
        } catch (ParseException exp) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "StanfordNLPDict", options );
        }
    }

    private void read_dictionary(String dict_filename) {
        Path path = Paths.get(dict_filename);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            /* read the heading */
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Iterable<String> tokens = PIPE_SPLITTER.split(line);
                Iterator<String> tokens_iter = tokens.iterator();
                try {
                    sentence_id.put(tokens_iter.next(), Integer.parseInt(tokens_iter.next()));
                } catch (NumberFormatException nfe) {
                    System.err.printf("failed to parse numbers (dict): line=[%1$s] error=[%1$s]\n", line, nfe.getMessage());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(StanfordNLPDict.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void read_sentiment(String sentiment_filename) {
        Path path = Paths.get(sentiment_filename);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            /* read the heading */
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Iterable<String> tokens = PIPE_SPLITTER.split(line);
                Iterator<String> tokens_iter = tokens.iterator();
                try {
                    id_sentiment.put(Integer.parseInt(tokens_iter.next()), Double.parseDouble(tokens_iter.next()));
                } catch (NumberFormatException nfe) {
                    System.err.printf("failed to parse numbers (dict): line=[%1$s] error=[%1$s]\n", line, nfe.getMessage());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(StanfordNLPDict.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
