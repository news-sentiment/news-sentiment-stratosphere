package de.tuberlin.dima.impro3.tagger;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.tuberlin.dima.impro3.util.ResourceManager;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class GermanTagger {
	
	private static StanfordCoreNLP pipeline = null;
	private static MaxentTagger tagger = null;
	private static AbstractSequenceClassifier<CoreLabel> classifier = null;
		
	public static GermanTagger newSentenceSplitter(){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		
		pipeline = new StanfordCoreNLP(props);
		
		return new GermanTagger();
	}
	
	public static GermanTagger newSentenceTagger() throws ClassNotFoundException, IOException, URISyntaxException {
		
		pipeline = null;
		
//		String taggerFile = GermanTagger.class.getClassLoader().getResource("tagger/german-accurate.tagger").getPath();		
//		String classifierFile = GermanTagger.class.getClassLoader().getResource("ner/dewac_175m_600.crf.ser.gz").getPath();
		
		String taggerFile = ResourceManager.getResourcePath("/tagger/german-accurate.tagger");
		String classifierFile = ResourceManager.getResourcePath("/ner/dewac_175m_600.crf.ser.gz");
		
		tagger = new MaxentTagger(taggerFile);
		classifier = CRFClassifier.getClassifier(classifierFile);
				
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		
		pipeline = new StanfordCoreNLP(props);
		
		return new GermanTagger();
	}
	
	public static GermanTagger newTextTagger() throws ClassNotFoundException, IOException, URISyntaxException {
		
		pipeline = null;
		
//		String taggerFile = GermanTagger.class.getClassLoader().getResource("tagger/german-accurate.tagger").getPath();		
//		String classifierFile = GermanTagger.class.getClassLoader().getResource("ner/dewac_175m_600.crf.ser.gz").getPath();
		
		String taggerFile = ResourceManager.getResourcePath("/tagger/german-accurate.tagger");
		String classifierFile = ResourceManager.getResourcePath("/ner/dewac_175m_600.crf.ser.gz");
		
		tagger = new MaxentTagger(taggerFile);
		classifier = CRFClassifier.getClassifier(classifierFile);
				
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		
		pipeline = new StanfordCoreNLP(props);
		
		return new GermanTagger();
	}
	
	public String[] splitSentences(String text) {
		
		List<String> sentenceList = new ArrayList<String>();
	    
	    Annotation document = new Annotation(text);
	    
	    pipeline.annotate(document);
	    
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	    	sentenceList.add(sentence.toString());
	    }
	    
	    String[] sentenceArray = new String [sentenceList.size()];
	    
	    sentenceList.toArray(sentenceArray);
		
	    return sentenceArray;
	}
	
	public TaggedSentence tagSentence(String sentence) {
		
		List<TaggedWord> tags = new ArrayList<TaggedWord>();
		List<CoreLabel> nerLabels = new ArrayList<CoreLabel>();
		
		List<String> Words = new ArrayList<String>();
		List<String> Tags = new ArrayList<String>();
		List<String> NerLabels = new ArrayList<String>();
		
		List<List<HasWord>> words = tagger.tokenizeText(new StringReader(sentence));
		
		for (List<HasWord> s : words) {
			tags.addAll(tagger.tagSentence(s));
			nerLabels.addAll(classifier.classifySentence(s));
		}
		
		for(TaggedWord tw:tags) {
			Words.add(tw.value());
			Tags.add(tw.tag());
		}
		
		for(CoreLabel cl:nerLabels) {
			NerLabels.add(cl.getString(AnswerAnnotation.class));
		}
		
		return new TaggedSentence(Words.toArray(new String[Words.size()]), Tags.toArray(new String[Tags.size()]), NerLabels.toArray(new String[NerLabels.size()]));
	}
	
	public TaggedSentence[] tagText(String text) {
		
		List<TaggedSentence> taggedSentences = new ArrayList<TaggedSentence>();
		
		String[] sentences;
		
		sentences = splitSentences(text);
		
		for(String sentence:sentences) {
			taggedSentences.add(tagSentence(sentence));
		}
		
		return taggedSentences.toArray(new TaggedSentence[taggedSentences.size()]);
	}

}
