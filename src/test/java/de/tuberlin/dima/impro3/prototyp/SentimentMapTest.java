package de.tuberlin.dima.impro3.prototyp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp.NewsPaperSentimentPact.SentimentMap;
import de.tuberlin.dima.impro3.tagger.TaggedSentence;
import de.tuberlin.dima.impro3.tagger.TaggedSentencesPact;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactDouble;
import eu.stratosphere.pact.common.type.base.PactString;

public class SentimentMapTest {
	
	@Test
	public void testSentimentMap() throws Exception {
		
		SentimentMap map = new SentimentMap();
		map.open(new Configuration());
		
		ArrayList<Double> sentimentList = new ArrayList<Double>();
		
		SentimentMapCollector out = new SentimentMapCollector(sentimentList);
		
		String [] tokens = {"Die", "SPD", "und", "die", "CDU", "und", "die", "CDU", "CSU", "sind", "verlogene", "Lustmolche", "."};
		String [] ne = {"O", "I-ORG", "O", "O", "I-ORG", "O", "O", "I-ORG", "I-ORG", "O", "O", "O", "O"};
		String [] tags = {"ART", "NE", "KON", "ART", "NE", "KON", "ART", "NE", "NE", "VAFIN", "ADJA", "NN", "$."};

		TaggedSentence taggedSentence = new TaggedSentence(tokens, tags, ne);
		
		PactRecord record = new PactRecord();
		record.setField(0, new PactString("Tagesspiegel"));
		record.setField(1, new PactString("Test Satz."));
		record.setField(2, new TaggedSentencesPact(taggedSentence));
		
		map.map(record, out);
		
		assertTrue("should have the right sentiment value", sentimentList.get(0) == -0.3355f);
	}
	
	private class SentimentMapCollector implements Collector<PactRecord> {
		
		ArrayList<Double> sentimentList;
		
		public SentimentMapCollector(ArrayList<Double> sentimentList) {
			super();
			this.sentimentList = sentimentList;
		}

		@Override
		public void close() {			
		}

		@Override
		public void collect(PactRecord record) {
			double d1 = record.getField(3, PactDouble.class).getValue();
			sentimentList.add(d1);			
		}
		
	}
	
}
