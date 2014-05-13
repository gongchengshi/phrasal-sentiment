package edu.washington.phrasal;

import edu.washington.data.sentimentreebank.PhraseIdDict;
import edu.washington.data.sentimentreebank.PhraseIdSentimentList;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.data.sentimentreebank.StanfordSentimentTreebankInfo;
import edu.washington.util.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

public class PhrasalVerbsInvestigation {
	private final Path simplePhrasalVerbsPath = Paths
			.get("phrasal_verb_investigation/base_phrasal_verbs.txt");
	private final StanfordSentimentTreebankInfo sentimentTbInfo = new StanfordSentimentTreebankInfo(
			"supplementary/stanfordSentimentTreebank");
	private PhraseIdDict phraseIds;
	private PhraseIdSentimentList phraseIdSentiments;
	private SentenceList sentences;
	private ArrayList<List<Integer>> phrasalVerbSentences;

	public static void main(String[] args) {
		PhrasalVerbsInvestigation i = new PhrasalVerbsInvestigation();
		i.init();
		i.printSentimentScores();
	}

	public void init() {

		try {
			ExtractAndWriteBasePhrasalVerbs(simplePhrasalVerbsPath);

			List<String> simplePhrasalVerbs = new ArrayList<>();
			BufferedReader reader = Files.newBufferedReader(
					simplePhrasalVerbsPath, StandardCharsets.UTF_8);
			String line;
			while ((line = reader.readLine()) != null) {
				simplePhrasalVerbs.add(line.split("\\t")[1]);
			}

			phraseIds = new PhraseIdDict(sentimentTbInfo.DictionaryPath);
			phraseIdSentiments = new PhraseIdSentimentList(
					sentimentTbInfo.SentimentLabelsPath, 239233);
			sentences = new SentenceList(sentimentTbInfo.DatasetSentencesPath,
					11856);
			phrasalVerbSentences = BuildPhraseVerbSentencesMap(
					simplePhrasalVerbs, sentences);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printAverageStatistics() {
		List<Double> aveSentOfVerbalPhraseSentences = new ArrayList<>();
		for (List<Integer> verbPhraseSentences : phrasalVerbSentences) {
			if (verbPhraseSentences.isEmpty()) {
				aveSentOfVerbalPhraseSentences.add(null);
				continue;
			}
			Double aveSentiment = 0.0;

			for (Integer sentenceId : verbPhraseSentences) {
				String sentence = sentences.sentenceList.get(sentenceId);
				Integer phraseId = phraseIds.getPhraseId(sentence, true);
				Double sentimentScore = null;
				try {
					sentimentScore = phraseIdSentiments.sentimentList.get(phraseId);
					aveSentiment += SentimentScoreToClass(sentimentScore);
				} catch (Exception ex) {
										
				}
			}

			aveSentiment /= (double) verbPhraseSentences.size();
			aveSentOfVerbalPhraseSentences.add(aveSentiment);
		}

		WriteAverageSentimentOfVerbalPhraseSentences(
				aveSentOfVerbalPhraseSentences,
				"phrasal_verb_investigation/phrasal_verb_sentence_sentiment.txt");
	}

	public void printSentimentScores() {
		Map<Integer, List<Integer>> indexedScores = new HashMap<>();
		for (int i = 0; i < phrasalVerbSentences.size(); i++) {
			List<Integer> verbPhraseSentences = phrasalVerbSentences.get(i);
			List<Integer> scoresForTerm = new ArrayList<>();
			for (Integer verbPhraseSentence : verbPhraseSentences) {
				String sentence = sentences.sentenceList.get(verbPhraseSentence);
				Integer phraseId = phraseIds.getPhraseId(sentence, true);
				Double sentimentScore = null;
				try {
					sentimentScore = phraseIdSentiments.sentimentList.get(phraseId);
					scoresForTerm.add(SentimentScoreToClass(sentimentScore));
				} catch (Exception ex) {
				}
			}
			if (!scoresForTerm.isEmpty())
				indexedScores.put(i, scoresForTerm);
		}

		writeSentimentScoresOfSentences(indexedScores,
				"phrasal_verb_investigation/phrasal_verb_sentence_sentiment_scores.txt");
	}

	private static void ExtractAndWriteBasePhrasalVerbs(Path outPath) {
		try {
			Path wikipediaPhrasalVerbsPath = Paths
					.get("supplementary/phrasal_verb_lists/wikipedia_phrasal_verbs.txt");
			Path usingEnglishPhrasalVerbsPath = Paths
					.get("supplementary/phrasal_verb_lists/usingenglish_dot_com_phrasal_verbs.txt");

			SortedSet<String> simplePhrasalVerbs = new TreeSet<>(
					Utils.GetTwoWordLines(wikipediaPhrasalVerbsPath));
			simplePhrasalVerbs.addAll(Utils
					.GetTwoWordLines(usingEnglishPhrasalVerbsPath));

			WriteSimplePhrasalVerbsFile(simplePhrasalVerbs, outPath.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void WriteAverageSentimentOfVerbalPhraseSentences(
			List<Double> values, String outPath) {
		try {
                    try (PrintWriter writer = new PrintWriter(outPath)) {
                        int i = 0;
                        for (Double value : values) {
                            writer.println(i++ + "\t" + (value == null ? "NA" : value));
                        }
                    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeSentimentScoresOfSentences(
			Map<Integer, List<Integer>> indexedScores, String outPath) {
		try {
                    try (PrintWriter writer = new PrintWriter(outPath)) {
                        for (Entry<Integer, List<Integer>> indexedScore : indexedScores
                                .entrySet()) {
                            List<Integer> scores = indexedScore.getValue();
                            writer.print(indexedScore.getKey() + "\t");
                            if (scores.size() > 0)
                                writer.print(scores.get(0));
                            for (int i = 1; i < scores.size(); i++) {
                                writer.print("," + scores.get(i));
                            }
                            writer.println();
                            
                        }
                        writer.flush();
                    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void WriteSimplePhrasalVerbsFile(
			Collection<String> simplePhrasalVerbs, String outPath)
			throws FileNotFoundException {
            try (PrintWriter writer = new PrintWriter(outPath)) {
                int i = 0;
                for (String item : simplePhrasalVerbs) {
                    writer.println(i++ + "\t" + item);
                }
            }
	}

	public static int SentimentScoreToClass(Double value) {
		if (value <= .2) {
			return 1;
		}
		if (value <= .4) {
			return 2;
		}
		if (value <= .6) {
			return 3;
		}
		if (value <= .8) {
			return 4;
		}
		if (value <= 1) {
			return 5;
		}

		throw new IllegalArgumentException("value must be between 0 and 1");
	}

	public static ArrayList<List<Integer>> BuildPhraseVerbSentencesMap(
			List<String> simplePhrasalVerbs, SentenceList sentences) {
		ArrayList<List<Integer>> phrasalVerbSentences = new ArrayList<>();
		try {
			for (String phrasalVerb : simplePhrasalVerbs) {
				phrasalVerbSentences.add(sentences
						.findSentencesWithPhrase(phrasalVerb));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return phrasalVerbSentences;
	}
}
