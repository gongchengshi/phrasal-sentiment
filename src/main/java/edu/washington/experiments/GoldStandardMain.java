package edu.washington.experiments;

import edu.washington.phrasal.gold_standard.GoldStandard;

import java.io.IOException;

public class GoldStandardMain {
    public static void main(String[] args) {
        try {
            GoldStandard gs = new GoldStandard();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
