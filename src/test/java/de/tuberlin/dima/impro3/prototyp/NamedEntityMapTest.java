package de.tuberlin.dima.impro3.prototyp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp.NewsPaperSentimentPact.NamedEntityMap;
import de.tuberlin.dima.impro3.tagger.TaggedSentence;
import de.tuberlin.dima.impro3.tagger.TaggedSentencesPact;
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
			
			String [] tokens = {"Die", "SPD", "und", "die", "CDU", "und", "die", "CDU", "CSU", "sind", "deutsche", "Parteien", "."};
			String [] ne = {"O", "I-ORG", "O", "O", "I-ORG", "O", "O", "I-ORG", "I-ORG", "O", "O", "O", "O"};
			String [] tags = {"ART", "NE", "KON", "ART", "NE", "KON", "ART", "NE", "NE", "VAFIN", "ADJA", "NN", "$."};
			
			TaggedSentence taggedSentence = new TaggedSentence(tokens, tags, ne);
			
			PactRecord record = new PactRecord();
			record.setField(0, new PactString("Tagesspiegel"));
			record.setField(1, new PactString("Die SPD und die CDU und die CDU CSU sind verlogene Lustmolche."));
			record.setField(2, new TaggedSentencesPact(taggedSentence));
			record.setField(3, new PactDouble(1));
			
			mapper.map(record, out);
						
			assertTrue("should be right count of named entities", namedEntities.size() == 3);
			
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
