package edu.stanford.SentimentTreebank;

import java.nio.file.Paths;

public class StanfordSentimentTreebankInfo {

    public final String SentimentLabelsPath;
    public final String DictionaryPath;
    public final String DatasetSplitPath;
    public final String DatasetSentencesPath;
    public final String SOStrPath;
    public final String STreePath;

    public StanfordSentimentTreebankInfo(String dirPath) {
        SentimentLabelsPath = Paths.get(dirPath, "sentiment_labels.txt").toString();
        DictionaryPath = Paths.get(dirPath, "dictionary.txt").toString();
        DatasetSplitPath = Paths.get(dirPath, "datasetSplit.txt").toString();
        DatasetSentencesPath = Paths.get(dirPath, "datasetSentences.txt").toString();
        SOStrPath = Paths.get(dirPath, "SOStr.ext").toString();
        STreePath = Paths.get(dirPath, "STree.txt").toString();
    }
}
