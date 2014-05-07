package edu.washington.phrasal.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DistributionCounter {

    //config 
    private Path simplePhrasalVerbsPath = Paths
            .get("phrasal_verb_investigation/base_phrasal_verbs.txt");

    private Path scorePath = Paths
            .get("phrasal_verb_investigation/phrasal_verb_sentence_sentiment_scores.txt");

    //instance
    private Map<Integer, List<Integer>> indexedScores = new HashMap<Integer, List<Integer>>();

    public static void main(String[] args) {
        DistributionCounter c = new DistributionCounter();
        c.load();
        c.countAndPrint();
    }

    public void countAndPrint() {
        Map<Integer, Integer> counts = new TreeMap<>();

        for (Entry<Integer, List<Integer>> e : indexedScores.entrySet()) {
            int size = e.getValue().size();
            Integer previousEntriesWithThisSize = counts.getOrDefault(size, 0);
            counts.put(size, previousEntriesWithThisSize + 1);
        }
        for (Entry<Integer, Integer> count : counts.entrySet()) {
            System.out.println(count);
        }
    }

    public void load() {
        BiMap<Integer, String> indexedVerbs = HashBiMap.create();

        try {
            BufferedReader reader = Files.newBufferedReader(
                    simplePhrasalVerbsPath, StandardCharsets.UTF_8);
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] datum = line.split("\t");
                indexedVerbs.put(Integer.parseInt(datum[0]), datum[1]);
            }
            reader.close();

            // read in scores
            reader = Files.newBufferedReader(scorePath, StandardCharsets.UTF_8);
            line = "";
            while ((line = reader.readLine()) != null) {
                String[] datum = line.split("\t");
                Integer key = Integer.parseInt(datum[0]);
                String[] scoreStrings = datum[1].split(",");
                List<Integer> scores = new ArrayList<>();
                for (String score : scoreStrings) {
                    scores.add(Integer.parseInt(score));
                }
                indexedScores.put(key, scores);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
