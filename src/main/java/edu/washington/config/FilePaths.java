package edu.washington.config;


import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePaths {

    //Wordlists
    public static final Path baseWordlist = Paths
            .get("supplementary/base_expression_list.txt");
    //C:\Users\Kaufmann\workspace\test\
    public static final Path mweIndex = Paths
            .get("supplementary/mwe/single.semcor");
    public static final Path wikipediaFigSemcorFile = Paths
            .get("supplementary/mwe/best.semcor");
    public static final StanfordSentimentTreebankInfo SSTPaths =
            new StanfordSentimentTreebankInfo("supplementary/stanfordSentimentTreebank");
}
