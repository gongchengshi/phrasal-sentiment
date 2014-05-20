package edu.washington.experiments;

import edu.washington.phrasal.gold_standard.GoldStandard;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class GoldStandardMain {
    public static void main(String[] args) {
        try {
            GoldStandard gs = new GoldStandard();

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("supplementary/gold_standard_sentiment.txt"),
                    StandardCharsets.UTF_8)) {
                for(Map.Entry<String, Double> expressionSentiment: gs.phraseSentiment.entrySet()) {
                    writer.write(String.format("%s\t%f\n", expressionSentiment.getKey(), expressionSentiment.getValue()));
                }
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
