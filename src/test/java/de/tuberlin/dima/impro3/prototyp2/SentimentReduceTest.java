package de.tuberlin.dima.impro3.prototyp2;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp2.NewsPaperSentimentPact.SentimentReduce;
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
		
			PactString newsPaperNePair = new PactString();
				
			newsPaperNePair.setValue("Tagesspiegel\tSPD");
			
			double [] sentimentValues = {1.7,-2.5,3.3,-4.4,5.6,-6.3,7.6,-8.4,9.6,10.5};
			
			for (int i = 0; i < sentimentValues.length; i++) {
				PactRecord record = new PactRecord();
				record.setField(0, newsPaperNePair);
				record.setField(1, new PactDouble(sentimentValues[i]));
				records.add(record);
			}
			
			Iterator<PactRecord> iterator = records.iterator();
			reducer.reduce(iterator, out);
			
			assertTrue("the average should be 5.5 ", averages.get(0) == 1.67d);
			
			assertTrue("count should be 10", counts.get(0) == 10);
			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private class SentimentReduceCollector implements Collector<PactRecord>{
		
		ArrayList<String> newsPapers;
		ArrayList<String> namedEntitiy;
		ArrayList<Double> average;
		ArrayList<Integer> count;
		
		public SentimentReduceCollector(ArrayList<String> newsPaper,
				ArrayList<String> namedEntitiy, ArrayList<Double> average,
				ArrayList<Integer> count) {
			super();
			this.newsPapers = newsPaper;
			this.namedEntitiy = namedEntitiy;
			this.average = average;
			this.count = count;
		}

		@Override
		public void close() {
		}

		@Override
		public void collect(PactRecord record) {
			PactString newsPaperNePair = record.getField(0, PactString.class);
			PactDouble averages = record.getField(1, PactDouble.class);
			PactInteger counts = record.getField(2, PactInteger.class);
			String newsPaper = newsPaperNePair.getValue().split("\t")[0];
			String ne = newsPaperNePair.getValue().split("\t")[1];
			newsPapers.add(newsPaper);
			namedEntitiy.add(ne);
			average.add(averages.getValue());
			count.add(counts.getValue());
		}
		
	}

}
