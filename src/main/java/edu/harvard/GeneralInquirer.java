package edu.harvard;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GeneralInquirer {
    // As of 5/12/2014 out of 11788 total words in the General Inquirer dictionary there are 4211 words with sentiment.
    private final Map<String, Double> termPolarity = new HashMap<>();

    public GeneralInquirer() throws IOException {
        initFromFile();
    }

    private void initFromFile() throws IOException {
        Path path = Paths.get("supplementary/inqtabs.txt");
        Scanner scanner = new Scanner(path).useDelimiter("\\t");
        scanner.nextLine();
        while (scanner.hasNext()){
            String entry = scanner.next(); // Entry
            scanner.next(); // Source
            String positiv = scanner.next(); // Positiv
            String negativ = scanner.next(); // Negativ
            String pstv = scanner.next(); // Pstv
            scanner.next(); //Affil
            String ngtv = scanner.next(); // Ngtv
            scanner.nextLine(); // rest of line

            Boolean pos = !(positiv.isEmpty() && pstv.isEmpty());
            Boolean neg = !(negativ.isEmpty() && ngtv.isEmpty());
            if(pos || neg) {
                String[] parts = entry.split("#");
                // Todo: take a weighted average of multiple definitions of identical terms
                // Right now only the most common definition is used.
                if(parts.length > 1 && !parts[1].equals("1")) {
                    continue;
                }
                termPolarity.put(parts[0].toLowerCase(), pos ? 1.0 : 0.0);
            }
        }
    }

    // Return value >= .5 should be considered positive < .5 is negative
    // Returns null if the term is not in the dictionary
    public Double getSentimentOfTerm(String term) {
        return termPolarity.get(term.toLowerCase());
    }

    public Double getSentimentOfPhrase(String phrase) {
        // Todo: implement better stemming
        String[] words = phrase.toLowerCase().split(" ");
        Double sum = 0.0;
        for(String word : words) {
            Double value = getSentimentOfTerm(word);
            if(value != null) {
                sum += value;
            }
        }
        return sum / words.length;
    }
}
