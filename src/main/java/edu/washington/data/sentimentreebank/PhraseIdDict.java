package edu.washington.data.sentimentreebank;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class PhraseIdDict {

    public final BiMap<String, Integer> phraseToId;
//    public final BiMap<Integer, String> idToPhrase;
    private static final Splitter PIPE_SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    public PhraseIdDict(Path path) throws IOException {
        this.phraseToId = HashBiMap.create();
//        this.idToPhrase = HashBiMap.create();
        this.read_dictionary(path);
    }

    public void read_dictionary(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        /* read the heading */
        String line;
        while ((line = reader.readLine()) != null) {
            Iterable<String> tokens = PIPE_SPLITTER.split(line);
            Iterator<String> tokens_iter = tokens.iterator();
            try {
                String phrase = tokens_iter.next();
                Integer id = Integer.parseInt(tokens_iter.next());
                phraseToId.put(phrase, id);
//                idToPhrase.put(id, phrase);
            } catch (NumberFormatException nfe) {
                System.err.printf("failed to parse numbers (dict): line=[%1$s] error=[%1$s]\n", line, nfe.getMessage());
            }
        }
    }

    public Set<String> getAllPhrase() {
        return phraseToId.keySet();
    }

    public Set<Integer> findPhrasesThatContain(String expression) {
        Set<Integer> ids = new HashSet<>();

        phraseToId.entrySet().stream().forEach(set -> {
            String phrase = set.getKey();
            Integer id = set.getValue();
            if (phrase.contains(expression)) {
                ids.add(id);
            }
        });
        return ids;
    }

    public Integer getPhraseId(String phrase, boolean replaceSpecial) {
        if (replaceSpecial) {
            phrase = phrase.replaceAll("-LRB-", "(").replace("-RRB-", ")");
        }

        return phraseToId.get(phrase);
    }

    public BiMap<String, Integer> getBiDict() {
        return phraseToId;
    }
}
