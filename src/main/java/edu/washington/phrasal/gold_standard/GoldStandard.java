package edu.washington.phrasal.gold_standard;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.washington.config.FilePaths;
import edu.washington.data.sentimentreebank.PhraseIdDict;
import edu.washington.data.sentimentreebank.PhraseIdSentimentDict;
import edu.washington.data.sentimentreebank.PhraseIdSentimentList;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.expressionlists.ExpressionFilters;
import edu.washington.expressionlists.ExpressionList;
import edu.washington.expressionlists.types.Expression;
import edu.washington.mwe.MWEDetector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.washington.expressionlists.ExpressionFilters.not;

public class GoldStandard {
    public final Map<String, Double> phraseSentiment = new HashMap<>();;

    public GoldStandard() throws IOException {
        File indexFile = FilePaths.wikipediaFigSemcorFile.toFile();
        if(!indexFile.exists()) {
            createMWEIndexFile();
        }

        computeGoldStandard();
    }

    private static void createMWEIndexFile() {
        ExpressionList e = new ExpressionList(FilePaths.baseWordlist);
		List<Expression> filteredExprs = e.getExpressions().parallelStream()
				.filter(not(ExpressionFilters.isSingleWord))
				.collect(Collectors.toList());
		ExpressionList filteredList = new ExpressionList(filteredExprs);
		filteredList.saveAsSemcor(FilePaths.wikipediaFigSemcorFile);
    }

    public void computeGoldStandard() throws IOException {
        MWEDetector detector = new MWEDetector(FilePaths.wikipediaFigSemcorFile.toFile());

        int NUM_SENTENCES_IN_STANFORD = 11856;

        PhraseIdDict phraseIds = new PhraseIdDict(FilePaths.SSTPaths.DictionaryPath);
        PhraseIdSentimentList phraseIdSentimentList = new PhraseIdSentimentList(FilePaths.SSTPaths.SentimentLabelsPath);
        SentenceList sentences = new SentenceList(FilePaths.SSTPaths.DatasetSentencesPath, NUM_SENTENCES_IN_STANFORD);

        for (String text: sentences.sentenceList.values()) {
//                text = "A great ending does n't make up for a weak movie , and Crazy as Hell does n't even have a great ending .";
            List<IMWE<IToken>> mwes = detector.detect(text);
            for(IMWE<IToken> token: mwes){
                Set<Map.Entry<IToken, IMWEDesc.IPart>> entries = token.getPartMap().entrySet();
                StringBuilder phrase = new StringBuilder();
                int k = 0;
                for(Map.Entry<IToken, IMWEDesc.IPart> entry: entries) {
                    phrase.append(entry.getKey().getForm());
                    ++k;
                    if(k < entries.size()) {
                        phrase.append(" ");
                    }
                }
                String phraseText = phrase.toString();

                // Todo: Find the smallest phrase in the dictionary that came from this sentence and contains this phrase.
                // The first task is to get a list of phrases that came from this sentence.
                Integer phraseId = phraseIds.getPhraseId(phrase.toString(), false);
                if(phraseId == null) {
                    //System.out.println("<" + phrase.toString() + "> could not be found in dictionary\n" + text);
                }
                // Use the phrase ID to look up the sentiment value.
                try {
                phraseSentiment.put(phraseText, phraseIdSentimentList.sentimentList.get(phraseId));
                } catch(Exception ex) {
                    // Todo: Remove this once the first Todo is complete.
                }
            }
        }
    }
}
