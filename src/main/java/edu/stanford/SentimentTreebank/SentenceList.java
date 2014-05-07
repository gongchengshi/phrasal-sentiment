package edu.stanford.SentimentTreebank;

import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SentenceList {

    public final List<String> sentenceList;
    private static final int DEFAULT_ARRAY_SIZE = 1000;

    private static final Splitter PIPE_SPLITTER = Splitter.on('\t')
            .trimResults()
            .omitEmptyStrings();

    public SentenceList(String path) throws IOException {
        this(path, DEFAULT_ARRAY_SIZE);
    }

    public SentenceList(String path, int size) throws IOException {
        sentenceList = new ArrayList(size);
        ReadSentences(path);
    }

    private void ReadSentences(String path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        /* read the heading */
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            Iterable<String> tokens = PIPE_SPLITTER.split(line);
            Iterator<String> tokensIter = tokens.iterator();
            tokensIter.next();
            sentenceList.add(tokensIter.next());
        }
    }
    
    public String getSentence(int id) {
        return sentenceList.get(id);
    }

    public List<Integer> findSentencesWithPhrase(String phrase) throws IOException {
        List<Integer> ids = new ArrayList<>();

        for (int i = 0; i < sentenceList.size(); i++) {
            String sentence = sentenceList.get(i);
            if (sentence.contains(phrase)) {
                ids.add(i);
            }
        }

        return ids;
    }
}
