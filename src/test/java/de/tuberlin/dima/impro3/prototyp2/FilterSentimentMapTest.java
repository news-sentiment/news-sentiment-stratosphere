package de.tuberlin.dima.impro3.prototyp2;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp2.NewsPaperSentimentPact.FilterSentimentMap;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactDouble;
import eu.stratosphere.pact.common.type.base.PactInteger;
import eu.stratosphere.pact.common.type.base.PactString;

public class FilterSentimentMapTest {

	@Test
	public void testFilterSentimentMap() {

		ArrayList<String> newsPapers = new ArrayList<String>();
		ArrayList<String> namedEntities = new ArrayList<String>();
		ArrayList<Double> sentimentValues = new ArrayList<Double>();
		ArrayList<Integer> countValues = new ArrayList<Integer>();
		
		FilterSentimentMap mapper = new FilterSentimentMap();
		
		try {
			mapper.open(new Configuration());
			FilterSentimentMapCollector out = new FilterSentimentMapCollector(newsPapers, namedEntities, sentimentValues, countValues);
					
			PactString newsPaperNePair = new PactString();
				
			newsPaperNePair.setValue("Tagesspiegel\tSPD");
			
			PactRecord record = new PactRecord();
			record.setField(0, newsPaperNePair);
			record.setField(1, new PactDouble());
			record.setField(2, new PactInteger(9));
			
			PactRecord record2 = new PactRecord();
			record2.setField(0, newsPaperNePair);
			record2.setField(1, new PactDouble());
			record2.setField(2, new PactInteger(11));
			
			mapper.map(record, out);
			
			assertTrue("Count list must be empty", countValues.size() == 0);
			
			mapper.map(record2, out);
			
			assertTrue("PactRecord must be out collected", countValues.size() == 1);
			
			out.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private class FilterSentimentMapCollector implements Collector<PactRecord>{

		ArrayList<String> newsPapers;
		ArrayList<String> namedEntitiy;
		ArrayList<Double> sentimentValue;
		ArrayList<Integer> counts;

		public FilterSentimentMapCollector(ArrayList<String> newsPaper,
				ArrayList<String> namedEntitiy,
				ArrayList<Double> sentimentValue, ArrayList<Integer> counts) {
			this.newsPapers = newsPaper;
			this.namedEntitiy = namedEntitiy;
			this.sentimentValue = sentimentValue;
			this.counts = counts;
		}

		@Override
		public void close() {
		}

		@Override
		public void collect(PactRecord record) {
			PactString newsPaper = record.getField(0, PactString.class);
			PactString ne= record.getField(1, PactString.class);
			PactDouble sentimentValues = record.getField(2, PactDouble.class);
			PactInteger count = record.getField(3, PactInteger.class);
			newsPapers.add(newsPaper.getValue());
			namedEntitiy.add(ne.getValue());
			sentimentValue.add(sentimentValues.getValue());
			counts.add(count.getValue());
		}
		
	}
}
