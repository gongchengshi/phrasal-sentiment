package edu.washington.wordlists;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import edu.washington.config.FilePaths;

public class ExpressionList {

	private List<Expression> expressions = new ArrayList<Expression>();

	public List<Expression> getExpressions() {
		return expressions;
	}

	public ExpressionList(List<Expression> expressions) {
		this.expressions = expressions;
	}
	

	public ExpressionList(Path listPath) {
		load(listPath);
	}

	private  void load(Path list) {
		try {
			BufferedReader b = Files.newBufferedReader(list,
					StandardCharsets.UTF_8);
			String line = b.readLine();
			String currentCategory = lineToCategory.apply(line);
			List<String> wordsForCategory = new ArrayList<String>();

			while ((line = b.readLine()) != null) {
				if (line.startsWith("Category:")) {
					for (String word : wordsForCategory) {
						expressions.add(new Expression(word, currentCategory));
					}
					currentCategory = lineToCategory.apply(line);
				} else {
					// "	Adam's apple" --> "Adam's apple"
					line = line.replaceAll("\t", "");
					wordsForCategory.add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Function<String, String> lineToCategory = (line) -> {
		line = line.replaceAll("Category:", "").replaceAll("_", " ");
		return line;
	};

	public static void main(String[] args) {
		ExpressionList e = new ExpressionList(FilePaths.baseWordlist);
	}
}
