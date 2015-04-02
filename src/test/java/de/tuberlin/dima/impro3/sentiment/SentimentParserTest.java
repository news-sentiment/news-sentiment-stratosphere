package de.tuberlin.dima.impro3.sentiment;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.tuberlin.dima.impro3.sentiment.SentimentParser;
import de.tuberlin.dima.impro3.tagger.TaggedSentence;

public class SentimentParserTest {
	
	@Test
	public void testSentimentParser(){
		SentimentParser sentimentParser = SentimentParser.newSentimentParser();
				
		float sentiment = sentimentParser.computeSentimentValue("Die SPD und die CDU haben die Wahl verloren, weil sie schlechte Verlierer sind.");
		
		assertTrue("should be the right sentiment value", sentiment == -0.5573500394821167f);
	}
	
	@Test
	public void testSentimentParserTagged(){
		SentimentParser sentimentParser = SentimentParser.newSentimentParser();
		
		String [] tokens = {"Die", "SPD", "und", "die", "CDU", "und", "die", "CDU", "CSU", "sind", "verlogene", "Lustmolche", "."};
		String [] ne = {"O", "I-ORG", "O", "O", "I-ORG", "O", "O", "I-ORG", "I-ORG", "O", "O", "O", "O"};
		String [] tags = {"ART", "NE", "KON", "ART", "NE", "KON", "ART", "NE", "NE", "VAFIN", "ADJA", "NN", "$."};

		TaggedSentence taggedSentence = new TaggedSentence(tokens, tags, ne);
		
		float sentiment = sentimentParser.computeSentimentValue(taggedSentence);
		
		assertTrue("should be the right sentiment value", sentiment == -0.3355f);
	}


}
