package edu.washington.phrasal.gold_standard;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CollectionUtils;
import edu.washington.config.FilePaths;
import edu.washington.data.sentimentreebank.*;
import edu.washington.expressionlists.ExpressionFilters;
import edu.washington.expressionlists.ExpressionList;
import edu.washington.expressionlists.types.Expression;
import edu.washington.mwe.MWEDetector;
import edu.washington.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static edu.washington.expressionlists.ExpressionFilters.not;

public class GoldStandard {
    public final Map<String, Double> phraseSentiment = new TreeMap<>();

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

        PhraseIdSentimentList phraseIdSentimentList = new PhraseIdSentimentList(FilePaths.SSTPaths.SentimentLabelsPath);
        SentenceList sentences = new SentenceList(FilePaths.SSTPaths.DatasetSentencesPath, NUM_SENTENCES_IN_STANFORD);

        for (int sentenceId = 0; sentenceId<sentences.sentenceList.size(); ++sentenceId) {
            String sentence = sentences.getSentence(sentenceId+1);
//                sentence = "A great ending does n't make up for a weak movie , and Crazy as Hell does n't even have a great ending .";
            List<IMWE<IToken>> mwes = detector.detect(sentence);
            for(IMWE<IToken> token: mwes) {
                List<String> origExpression = Arrays.asList(token.getForm().split("_"));
                String foundExpression = token.getEntry().getForm().replace('_', ' ');

                // Find the smallest phrase in the dictionary that came from this sentence and contains this phrase.
                // Note: It isn't guaranteed that this phraseId is from the current sentence.
                Integer shortestPhraseId = SentimentDataset.getPhraseId(origExpression);

                if(shortestPhraseId == null) {
                    int currMinLen = Integer.MAX_VALUE;
                    for(Integer phraseId: SentimentDataset.phrasesInSentences.get(sentenceId)) {
                        List<String> phraseTokens = SentimentDataset.idLowerCasePhraseMap.get(phraseId);

                        if(phraseTokens.size() < currMinLen && Utils.listContains(phraseTokens, origExpression)) {
                            shortestPhraseId = phraseId;
                            currMinLen = phraseTokens.size();
                        }
                    }
                }

                try {
                    // Use the phrase ID to look up the sentiment value.
                    // Todo: What about cases where the same expression is found in multiple sentences/phrases
                    phraseSentiment.put(foundExpression, phraseIdSentimentList.sentimentList.get(shortestPhraseId));
                } catch(Exception ex) {
                    String phraseText = String.join(" ", origExpression);
                    System.out.println("<" + phraseText + "> could not be found in dictionary\n" + sentence);
                }
            }
        }
    }
}
