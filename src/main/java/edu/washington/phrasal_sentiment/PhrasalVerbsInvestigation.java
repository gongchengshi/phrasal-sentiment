package edu.washington.phrasal_sentiment;

import edu.stanford.SentimentTreebank.PhraseIdDict;
import edu.stanford.SentimentTreebank.PhraseIdSentimentList;
import edu.stanford.SentimentTreebank.SentenceList;
import edu.stanford.SentimentTreebank.StanfordSentimentTreebankInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PhrasalVerbsInvestigation {
    public static void main(String[] args) throws IOException {
        StanfordSentimentTreebankInfo sentimentTbInfo = new StanfordSentimentTreebankInfo("supplementary/stanfordSentimentTreebank");

        Path wikipediaPhrasalVerbsPath = Paths.get("supplementary/phrasal_verb_lists/wikipedia_phrasal_verbs.txt");
        Path simplePhrasalVerbsPath = Paths.get("base_phrasal_verbs.txt");
        List<String> simplePhrasalVerbs = Utils.GetTwoWordLines(wikipediaPhrasalVerbsPath);

        WriteSimplePhrasalVerbsFile(simplePhrasalVerbs, simplePhrasalVerbsPath.toString());

        PhraseIdDict phraseIds = new PhraseIdDict(sentimentTbInfo.DictionaryPath);
        PhraseIdSentimentList phraseIdSentiments = new PhraseIdSentimentList(sentimentTbInfo.SentimentLabelsPath, 239233);
        SentenceList sentences = new SentenceList(sentimentTbInfo.DatasetSentencesPath, 11856);
        ArrayList<List<Integer>> phrasalVerbSentences = BuildPhraseVerbSentencesMap(simplePhrasalVerbs, sentences);
        List<Double> aveSentOfVerbalPhraseSentences = new ArrayList<>(simplePhrasalVerbs.size());

        for(List<Integer> verbPhraseSentences: phrasalVerbSentences) {
            Double aveSentiment = 0.0;
            for(Integer sentenceId : verbPhraseSentences) {
                Integer phraseId = phraseIds.Dict.get(sentences.List.get(sentenceId));
                Double sentimentScore = phraseIdSentiments.List.get(phraseId);
                aveSentiment += (Integer)SentimentScoreToClass(sentimentScore);
            }

            aveSentiment /= (double)verbPhraseSentences.size();
            aveSentOfVerbalPhraseSentences.add(aveSentiment);
        }

        WriteAverageSentimentOfVerbalPhraseSentences(aveSentOfVerbalPhraseSentences, "phrasal_verb_sentence_sentiment.txt");
    }

    private static void WriteAverageSentimentOfVerbalPhraseSentences(List<Double> values, String outPath) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outPath);
        int i = 0;
        for(Double value : values) {
            writer.println(i++ + "\t" + value);
        }
        writer.close();
    }

    private static void WriteSimplePhrasalVerbsFile(List<String> simplePhrasalVerbs, String outPath) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outPath);
        int i = 0;
        for(String item : simplePhrasalVerbs) {
            writer.println(i++ + "\t" +item);
        }
        writer.close();
    }

    public static int SentimentScoreToClass(Double value) {
        if(value <= .2) {
            return 1;
        }
        if(value <= .4) {
            return 2;
        }
        if(value <= .6) {
            return 3;
        }
        if(value <= .8) {
            return 4;
        }
        if(value <= 1) {
            return 5;
        }

        throw new IllegalArgumentException("value must be between 0 and 1");
    }

    public static ArrayList<List<Integer>> BuildPhraseVerbSentencesMap(
            List<String> simplePhrasalVerbs, SentenceList sentences) throws IOException {
        ArrayList<List<Integer>> phrasalVerbSentences = new ArrayList<List<Integer>>(simplePhrasalVerbs.size());

        for(String phrasalVerb : simplePhrasalVerbs) {
            phrasalVerbSentences.add(sentences.FindSentencesWithPhrase(phrasalVerb));
        }

        return phrasalVerbSentences;
    }
}
