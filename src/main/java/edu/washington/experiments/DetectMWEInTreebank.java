package edu.washington.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.washington.config.FilePaths;
import edu.washington.expressionlists.ExpressionFilters;
import edu.washington.expressionlists.ExpressionList;
import edu.washington.expressionlists.types.Expression;
import edu.washington.mwe.MWEDetector;

public class DetectMWEInTreebank {

	
	
	public static void main(String[] args) {
		
		//Where to store the output expression list 
		 Path outputSemcorFile = Paths.get("supplementary/mwe/best.secmor");
		 
//		Generate expression list
//		ExpressionList e = new ExpressionList(FilePaths.baseWordlist);
//		List<Expression> filteredExprs = e.getExpressions().parallelStream()
//				.filter(not(ExpressionFilters.isSingleWord))
//				.collect(Collectors.toList());
//		ExpressionList filteredList = new ExpressionList(filteredExprs);
//		System.out.println(filteredList.getExpressions().size() + " expresssions to consider");
//		filteredList.saveAsSemcor(outputSemcorFile);
		
		//Extract MWEs from stanford corpus
		MWEDetector detector = MWEDetector.getInstance();
		Map<String, List<IMWE<IToken>>> results = detector.detectFromTreebank(outputSemcorFile.toFile());
		
		//print results
//		results.entrySet().parallelStream().forEach(System.out::println);
//		printStaCtistics(results);
		
	}

	public static void printStatistics(Map<String, List<IMWE<IToken>>> results ){
		Integer numMWEs = 0;
			

			for (Entry<String, List<IMWE<IToken>>> result : results.entrySet()){
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
