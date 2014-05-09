package edu.washington.expressionlists;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExpressionFilters {

	private static Predicate<Expression> isSingleWord = (expression) ->{
		return expression.getText().split(" ").length > 1;
	};

	
	public static Function<ExpressionList,ExpressionList> removeSingleWords = (list) ->{
		List<Expression> multiWordExpressions= list.getExpressions().parallelStream().filter(isSingleWord).collect(Collectors.toList());
		return new ExpressionList(multiWordExpressions);
	};
	
	
	
	


}
