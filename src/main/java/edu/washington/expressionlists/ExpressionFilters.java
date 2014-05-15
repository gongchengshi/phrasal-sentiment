package edu.washington.expressionlists;

import java.util.List;
import java.util.function.Predicate;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.washington.expressionlists.types.Expression;
import edu.washington.util.Prepositions;
import edu.washington.util.StanfordAnnotator;

public class ExpressionFilters {

    private static StanfordAnnotator annotator = StanfordAnnotator.getInstance();

    public static Predicate<Expression> not(Predicate<Expression> t) {
        return t.negate();
    }

	//AVAILABLE FILTERS
    //returns true if the expression is one word 
    public static Predicate<Expression> isSingleWord = (expression) -> {
        return expression.getText().split(" ").length == 1;
    };

    //returns true if the expression starts with a prep
    public static Predicate<Expression> startsWithPrep = (expression) -> {
        String firstWord = expression.getText().split(" ")[0].toLowerCase();
        return Prepositions.isPrep(firstWord);
    };

    //returns true if the expression contains at least one noun
    public static Predicate<Expression> containsAtLeastOneNoun = (expression) -> {
        List<CoreLabel> tokens = annotator.annotate(expression.getText());
        for (CoreLabel token : tokens) {
            if (token.get(PartOfSpeechAnnotation.class).startsWith("N")) {
                return true;
            }
        }
        return false;
    };

    public static Predicate<Expression> createFilterForCategory(String category) {
        Predicate<Expression> catFilter = (expression) -> {
            return expression.getCategory().equals(category);
        };
        return catFilter;
    }

//	public static Function<ExpressionList, ExpressionList> inflectExpressions = (
//			list) -> {
//		List<Expression> multiWordExpressions = list.getExpressions()
//				.parallelStream().filter(isSingleWord)
//				.collect(Collectors.toList());
//		return new ExpressionList(multiWordExpressions);
//	};
//	public List<Expression> inflectExpression(Expression baseExpression) {
//		List<Expression> inflectedForms = new ArrayList<Expression>();
//		String original = baseExpression.getText();
//
//		String[] words = original.split(" ");
//		return inflectedForms;
//	}
}
