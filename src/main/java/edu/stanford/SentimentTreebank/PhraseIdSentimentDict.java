package edu.stanford.SentimentTreebank;

import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class PhraseIdSentimentDict {

    public final HashMap<Integer, Double> sentimentDict = new HashMap<>();

    private static final Splitter PIPE_SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    public PhraseIdSentimentDict(String path) throws IOException {
        read_sentiment(path);
    }

    private void read_sentiment(String sentiment_filename) throws IOException {
        Path path = Paths.get(sentiment_filename);
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        /* read the heading */
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            Iterable<String> tokens = PIPE_SPLITTER.split(line);
            Iterator<String> tokens_iter = tokens.iterator();
            try {
                sentimentDict.put(Integer.parseInt(tokens_iter.next()), Double.parseDouble(tokens_iter.next()));
            } catch (NumberFormatException nfe) {
                System.err.printf("failed to parse numbers (dict): line=[%1$s] error=[%1$s]\n", line, nfe.getMessage());
            }
        }
    }
}
