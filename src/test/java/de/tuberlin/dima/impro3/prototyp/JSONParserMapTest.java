package de.tuberlin.dima.impro3.prototyp;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp.NewsPaperSentimentPact.JSONParserMap;

import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class JSONParserMapTest {
	
	@Test
	public void testJSONParserMap() throws Exception {
		
		ArrayList<String> newsTexts = new ArrayList<String>();
		ArrayList<String> newsPapers = new ArrayList<String>();
		
		JSONParserMap map = new JSONParserMap();
		map.open(new Configuration());
		SentenceSplitterMapCollector out = new SentenceSplitterMapCollector(newsTexts, newsPapers);
		
		Scanner scanner = new Scanner(new File("src/test/resources/1_newsdump_utf8.json"));
		
		while (scanner.hasNextLine()) {
			String jsonString = (String) scanner.nextLine();
			PactRecord record = new PactRecord(new PactString(jsonString));
			map.map(record, out);
		}
		
		assertTrue("should be right output count", newsPapers.size() == 1);
		assertTrue("should be right output count", newsTexts.size() == 1);
		
		assertTrue("should be right news paper", newsPapers.get(0).equals("Filmstarts.de"));
		
		scanner.close();
		
	}
	
	private class SentenceSplitterMapCollector implements Collector<PactRecord> {
		
		ArrayList<String> newsTexts;
		ArrayList<String> newsPapers;
		
		public SentenceSplitterMapCollector(ArrayList<String> sentences,
				ArrayList<String> newsPapers) {
			this.newsTexts = sentences;
			this.newsPapers = newsPapers;
		}

		@Override
		public void close() {
		}

		@Override
		public void collect(PactRecord record) {
			PactString newsPaper = record.getField(0, PactString.class);
			PactString sentence = record.getField(1, PactString.class);
			newsPapers.add(newsPaper.getValue());
			newsTexts.add(sentence.getValue());
		}
		
	}
	
	@Test
	public void testJsonParsing() throws FileNotFoundException, JSONException {
		
		Scanner scanner = new Scanner(new File("src/test/resources/1_newsdump_utf8.json"));
		
		if (scanner.hasNextLine()) {
			String jsonString = (String) scanner.nextLine();
			
			JSONObject bigJson = new JSONObject(jsonString);
			
			bigJson.getJSONObject("d");
		}
		
		scanner.close();

	}

}
