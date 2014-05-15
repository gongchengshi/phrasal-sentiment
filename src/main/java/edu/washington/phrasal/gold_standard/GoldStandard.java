package edu.washington.phrasal.gold_standard;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.washington.config.FilePaths;
import edu.washington.expressionlists.ExpressionFilters;
import edu.washington.expressionlists.ExpressionList;
import edu.washington.expressionlists.types.Expression;
import edu.washington.mwe.MWEDetector;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.washington.expressionlists.ExpressionFilters.not;

public class GoldStandard {
    public GoldStandard() {

    }

    public void createMWEIndexFile() {
        ExpressionList e = new ExpressionList(FilePaths.baseWordlist);
		List<Expression> filteredExprs = e.getExpressions().parallelStream()
				.filter(not(ExpressionFilters.isSingleWord))
				.collect(Collectors.toList());
		ExpressionList filteredList = new ExpressionList(filteredExprs);
		filteredList.saveAsSemcor(FilePaths.wikipediaFigSemcorFile);
    }

    public Map<String, Double> getSentimentFromTreebank() {
        //Extract MWEs from stanford corpus
        MWEDetector detector = MWEDetector.getInstance();
        Map<String, List<IMWE<IToken>>> results = detector.detectFromTreebank(FilePaths.wikipediaFigSemcorFile.toFile());
        Map<String, Double> mweSentiment = null;
        // Todo
        return mweSentiment;
    }
}
