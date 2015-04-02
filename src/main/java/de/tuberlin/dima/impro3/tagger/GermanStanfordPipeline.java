package de.tuberlin.dima.impro3.tagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class GermanStanfordPipeline {

	private static StanfordCoreNLP pipeline = null;

	public static GermanStanfordPipeline newSentenceSplitter() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit");
		
		pipeline = new StanfordCoreNLP(props);
		
		return new GermanStanfordPipeline();
	}

	public static GermanStanfordPipeline newSentenceTagger() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		props.put("ssplit.isOneSentence", Boolean.TRUE);
		props.put("pos.model", "src/main/resources/tagger/german-accurate.tagger");
		props.put("ner.model", "src/main/resources/ner/dewac_175m_600.crf.ser.gz");
		
		pipeline = new StanfordCoreNLP(props);
		
		return new GermanStanfordPipeline();
	}
	
	public static GermanStanfordPipeline newTextTagger() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		props.put("pos.model", "src/main/resources/tagger/german-accurate.tagger");
		props.put("ner.model", "src/main/resources/ner/dewac_175m_600.crf.ser.gz");
		
		pipeline = new StanfordCoreNLP(props);
		
		return new GermanStanfordPipeline();
	}
	
	public String[] splitSentences(String text) {
		List<String> sentenceList = new ArrayList<String>();

		Annotation document = new Annotation(text);

		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			sentenceList.add(sentence.toString());
		}

		String[] sentenceArray = new String[sentenceList.size()];

		sentenceList.toArray(sentenceArray);

		return sentenceArray;
	}

	public TaggedSentence tagSentence(String sentence) {
		// TODO
		return null;
	}

	public TaggedSentence[] tagText(String text) {
		// TODO
		return null;
	}
}
