/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.washington.phrasal.feature;

import com.google.common.base.Joiner;

import edu.stanford.nlp.ie.machinereading.common.SimpleTokenize;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author nickchen
 */
public class SentenceIdPhrasalVerbId {

    private final Integer sentence_id;
    private final String sentence;
    private final Integer phrasal_verb_id;
    private final FeatureGenerator fg;
    private final List<String> sentenceTokens;
    private final List<String> phrasalVerbTokens;
    private final List<String> sentencePOS;
    private final List<String> phraseVerbPOS;
    private final List<Integer> characterOffsets;
    public final Integer pvStartIndex;
    public final Integer pvEndIndex;

    public SentenceIdPhrasalVerbId(Integer sentence_id, Integer phrasal_verb_id, FeatureGenerator fg) {
        this.sentence_id = sentence_id;
        this.sentence = fg.sentenceList.getSentence(this.sentence_id);
        this.phrasal_verb_id = phrasal_verb_id;
        this.fg = fg;
        this.sentenceTokens = SimpleTokenize.tokenize(sentence);
        this.phrasalVerbTokens = SimpleTokenize.tokenize(fg.getPhrasalVerbById(phrasal_verb_id));

        sentencePOS = tagWordTokens(sentenceTokens);
        phraseVerbPOS = tagWordTokens(phrasalVerbTokens);

        /* start index of phrase in sentence, first occurance */
        pvStartIndex = Collections.indexOfSubList(sentenceTokens, phrasalVerbTokens);
        /* end index of phrase in sentence */
        pvEndIndex = pvStartIndex + phrasalVerbTokens.size();
        
        /*Compute character based offsets*/
        characterOffsets = new ArrayList<Integer>();
		int offset = 0;
		for(String token : sentenceTokens){
			characterOffsets.add(offset);
			offset = offset + token.length() + 1;
		}
    }

    private List<String> tagWordTokens(List<String> tokens) {
        List<HasWord> hw = tokens.stream().map((t) -> new Word(t)).collect(Collectors.toList());
        /* convert tag back to string */
        return fg.getTagger().apply(hw).stream().map((pos) -> pos.tag()).collect(Collectors.toList());
    }

    public Integer getPhrasalVerbId() {
        return phrasal_verb_id;
    }

    public List<String> getPhrasalVerbTokens() {
        return phrasalVerbTokens;
    }

    public Integer getSentenceId() {
        return sentence_id;
    }

    public FeatureGenerator getFG() {
        return this.fg;
    }

    public List<String> getSentenceTokens() {
        return sentenceTokens;
    }

    public List<String> getSentencePOS() {
        return sentencePOS;
    }

    public List<String> getPhraseVerbPOS() {
        return phraseVerbPOS;
    }

	public String getSentence() {
		return sentence;
	}

	public List<Integer> getCharacterOffsets() {
		return characterOffsets;
	}

}
