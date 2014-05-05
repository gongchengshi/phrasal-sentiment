package edu.stanford.SentimentTreebank;

import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SentenceList {
    public final List<String> List;

    private static final Splitter PIPE_SPLITTER = Splitter.on('\t')
            .trimResults()
            .omitEmptyStrings();

    public SentenceList(String path, int size) throws IOException {
        List = new ArrayList(size);
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
            List.add(tokensIter.next());
        }
    }

    public List<Integer> FindSentencesWithPhrase(String phrase) throws IOException {
        List<Integer> ids = new ArrayList<Integer>();

        int i = 0;
        for(String sentence : List) {
            if(sentence.contains(phrase)) {
                ids.add(i);
            }
            ++i;
        }

        return ids;
    }
}
