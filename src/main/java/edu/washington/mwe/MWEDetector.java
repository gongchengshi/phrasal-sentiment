package edu.washington.mwe;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.washington.util.StanfordAnnotator;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MWEDetector {
    private StanfordAnnotator annotator;
    private IMWEDetector detector;

    public MWEDetector(File idxFile) {
        this("consecutive", idxFile);
    }

    public MWEDetector(String detectorType, File idxFile) {
        JWMEWrapper jwme = new JWMEWrapper(idxFile);
        detector = jwme.detectorFromName(detectorType);
        annotator = StanfordAnnotator.getInstance();
    }

    public List<IMWE<IToken>> detect(String text) {
        List<CoreLabel> tokens = annotator.annotate(text);
        List<IToken> sentence = tokens
                .parallelStream()
                .map((token) -> new Token(token.get(TextAnnotation.class),
                        token.get(PartOfSpeechAnnotation.class), token
                        .get(LemmaAnnotation.class)))
                .collect(Collectors.toList());
        List<IMWE<IToken>> mwes = detector.detect(sentence);
        return mwes;
    }
}
