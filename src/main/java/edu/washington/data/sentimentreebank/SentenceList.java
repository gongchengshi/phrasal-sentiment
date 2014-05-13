package edu.washington.data.sentimentreebank;

import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SentenceList {

    public final Map<Integer, String> sentenceList;
    private static final int DEFAULT_ARRAY_SIZE = 11855;

    private static final Splitter PIPE_SPLITTER = Splitter.on('\t')
            .trimResults()
            .omitEmptyStrings();

    public SentenceList(String path) throws IOException {
        this(path, DEFAULT_ARRAY_SIZE);
    }

    public SentenceList(String path, int size) throws IOException {
        sentenceList = new HashMap<>();
        ReadSentences(path);
    }

    private void ReadSentences(String path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
        /* read the heading */
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            Iterable<String> tokens = PIPE_SPLITTER.split(line);
            Iterator<String> tokensIter = tokens.iterator();
            int index = Integer.parseInt(tokensIter.next());
            sentenceList.put(index, tokensIter.next());
        }
    }
    
    public String getSentence(int id) {
        return sentenceList.get(id);
    }

    public List<Integer> findSentencesWithPhrase(String phrase) throws IOException {
        List<Integer> ids = new ArrayList<>();

        sentenceList.entrySet().stream().forEach(set -> {
            String sentence = set.getValue();
            Integer key = set.getKey();
            if (sentence.contains(phrase)) {
                ids.add(key);
            }
        });
//        System.out.println("hi");
        return ids;
    }
}
