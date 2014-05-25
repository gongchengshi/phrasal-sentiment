package edu.washington.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.machinereading.common.SimpleTokenize;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordAnnotator {

	protected StanfordCoreNLP pipeline;

	private static StanfordAnnotator self;

	public static StanfordAnnotator getInstance() {
		if (self == null) {
			self = new StanfordAnnotator();
		}
		return self;
	}

	private StanfordAnnotator() {
		// Create StanfordCoreNLP object properties, with POS tagging
		// (required for lemmatization), and lemmatization
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma,parse");

		// StanfordCoreNLP loads a lot of models, so you probably
		// only want to do this once per execution
		this.pipeline = new StanfordCoreNLP(props);
	}

	public List<String> lemmatize(String documentText) {
		List<String> lemmas = new LinkedList<>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		this.pipeline.annotate(document);

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				lemmas.add(token.get(LemmaAnnotation.class));
			}
		}

		return lemmas;
	}

	public List<CoreLabel> annotate(String documentText) {
		List<CoreLabel> lemmas = new LinkedList<>();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);

		// run all Annotators on this text
		this.pipeline.annotate(document);

		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				lemmas.add(token);
			}
		}

		return lemmas;
	}

	public Tree getTree(String text) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		CoreMap annotatedSentence = document.get(SentencesAnnotation.class)
				.get(0);
		SemanticGraph dependecies = annotatedSentence
				.get(CollapsedCCProcessedDependenciesAnnotation.class);
		Tree tree = annotatedSentence.get(TreeAnnotation.class);
		return tree;

	}

	public SemanticGraph getDeps(String text) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		CoreMap annotatedSentence = document.get(SentencesAnnotation.class)
				.get(0);
		SemanticGraph dependecies = annotatedSentence
				.get(CollapsedCCProcessedDependenciesAnnotation.class);
		return dependecies;
		// Tree tree = annotatedSentence.get(TreeAnnotation.class);

	}

	
}
