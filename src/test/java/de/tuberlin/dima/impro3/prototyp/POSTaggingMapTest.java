package de.tuberlin.dima.impro3.prototyp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp.NewsPaperSentimentPact.POSTaggingMap;
import de.tuberlin.dima.impro3.tagger.TaggedSentence;
import de.tuberlin.dima.impro3.tagger.TaggedSentencesPact;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class POSTaggingMapTest {

	@Test
	public void testPOSTaggingMap() throws Exception {
		ArrayList<String> sentences = new ArrayList<String>();
		ArrayList<String> newsPapers = new ArrayList<String>();
		ArrayList<TaggedSentence> taggedSentences = new ArrayList<TaggedSentence>();

		POSTaggingMap map = new POSTaggingMap();
		map.open(new Configuration());
		POSTaggingMapCollector out = new POSTaggingMapCollector(sentences,
				newsPapers, taggedSentences);

		PactRecord record = new PactRecord();
		record.setField(0, new PactString("Tagesspiegel"));
		record.setField(1, new PactString(
				"Die SPD und die CDU haben die Wahl verloren, weil sie schlechte Verlierer sind."));
		map.map(record, out);
		
		assertTrue("should be right news paper", newsPapers.get(0).equals("Tagesspiegel"));

		assertTrue("should be right count of tokens", taggedSentences.get(0).getTokens().length == 16);
		
		out.close();

	}

}

class POSTaggingMapCollector implements Collector<PactRecord> {

	ArrayList<String> sentences;
	ArrayList<String> newsPapers;
	ArrayList<TaggedSentence> taggedSentences;

	public POSTaggingMapCollector(ArrayList<String> sentences,
			ArrayList<String> newsPapers,
			ArrayList<TaggedSentence> taggedSentences) {
		this.sentences = sentences;
		this.newsPapers = newsPapers;
		this.taggedSentences = taggedSentences;
	}

	@Override
	public void close() {
	}

	@Override
	public void collect(PactRecord record) {
		PactString newsPaper = record.getField(0, PactString.class);
		PactString sentence = record.getField(1, PactString.class);
		TaggedSentencesPact taggedSentence = record.getField(2,
				TaggedSentencesPact.class);
		newsPapers.add(newsPaper.getValue());
		sentences.add(sentence.getValue());
		taggedSentences.add(taggedSentence.getTaggedSentence());
	}

}
