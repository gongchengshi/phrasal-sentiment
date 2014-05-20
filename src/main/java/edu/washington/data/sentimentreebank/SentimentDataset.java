package edu.washington.data.sentimentreebank;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.PTBEscapingProcessor;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CollectionUtils;
import edu.stanford.nlp.util.Function;
import edu.stanford.nlp.util.Generics;
import edu.washington.config.FilePaths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static edu.stanford.nlp.sentiment.ReadSentimentDataset.convertTree;

public class SentimentDataset {
    public static final Map<Integer, List<Integer>> phrasesInSentences;

    static final Function<Tree, String> TRANSFORM_TREE_TO_WORD = new Function<Tree, String>() {
        public String apply(Tree tree) {
            return tree.label().value();
        }
    };

    static final Function<String, String> TRANSFORM_PARENS = new Function<String, String>() {
        public String apply(String word) {
            if (word.equals("(")) {
                return "-LRB-";
            }
            if (word.equals(")")) {
                return "-RRB-";
            }
            return word;
        }
    };

    public static Integer getPhraseId(List<String> words) {
        // First we look for a copy of the phrase with -LRB- -RRB-
        // instead of ().  The sentiment trees sometimes have both, and
        // the escaped versions seem to have more reasonable scores.
        // If a particular phrase doesn't have -LRB- -RRB- we fall back
        // to the unescaped versions.
        Integer phraseId = phraseIds.get(CollectionUtils.transformAsList(words, TRANSFORM_PARENS));
        if (phraseId == null) {
            phraseId = phraseIds.get(words);
        }

        return phraseId;
    }

    public static Integer getLowerCasePhraseId(List<String> words) {
        // First we look for a copy of the phrase with -LRB- -RRB-
        // instead of ().  The sentiment trees sometimes have both, and
        // the escaped versions seem to have more reasonable scores.
        // If a particular phrase doesn't have -LRB- -RRB- we fall back
        // to the unescaped versions.
        Integer phraseId = phraseIds.get(CollectionUtils.transformAsList(words, TRANSFORM_PARENS));
        if (phraseId == null) {
            phraseId = lowerCasePhraseIds.get(words);
        }

        return phraseId;
    }

    public static List<Integer> getPhrasesInSentence(List<Integer> parentPointers, List<String> sentence, Map<List<String>, Integer> phraseIds) {
        int maxNode = 0;
        for (Integer parent : parentPointers) {
            maxNode = Math.max(maxNode, parent);
        }

        Tree[] subtrees = new Tree[maxNode + 1];
        for (int i = 0; i < sentence.size(); ++i) {
            CoreLabel word = new CoreLabel();
            word.setValue(sentence.get(i));
            Tree leaf = new LabeledScoredTreeNode(word);
            subtrees[i] = new LabeledScoredTreeNode(new CoreLabel());
            subtrees[i].addChild(leaf);
        }

        for (int i = sentence.size(); i <= maxNode; ++i) {
            subtrees[i] = new LabeledScoredTreeNode(new CoreLabel());
        }

        boolean[] connected = new boolean[maxNode + 1];
        Tree root = null;
        for (int index = 0; index < parentPointers.size(); ++index) {
            if (parentPointers.get(index) == -1) {
                if (root != null) {
                    throw new RuntimeException("Found two roots for sentence " + sentence);
                }
                root = subtrees[index];
            } else {
                connect(parentPointers, subtrees, connected, index);
            }
        }

        List<Integer> phraseIdsInSentence = Generics.newArrayList();
        for (int i = 0; i <= maxNode; ++i) {
            List<Tree> leaves = subtrees[i].getLeaves();
            List<String> words = CollectionUtils.transformAsList(leaves, TRANSFORM_TREE_TO_WORD);
            Integer phraseId = getPhraseId(words);
            if (phraseId == null) {
                throw new RuntimeException("Could not find phrase id for phrase " + sentence);
            }
            phraseIdsInSentence.add(phraseId);
        }

        return phraseIdsInSentence;
    }

    private static void connect(List<Integer> parentPointers, Tree[] subtrees, boolean[] connected, int index) {
        if (connected[index]) {
            return;
        }
        if (parentPointers.get(index) < 0) {
            return;
        }
        subtrees[parentPointers.get(index)].addChild(subtrees[index]);
        connected[index] = true;
        connect(parentPointers, subtrees, connected, parentPointers.get(index));
    }

    public static final List<List<String>> sentences;
    public static final Map<List<String>, Integer> phraseIds;
    public static final Map<List<String>, Integer> lowerCasePhraseIds;
    public static final Map<Integer, List<String>> idPhraseMap;
    public static final Map<Integer, List<String>> idLowerCasePhraseMap;
    public static final Map<Integer, Double> sentimentScores;
    public static final List<Tree> trees;


    static {
        String dictionaryFilename = FilePaths.SSTPaths.DictionaryPath.toString();
        String sentimentFilename = FilePaths.SSTPaths.SentimentLabelsPath.toString();
        String tokensFilename = FilePaths.SSTPaths.SOStrPath.toString();
        String parseFilename = FilePaths.SSTPaths.STreePath.toString();

        // Sentence file is formatted
        //   w1|w2|w3...
        sentences = Generics.newArrayList();
        for (String line : IOUtils.readLines(tokensFilename, "utf-8")) {
            String[] sentence = line.split("\\|");
            sentences.add(Arrays.asList(sentence));
        }

        // Split and read the phrase ids file.  This file is in the format
        //   w1 w2 w3 ... | id
        phraseIds = Generics.newHashMap();
        idPhraseMap = Generics.newHashMap();
        lowerCasePhraseIds = Generics.newHashMap();
        idLowerCasePhraseMap = Generics.newHashMap();
        for (String line : IOUtils.readLines(dictionaryFilename, "utf-8")) {
            String[] pieces = line.split("\\|");
            String[] sentence = pieces[0].split(" ");
            Integer id = Integer.valueOf(pieces[1]);
            List<String> phrase = Arrays.asList(sentence);
            phraseIds.put(phrase, id);
            idPhraseMap.put(id, phrase);

            List<String> lowercasePhrase = new ArrayList<>(phrase.size());
            for(String token: phrase) {
                lowercasePhrase.add(token.toLowerCase());
            }

            lowerCasePhraseIds.put(lowercasePhrase, id);
            idLowerCasePhraseMap.put(id, lowercasePhrase);
        }

        // Split and read the sentiment scores file.  Each line of this
        // file is of the format:
        //   phrasenum | score
        sentimentScores = Generics.newHashMap();
        for (String line : IOUtils.readLines(sentimentFilename, "utf-8")) {
            if (line.startsWith("phrase")) {
                continue;
            }
            String[] pieces = line.split("\\|");
            Integer id = Integer.valueOf(pieces[0]);
            Double score = Double.valueOf(pieces[1]);
            sentimentScores.put(id, score);
        }

        // Read lines from the tree structure file.  This is a file of parent pointers for each tree.
        int index = 0;
        PTBEscapingProcessor escaper = new PTBEscapingProcessor();
        trees = Generics.newArrayList();
        phrasesInSentences = Generics.newHashMap();
        for (String line : IOUtils.readLines(parseFilename, "utf-8")) {
            String[] pieces = line.split("\\|");
            List<Integer> parentPointers = CollectionUtils.transformAsList(Arrays.asList(pieces), new Function<String, Integer>() {
                public Integer apply(String arg) {
                    return Integer.valueOf(arg) - 1;
                }
            });
            Tree tree = convertTree(parentPointers, sentences.get(index), phraseIds, sentimentScores, escaper);
            phrasesInSentences.put(index, getPhrasesInSentence(parentPointers, sentences.get(index), phraseIds));

            ++index;
            trees.add(tree);
        }
    }
}
