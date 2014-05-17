package edu.washington.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StanfordSentimentTreebankInfo {

    public final Path SentimentLabelsPath;
    public final Path DictionaryPath;
    public final Path DatasetSplitPath;
    public final Path DatasetSentencesPath;
    public final Path SOStrPath;
    public final Path STreePath;

    public StanfordSentimentTreebankInfo(String dirPath) {
        SentimentLabelsPath = Paths.get(dirPath, "sentiment_labels.txt");
        DictionaryPath = Paths.get(dirPath, "dictionary.txt");
        DatasetSplitPath = Paths.get(dirPath, "datasetSplit.txt");
        DatasetSentencesPath = Paths.get(dirPath, "datasetSentences.txt");
        SOStrPath = Paths.get(dirPath, "SOStr.ext");
        STreePath = Paths.get(dirPath, "STree.txt");
    }
}
