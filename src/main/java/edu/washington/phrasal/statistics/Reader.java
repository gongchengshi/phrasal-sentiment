package edu.washington.phrasal.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


public class Reader {

	Path simplePhrasalVerbsPath = Paths
			.get("phrasal_verb_investigation/base_phrasal_verbs.txt");
	Path scorePath = Paths
			.get("phrasal_verb_investigation/phrasal_verb_sentence_sentiment.txt");
	HashMap<String, Double> scores = new HashMap<String,Double>();

	

	public void load() {
		BiMap<Integer, String> indexedVerbs = HashBiMap.create();

		try {
			BufferedReader reader = Files.newBufferedReader(
					simplePhrasalVerbsPath, StandardCharsets.UTF_8);
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] datum = line.split("\t");
				indexedVerbs.put(Integer.parseInt(datum[0]), datum[1]);
			}
			reader.close();

			// read in scores
			reader = Files.newBufferedReader(scorePath, StandardCharsets.UTF_8);
			line = "";
			BiMap<Integer, String> idIndex = HashBiMap.create();
			while ((line = reader.readLine()) != null) {
				String[] datum = line.split("\t");
				if (!datum[1].contains("NA")) {
					int index = Integer.parseInt(datum[0]);
					String phrasalVerb = indexedVerbs.get(index);

					double score = Double.parseDouble(datum[1]);
					scores.put(phrasalVerb, score);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Reader v = new Reader();
		v.load();
		v.print();
	}

	public void print() {
		 for( Entry<String, Double> e :scores.entrySet()){
			 System.out.println(e);
		 }
	}
}
