package edu.washington.experiments;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.washington.config.FilePaths;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.mwe.MWEDetector;
import edu.washington.phrasal.gold_standard.GoldStandard;
import edu.stanford.nlp.sentiment.ReadSentimentDataset;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoldStandardMain {
    public static void main(String[] args) {
        GoldStandard.createMWEIndexFile();

//        ReadSentimentDataset r = new ReadSentimentDataset();

        int NUM_SENTENCES_IN_STANFORD = 11856;
        Map<String, List<IMWE<IToken>>> results = new HashMap<String, List<IMWE<IToken>>>();
        try {
            MWEDetector detector = new MWEDetector(FilePaths.wikipediaFigSemcorFile.toFile());
            SentenceList sentences = new SentenceList(FilePaths.SSTPaths.DatasetSentencesPath, NUM_SENTENCES_IN_STANFORD);
            for (int i = 1; i < NUM_SENTENCES_IN_STANFORD; i++) {
                String text = sentences.getSentence(i);
                List<IMWE<IToken>> mwes = detector.detect(text);
                results.put(text, mwes);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        Map<String, List<IMWE<IToken>>> results = detector.detectFromTreebank(FilePaths.wikipediaFigSemcorFile.toFile());

        //GoldStandard gs = new GoldStandard();
        //Map<String,Double> mweSentiment = gs.getSentimentFromTreebank();

    }
}
