package edu.washington.experiments;

import edu.washington.phrasal.gold_standard.GoldStandard;

import java.util.Map;

public class GoldStandardMain {
    public static void main(String[] args) {
        GoldStandard gs = new GoldStandard();
        Map<String,Double> mweSentiment = gs.getSentimentFromTreebank();

    }
}
