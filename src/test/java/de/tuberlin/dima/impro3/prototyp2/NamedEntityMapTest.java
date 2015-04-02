package de.tuberlin.dima.impro3.prototyp2;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp2.NewsPaperSentimentPact.NamedEntityMap;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactDouble;
import eu.stratosphere.pact.common.type.base.PactString;

public class NamedEntityMapTest {

	@Test
	public void testSentimentMap() {

		ArrayList<String> newsPapers = new ArrayList<String>();
		ArrayList<String> namedEntities = new ArrayList<String>();
		ArrayList<Double> sentimentValues = new ArrayList<Double>(); 
		
		NamedEntityMap mapper = new NamedEntityMap();
		try {
			
			mapper.open(new Configuration());
			NamedEntityMapCollector out = new NamedEntityMapCollector(newsPapers, namedEntities, sentimentValues);
						
			PactRecord record = new PactRecord();
			record.setField(0, new PactString("Tagesspiegel"));
			record.setField(1, new PactString("Die SPD und die CDU haben die Wahl verloren."));
			record.setField(2, new PactDouble(1));
			
			mapper.map(record, out);
						
			assertTrue("should be right count of named entities", namedEntities.size() == 2);
			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private class NamedEntityMapCollector implements Collector<PactRecord> {
		
		ArrayList<String> newsPapers;
		ArrayList<String> namedEntities;
		ArrayList<Double> sentimentValues; 

		public NamedEntityMapCollector(ArrayList<String> newsPapers,
				ArrayList<String> namedEntities,
				ArrayList<Double> sentimentValues) {
			this.newsPapers = newsPapers;
			this.namedEntities = namedEntities;
			this.sentimentValues = sentimentValues;
		}

		@Override
		public void close() {
		}

		@Override
		public void collect(PactRecord record) {
			PactString newsPaperNePair = record.getField(0, PactString.class);
			PactDouble sentimentValue = record.getField(1, PactDouble.class);
			String newsPaper = newsPaperNePair.getValue().split("\t")[0];
			String ne = newsPaperNePair.getValue().split("\t")[1];
			newsPapers.add(newsPaper);
			namedEntities.add(ne);
			sentimentValues.add(sentimentValue.getValue());
		}
		
	}

}
