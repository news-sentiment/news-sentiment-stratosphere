package de.tuberlin.dima.impro3.prototyp2;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp2.NewsPaperSentimentPact.SentenceSplitterMap;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class SentenceSplitterMapTest {
	
	@Test
	public void testSentenceSplitterMap() throws Exception {
		ArrayList<String> sentences = new ArrayList<String>();
		ArrayList<String> newsPapers = new ArrayList<String>();
		
		SentenceSplitterMap map = new SentenceSplitterMap();
		map.open(new Configuration());
		SentenceSplitterMapCollector out = new SentenceSplitterMapCollector(sentences, newsPapers);
		
		Scanner scanner = new Scanner(new File("src/test/resources/1_newsdump_uft8.txt"));
		
		while (scanner.hasNextLine()) {
			String jsonString = (String) scanner.nextLine();
			PactRecord record = new PactRecord();
			record.setField(0, new PactString("Filmstarts.de"));
			record.setField(1, new PactString(jsonString));
			map.map(record, out);
		}
		
		for (String newsPaper : newsPapers) {
			assertTrue("should be right news paper", newsPaper.equals("Filmstarts.de"));
		}
		
		assertTrue("should be right count of sentences", sentences.size() == 15);
		
		String firstSentence = "\"Kick-Ass\"-Comic-Autor Mark Millar war vor Ort beim Dreh von \"Kick-Ass 2\" dabei";
		String lastSentence = "Beitrag von: Leif Höfler Erfahre mehr:  Nicolas Cage , John Leguizamo , Jeff Wadlow , Chloë Moretz , Mark Millar , Christopher Mintz-Plasse , Kick-Ass , Kick-Ass 2";		
		
		assertTrue(sentences.get(0).equals(firstSentence));
		assertTrue(sentences.get(14).equals(lastSentence));
		
		scanner.close();
		out.close();
	}
	
	private class SentenceSplitterMapCollector implements Collector<PactRecord> {
		
		ArrayList<String> sentences;
		ArrayList<String> newsPapers;
		
		public SentenceSplitterMapCollector(ArrayList<String> sentences,
				ArrayList<String> newsPapers) {
			this.sentences = sentences;
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
			sentences.add(sentence.getValue());
		}
		
	}

}
