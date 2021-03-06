package de.tuberlin.dima.impro3.prototyp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp.NewsPaperSentimentPact.SentimentReduce;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactDouble;
import eu.stratosphere.pact.common.type.base.PactInteger;
import eu.stratosphere.pact.common.type.base.PactString;

public class SentimentReduceTest {

	@Test
	public void testSentimentMap() {

		ArrayList<String> newsPapers = new ArrayList<String>();
		ArrayList<String> namedEntities = new ArrayList<String>();
		ArrayList<Double> averages = new ArrayList<Double>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		SentimentReduce reducer = new SentimentReduce();
		
		try {
			reducer.open(new Configuration());
			SentimentReduceCollector out = new SentimentReduceCollector(newsPapers, namedEntities, averages, counts);
			
			ArrayList<PactRecord> records = new ArrayList<PactRecord>();
		
			PactString newsPaperNePair = new PactString("Tagesspiegel \t SPD");
							
			double [] sentimentValues = {1,2,3,4,5,6,7,8,9,10};
			
			for (int i = 0; i < sentimentValues.length; i++) {
				PactRecord record = new PactRecord();
				record.setField(0, newsPaperNePair);
				record.setField(1, new PactDouble(sentimentValues[i]));
				records.add(record);
			}
			
			Iterator<PactRecord> iterator = records.iterator();
			reducer.reduce(iterator, out);
						
			assertTrue("the average should be 5.5 ", averages.get(0) == 5.5);
			
			assertTrue("count should be 10", counts.get(0) == 10);

			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private class SentimentReduceCollector implements Collector<PactRecord>{

		ArrayList<String> newsPapers;
		ArrayList<String> namedEntities;
		ArrayList<Double> averages;
		ArrayList<Integer> counts;
		
		public SentimentReduceCollector(ArrayList<String> newsPaper,
				ArrayList<String> namedEntitiy, ArrayList<Double> average,
				ArrayList<Integer> count) {
			this.newsPapers = newsPaper;
			this.namedEntities = namedEntitiy;
			this.averages = average;
			this.counts = count;
		}

		@Override
		public void close() {
		}

		@Override
		public void collect(PactRecord record) {
			PactString newsPaperNePair = record.getField(0, PactString.class);
			PactDouble average = record.getField(1, PactDouble.class);
			PactInteger count = record.getField(2, PactInteger.class);
			String newsPaper = newsPaperNePair.getValue().split("\t")[0];
			String ne = newsPaperNePair.getValue().split("\t")[1];
			newsPapers.add(newsPaper);
			namedEntities.add(ne);
			averages.add(average.getValue());
			counts.add(count.getValue());
		}
		
	}

}
