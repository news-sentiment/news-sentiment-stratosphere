package de.tuberlin.dima.impro3.tagger;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

import de.tuberlin.dima.impro3.tagger.GermanTagger;
import de.tuberlin.dima.impro3.tagger.TaggedSentence;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class GermanTaggerTest {
	
	GermanTagger pipeline;
		
	@Test
	public void splitSentences() {
		pipeline = GermanTagger.newSentenceSplitter();
		
		ArrayList<String> sentences = new ArrayList<String>();
		sentences.add("This is test sentence.");
		sentences.add("This is a sample sentence.");
		
		String [] retSentences = pipeline.splitSentences(Joiner.on(" ").join(sentences));
		
		assertTrue("Ther should be " + sentences.size() + "sentences", retSentences.length == sentences.size());
		
		int i = 0;
		for (String sentence : sentences) {
			assertTrue("should be the correct sentence.", sentence.equals(retSentences[i]));
			i++;
		}
		
		pipeline = null;
	}
	
	@Test
	public void tagSentence() throws ClassNotFoundException, IOException, URISyntaxException {
		pipeline = GermanTagger.newSentenceTagger();
		
		String sentence ="SPD hat die Wahl gewonnen.";
		TaggedSentence ts = pipeline.tagSentence(sentence);
		 
		assertTrue("There should be 6 tokens", 6 == ts.getTokens().length);
		assertTrue("SPD is an organisation!", "I-ORG".equals(ts.getNe()[0]));
		
		pipeline = null;
	}

	@Test
	public void tagText() throws ClassNotFoundException, IOException, URISyntaxException {
		pipeline = GermanTagger.newTextTagger();
		
		ArrayList<String> sentences = new ArrayList<String>();
		sentences.add("SPD hat die Wahl gewonnen. Wir sind alle froh darüber. NPD ist wieder am Anmarsch.");
		
		TaggedSentence[] taggedOnes=pipeline.tagText(sentences.get(0));
		
		TaggedSentence ts = taggedOnes[0];
			assertTrue("There should be 6 tokens", 6 == ts.getTokens().length);
			assertTrue("SPD is an organisation!", "I-ORG".equals(ts.getNe()[0]));
			
		pipeline = null;	
	}
	
	@Test
	public void testLibrary() throws ClassNotFoundException, IOException {
		String text = "Etwas erholen konnten sich die Piraten. Sie verbesserten sich um einen Punkt auf fünf Prozent und haben damit wieder Chancen auf einen Einzug in den Bundestag. Auf sonstige Parteien entfallen fünf Prozent.";
		
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(text));
								
		String sentence = "Die SPD und die CDU und die CDU CSU sind verlogene Lustmolche.";
		
		MaxentTagger tagger = new MaxentTagger("src/main/resources/tagger/german-accurate.tagger");
		
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier("src/main/resources/ner/dewac_175m_600.crf.ser.gz");
		
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		List<CoreLabel> nerLabels = new ArrayList<CoreLabel>();
		
		List<List<HasWord>> sentences1 = MaxentTagger.tokenizeText(new StringReader(sentence));
		for (List<HasWord> s : sentences1) {
			taggedWords.addAll(tagger.tagSentence(s));
			nerLabels.addAll(classifier.classifySentence(s));
		}
	}

}
