package edu.stanford.SentimentTreebank;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

public final class PhraseIdDict {

    public final BiMap<String, Integer> idDict;
    private static final Splitter PIPE_SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    public PhraseIdDict(String path) throws IOException {
        this.idDict = HashBiMap.create();
        this.read_dictionary(path);
    }

    public void read_dictionary(String dict_filename) throws IOException {
        Path path = Paths.get(dict_filename);
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        /* read the heading */
        String line;
        while ((line = reader.readLine()) != null) {
            Iterable<String> tokens = PIPE_SPLITTER.split(line);
            Iterator<String> tokens_iter = tokens.iterator();
            try {
                idDict.put(tokens_iter.next(), Integer.parseInt(tokens_iter.next()));
            } catch (NumberFormatException nfe) {
                System.err.printf("failed to parse numbers (dict): line=[%1$s] error=[%1$s]\n", line, nfe.getMessage());
            }
        }
    }

    public Set<String> getAllPhrase() {
        return idDict.keySet();
    }

    public Integer getPhraseId(String phrase, boolean replaceSpecial) {
        if (replaceSpecial) {
            phrase = phrase.replaceAll("-LRB-", "(").replace("-RRB-", ")");
        }

        return idDict.get(phrase);
    }
}
