package edu.washington.mwe;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.washington.data.sentimentreebank.SentenceList;
import edu.washington.data.sentimentreebank.StanfordSentimentTreebankInfo;
import edu.washington.util.StanfordAnnotator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MWEDetector {

    private StanfordSentimentTreebankInfo sentimentTbInfo;
    private StanfordAnnotator annotator;
    private static MWEDetector self;

    private MWEDetector() {
        annotator = StanfordAnnotator.getInstance();
        sentimentTbInfo = new StanfordSentimentTreebankInfo(
                "supplementary/stanfordSentimentTreebank");
    }

    public static MWEDetector getInstance() {
        if (self == null) {
            self = new MWEDetector();
        }
        return self;
    }

    public Map<String, List<IMWE<IToken>>> detectFromTreebank(File idxFile) {
        int NUM_SENTENCES_IN_STANFORD = 11856;
        Map<String, List<IMWE<IToken>>> results = new HashMap<String, List<IMWE<IToken>>>();
        try {
            SentenceList sentences = new SentenceList(
                    sentimentTbInfo.DatasetSentencesPath,
                    NUM_SENTENCES_IN_STANFORD);
            for (int i = 1; i < NUM_SENTENCES_IN_STANFORD; i++) {
                String text = sentences.getSentence(i);
                List<IMWE<IToken>> mwes = detect(text, idxFile);

                results.put(text, mwes);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return results;
    }

    public List<IMWE<IToken>> detect(String text, File idxFile) {
        return detect(text, "consecutive", idxFile);
    }

    public List<IMWE<IToken>> detect(String text, String detectorType,
            File idxFile) {
        List<CoreLabel> tokens = annotator.annotate(text);
        List<IToken> sentence = tokens
                .parallelStream()
                .map((token) -> new Token(token.get(TextAnnotation.class),
                        token.get(PartOfSpeechAnnotation.class), token
                        .get(LemmaAnnotation.class)))
                .collect(Collectors.toList());
        JWMEWrapper jwme = new JWMEWrapper(idxFile);
        IMWEDetector detector = jwme.detectorFromName(detectorType);
        // run detector and print out results
        List<IMWE<IToken>> mwes = detector.detect(sentence);
        return mwes;
    }

}
