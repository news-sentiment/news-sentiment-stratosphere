package de.tuberlin.dima.impro3.tagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

@Deprecated
public class StanfordPipeline {
	
	private StanfordCoreNLP pipeline = null;
	
	public StanfordPipeline(Properties properties) {
		this.pipeline = new StanfordCoreNLP(properties);
	}

	public String[] splitSentences(String text) {
		List<String> sentenceList = new ArrayList<String>();
			    	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	    	sentenceList.add(sentence.toString());
	    }
	    
	    String[] sentenceArray = new String [sentenceList.size()];
	    
	    sentenceList.toArray(sentenceArray);
		return sentenceArray;
	}
	
	public TaggedSentence tagSentence(String sentence) {
		List<String> words = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		List<String> nes = new ArrayList<String>();
		
		String[] wordsArray = null;
		String[] tagsArray = null;
		String[] nesArray = null;
			    
	    // read some text in the text variable
	    String Sentence = sentence;
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(Sentence);
	    
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

	    words.toArray(wordsArray);
	    tags.toArray(tagsArray);
	    nes.toArray(nesArray);
	    
	    TaggedSentence taggedSentence = new TaggedSentence(wordsArray,tagsArray,nesArray);
	    
		return taggedSentence;
	}
	
	public TaggedSentence[] tagText(String text) {
		// TODO implement this method for tagging a hole text
		// TODO return an array of TaggedSentence
		// TODO iterate first over coreMap and the over coreLabel, etc.
		
		return null;
	}
	
}
