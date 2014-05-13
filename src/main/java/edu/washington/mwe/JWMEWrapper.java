package edu.washington.mwe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.mit.jmwe.detect.CompositeDetector;
import edu.mit.jmwe.detect.Consecutive;
import edu.mit.jmwe.detect.Exhaustive;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.detect.ProperNouns;
import edu.mit.jmwe.detect.StopWords;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.IndexBuilder;
import edu.mit.jmwe.index.MWEIndex;

public class JWMEWrapper {

    private IMWEIndex index;

    public IMWEDetector detectorFromName(String name) {
        // switch(name){
        if (name.equals("consecutive")) {
            return new Consecutive(index);
        } else if (name.equals("exhaustive")) {
            return new Exhaustive(index); // / StopWords
        } else if (name.equals("stopWords")) {
            return new StopWords(index);
        } else if (name.equals("properNouns")) {
            return ProperNouns.getInstance();
        } else if (name.equals("all")) {
            return new CompositeDetector(new Consecutive(index),
                    new Exhaustive(index), new StopWords(index),
                    ProperNouns.getInstance());
        } else {
            return null;
        }
        // }
    }

    public JWMEWrapper(File idxData) {
        try {
            index = new MWEIndex(idxData);
            index.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void makeNewIndexFile(String indexName, String newIndexFile,
            List<String> forms) {
        try {
            IMWEIndex newIndex = new MWEIndex(forms);
            newIndex.open();
            List<String> headerLines = new LinkedList<String>();
            headerLines.add("Index: " + indexName);
            headerLines.add("Generated on: " + new Date());
            IndexBuilder.writeDataFile(newIndex, new FileOutputStream(new File(
                    newIndexFile)), headerLines);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
