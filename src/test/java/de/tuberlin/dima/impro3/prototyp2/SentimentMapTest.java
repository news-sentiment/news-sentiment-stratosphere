package de.tuberlin.dima.impro3.prototyp2;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp2.NewsPaperSentimentPact.SentimentMap;
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
				
		PactRecord record = new PactRecord();
		record.setField(0, new PactString("Tagesspiegel"));
		record.setField(1, new PactString("Die SPD und die CDU haben die Wahl verloren, weil sie schlechte Verlierer sind."));
		
		map.map(record, out);
		
		assertTrue("should have the right sentiment value", sentimentList.get(0) == -0.5573500394821167f);
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
			double d1 = record.getField(2, PactDouble.class).getValue();
			sentimentList.add(d1);			
		}
		
	}
	
}
