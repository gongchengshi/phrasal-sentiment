package edu.washington.experiments;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.washington.config.FilePaths;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.expressionlists.ExpressionFilters;
import edu.washington.expressionlists.ExpressionList;
import edu.washington.expressionlists.types.Expression;
import edu.washington.mwe.MWEDetector;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DetectMWEInTreebank {

    public static void main(String[] args) throws IOException {
		//Generate expression list
//		ExpressionList e = new ExpressionList(FilePaths.baseWordlist);
//		List<Expression> filteredExprs = e.getExpressions().parallelStream()
//				.filter(not(ExpressionFilters.isSingleWord))
//				.collect(Collectors.toList());
//		ExpressionList filteredList = new ExpressionList(filteredExprs);
//		System.out.println(filteredList.getExpressions().size() + " expresssions to consider");
//		filteredList.saveAsSemcor(FilePaths.wikipediaFigSemcorFile);

        //Extract MWEs from stanford corpus
        MWEDetector detector = new MWEDetector(FilePaths.wikipediaFigSemcorFile.toFile());
        int NUM_SENTENCES_IN_STANFORD = 11856;
        SentenceList sentences = new SentenceList(FilePaths.SSTPaths.DatasetSentencesPath, NUM_SENTENCES_IN_STANFORD);
        Map<String, List<IMWE<IToken>>> results = new HashMap<>();
        for (int sentenceId = 0; sentenceId<sentences.sentenceList.size(); ++sentenceId) {
            String sentence = sentences.getSentence(sentenceId+1);
            List<IMWE<IToken>> mwes = detector.detect(sentence);
            results.put(sentence, mwes);
        }

        printResults(results);
    }

    public static void printResults(Map<String, List<IMWE<IToken>>> results) {
        int count = 0;
        for (Entry<String, List<IMWE<IToken>>> result : results.entrySet()) {
            if(result.getValue().size() <= 0) {
                continue;
            }
            System.out.println(result.getKey());

            count += result.getValue().size();

            for(IMWE<IToken> mwe: result.getValue()) {
                System.out.println("\t" + mwe.getEntry() + " -> " + mwe.getForm());
            }
        }
        System.out.println("Total count = " + count);
    }

    public static void printStatistics(Map<String, List<IMWE<IToken>>> results) {
        Integer numMWEs = 0;

        for (Entry<String, List<IMWE<IToken>>> result : results.entrySet()) {
            String text = result.getKey();

            List<IMWE<IToken>> extractedExpressions = result.getValue();

            numMWEs += extractedExpressions.size();
        }
        System.out.println(numMWEs);
    }

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
