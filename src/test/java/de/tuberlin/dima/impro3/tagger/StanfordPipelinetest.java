package de.tuberlin.dima.impro3.tagger;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordPipelinetest {
	
	@Ignore
	@Test
	public void splitSentences() {
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit");
		StanfordPipeline pipeline = new StanfordPipeline(props);
		
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
	}
	
	@Ignore
	@Test
	public void tagSentence() {
		// TODO write test for this method
	}
	
	@Ignore
	@Test
	public void tagText() {
		// TODO write test for this method
	}
	
	@Ignore
	@Test
	public void testLibrary() {
		
		List<String> words = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		List<String> nes = new ArrayList<String>();
		
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation("This is a english sample sentence.");
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    for(CoreLabel token: document.get(TokensAnnotation.class)){
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        words.add(word);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        tags.add(pos);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class); 
	        nes.add(ne);
	    }
	}
	
}
