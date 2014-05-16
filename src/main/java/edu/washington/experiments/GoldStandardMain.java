package edu.washington.experiments;

import edu.mit.jmwe.data.*;
import edu.washington.config.FilePaths;
import edu.washington.data.sentimentreebank.PhraseIdDict;
import edu.washington.data.sentimentreebank.PhraseIdSentimentDict;
import edu.washington.data.sentimentreebank.PhraseIdSentimentList;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.mwe.MWEDetector;
import edu.washington.phrasal.gold_standard.GoldStandard;
import edu.stanford.nlp.sentiment.ReadSentimentDataset;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.mit.jmwe.data.IMWEDesc.IPart;

public class GoldStandardMain {
    public static void main(String[] args) {
//        GoldStandard.createMWEIndexFile();
        try {
            PhraseIdDict phraseIds = new PhraseIdDict(FilePaths.SSTPaths.DictionaryPath);
            int NUM_PHRASES_IN_STANFORD = 239233;
            PhraseIdSentimentList phraseIdSentimentList = new PhraseIdSentimentList(FilePaths.SSTPaths.SentimentLabelsPath);

            PhraseIdSentimentDict foundPhraseIdSentiment = new PhraseIdSentimentDict();

            MWEDetector detector = new MWEDetector(FilePaths.wikipediaFigSemcorFile.toFile());
//            String sentence = "A great ending does n't make up for a weak movie , and Crazy as Hell does n't even have a great ending .";
//            List<IMWE<IToken>> sentenceMwes = detector.detect(sentence);

            int NUM_SENTENCES_IN_STANFORD = 11856;
            Map<String, List<IMWE<IToken>>> results = new HashMap<>(NUM_SENTENCES_IN_STANFORD);

            SentenceList sentences = new SentenceList(FilePaths.SSTPaths.DatasetSentencesPath, NUM_SENTENCES_IN_STANFORD);
            for (int i = 1; i < NUM_SENTENCES_IN_STANFORD; i++) {
                String text = sentences.getSentence(i);
//                text = "A great ending does n't make up for a weak movie , and Crazy as Hell does n't even have a great ending .";
                List<IMWE<IToken>> mwes = detector.detect(text);
                for(IMWE<IToken> token: mwes){
                    Set<Map.Entry<IToken, IPart>> entries = token.getPartMap().entrySet();
                    StringBuilder phrase = new StringBuilder();
                    int k = 0;
                    for(Map.Entry<IToken, IPart> entry: entries) {
                        phrase.append(entry.getKey().getForm());
                        ++k;
                        if(k < entries.size()) {
                            phrase.append(" ");
                        }
                    }
                    Integer phraseId = phraseIds.getPhraseId(phrase.toString(), false);
                    if(phraseId == null) {
                        System.out.println("<" + phrase.toString() + "> could not be found in dictionary\n" + text);
                    }
                    foundPhraseIdSentiment.sentimentDict.put(phraseId, phraseIdSentimentList.sentimentList.get(phraseId));
                }
                results.put(text, mwes);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
