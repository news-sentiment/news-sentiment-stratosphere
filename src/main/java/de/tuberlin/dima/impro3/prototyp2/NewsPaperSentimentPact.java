package de.tuberlin.dima.impro3.prototyp2;

import java.util.Iterator;

import org.json.JSONObject;

import de.tuberlin.dima.impro3.sentiment.SentimentParser;

import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.contract.FileDataSink;
import eu.stratosphere.pact.common.contract.FileDataSource;
import eu.stratosphere.pact.common.contract.MapContract;
import eu.stratosphere.pact.common.contract.ReduceContract;
import eu.stratosphere.pact.common.io.RecordOutputFormat;
import eu.stratosphere.pact.common.io.TextInputFormat;
import eu.stratosphere.pact.common.plan.Plan;
import eu.stratosphere.pact.common.plan.PlanAssembler;
import eu.stratosphere.pact.common.plan.PlanAssemblerDescription;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.MapStub;
import eu.stratosphere.pact.common.stubs.ReduceStub;
import eu.stratosphere.pact.common.stubs.StubAnnotation.ConstantFields;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactDouble;
import eu.stratosphere.pact.common.type.base.PactInteger;
import eu.stratosphere.pact.common.type.base.PactString;

public class NewsPaperSentimentPact implements PlanAssembler,
		PlanAssemblerDescription {

	@Override
	public String getDescription() {
		return "Parameters: [noSubTasks] [input] [output]";
	}

	@Override
	public Plan getPlan(String... args) throws IllegalArgumentException {

		// parse job parameters
		int noSubTasks = (args.length > 0 ? Integer.parseInt(args[0]) : 1);
		String dataInput = (args.length > 1 ? args[1] : "");
		String output = (args.length > 2 ? args[2] : "");

		FileDataSource source = new FileDataSource(TextInputFormat.class,
				dataInput, "Input Lines");
		source.setDegreeOfParallelism(noSubTasks);
		
		MapContract jsonParserContract = MapContract
				.builder(JSONParserMap.class).input(source)
				.name("JSONParserMap").build();
		
		MapContract sentenceSplitterContract = MapContract
				.builder(SentenceSplitterMap.class).input(jsonParserContract)
				.name("SentenceSplitterMap").build();

		MapContract sentimentContract = MapContract.builder(SentimentMap.class)
				.input(sentenceSplitterContract).name("SentimentMap").build();

		MapContract namedEntityContract = MapContract
				.builder(NamedEntityMap.class).input(sentimentContract)
				.name("NamedEntityMap").build();

		ReduceContract sentimentReduceContract = new ReduceContract.Builder(
				SentimentReduce.class, PactString.class, 0)
				.input(namedEntityContract).name("SentimentReduce").build();

		MapContract sentimentFilterContract = MapContract
				.builder(FilterSentimentMap.class)
				.input(sentimentReduceContract).name("FilterSentimentMap")
				.build();

		FileDataSink out = new FileDataSink(RecordOutputFormat.class, output,
				sentimentFilterContract, "Sentiment Filter Output Contract");

		RecordOutputFormat.configureRecordFormat(out).recordDelimiter("\n")
				.fieldDelimiter('\t').lenient(true).field(PactString.class, 0)
				.field(PactString.class, 1).field(PactDouble.class, 2)
				.field(PactInteger.class, 3);

		Plan plan = new Plan(out, "NewsPaperSentiment Pact Implementation");
		plan.setDefaultParallelism(noSubTasks);

		return plan;
	}
	
	/**
	 * 
	 * Input: [jsonInput] 
	 * Output: [news paper, news content]
	 * 
	 */
	public static class JSONParserMap extends MapStub {
		
		// initialize reusable mutable objects
		private final PactRecord outputRecord = new PactRecord();
		private final PactString newsPaper = new PactString();
		private final PactString newsText = new PactString();

		@Override
		public void map(PactRecord jsonInputRecord, Collector<PactRecord> out)
				throws Exception {
			
			String jsonInput = jsonInputRecord.getField(0, PactString.class).getValue();
			JSONObject jsonObject = new JSONObject(jsonInput);
			
			if (jsonObject.has("d")) {
				
				JSONObject data = jsonObject.getJSONObject("d");
				
				if (data.has("Document_Content_News_Body") && data.has("Document_Content_News_Publication_Name")) {
					
					String newsBody = data.getString("Document_Content_News_Body");
					String publicationName = data.getString("Document_Content_News_Publication_Name");
				
					newsPaper.setValue(publicationName);
					newsText.setValue(newsBody);
					outputRecord.setField(0, newsPaper);
					outputRecord.setField(1, newsText);
					out.collect(outputRecord);

				}
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * Input: [news paper, news content] 
	 * Output: [<news paper, sentence>]
	 * 
	 */
	public static class SentenceSplitterMap extends MapStub {

		// initialize reusable mutable objects
		private final PactRecord outputRecord = new PactRecord();
		private final PactString newsPaper = new PactString();
		private final PactString sentence = new PactString();

		@Override
		public void map(PactRecord newsRecord, Collector<PactRecord> out)
				throws Exception {

			newsPaper.setValue(newsRecord.getField(0, PactString.class));
			String newsText = newsRecord.getField(1, PactString.class).getValue();
				
			// split sentences
			String[] sentences = newsText.split("\\.");
			for (int i = 0; i < sentences.length; i++) {
				sentences[i] = sentences[i].trim();
				// sentences[i] = sentences[i].concat(".");
			}

			outputRecord.setField(0, newsPaper);

			for (String sentenceString : sentences) {
				sentence.setValue(sentenceString);
				outputRecord.setField(1, sentence);
				out.collect(outputRecord);
			}
				
		}
	}
	
	/**
	 * 
	 * Input: [news paper, sentence] 
	 * Output: [news paper, sentence, sentiment value]
	 * 
	 */
	public static class SentimentMap extends MapStub {

		// initialize reusable mutable objects
		private final PactRecord outputRecord = new PactRecord();
		private final PactString newsPaper = new PactString();
		private final PactString sentence = new PactString();
		private final PactDouble sentimentValue = new PactDouble();

		SentimentParser sentimentParser = null;

		@Override
		public void open(Configuration parameters) throws Exception {
			sentimentParser = SentimentParser.newSentimentParser();
			super.open(parameters);
		}

		@Override
		public void map(PactRecord record, Collector<PactRecord> out)
				throws Exception {

			newsPaper.setValue(record.getField(0, PactString.class));
			sentence.setValue(record.getField(1, PactString.class));

			float sentiment = sentimentParser.computeSentimentValue(sentence
					.getValue());
			sentimentValue.setValue(sentiment);

			outputRecord.setField(0, newsPaper);
			outputRecord.setField(1, sentence);
			outputRecord.setField(2, sentimentValue);

			out.collect(outputRecord);
		}

	}

	/**
	 * 
	 * Input: [news paper, sentence, sentiment value] 
	 * Output: [<news paper, NE>, sentiment value]
	 * 
	 */
	public static class NamedEntityMap extends MapStub {

		// initialize reusable mutable objects
		private final PactRecord outputRecord = new PactRecord();
		private final PactString newsPaperNePair = new PactString();
		private final PactDouble sentimentValue = new PactDouble();
		private final PactString sentence = new PactString();
		private final PactString newsPaper = new PactString();

		@Override
		public void map(PactRecord record, Collector<PactRecord> out)
				throws Exception {

			newsPaper.setValue(record.getField(0, PactString.class));
			sentence.setValue(record.getField(1, PactString.class));
			sentimentValue.setValue(record.getField(2, PactDouble.class).getValue());

			String[] neFilter = { "SPD", "CDU", "FDP", "CSU", "Linke", "Piraten" };

			for (int i = 0; i < neFilter.length; i++) {
				if (sentence.getValue().contains(neFilter[i])) {
					newsPaperNePair.setValue(newsPaper + "\t" + neFilter[i]);
					outputRecord.setField(0, newsPaperNePair);
					outputRecord.setField(1, sentimentValue);
					out.collect(outputRecord);
				}
			}

		}

	}

	/**
	 * Builds the average of the sentiments values for a given key. The
	 * sentiments values are assumed to be at position <code>1</code> in the
	 * record. The other fields are not modified.
	 * 
	 * Input: [<news paper, NE>, sentiment value] 
	 * Output: [<news paper, NE>, avg(sentiment value), count]
	 * 
	 */
	@ConstantFields(fields = { 0 })
	public static class SentimentReduce extends ReduceStub {

		// initialize reusable mutable objects
		private final PactRecord outputRecord = new PactRecord();
		private final PactString newsPaperNePair = new PactString();
		private final PactDouble sentimentValue = new PactDouble();

		@Override
		public void reduce(Iterator<PactRecord> records,
				Collector<PactRecord> out) throws Exception {
			// build average or something else on the sentiment values
			double avg = 0;
			double sum = 0;
			int count = 0;

			while (records.hasNext()) {
				PactRecord re = records.next();
				newsPaperNePair.setValue(re.getField(0, PactString.class));
				sentimentValue.setValue(re.getField(1, PactDouble.class).getValue());
				count++;
				sum += sentimentValue.getValue();
			}
			
			avg = sum / count;

			outputRecord.setField(0, newsPaperNePair);
			outputRecord.setField(1, new PactDouble(avg));
			outputRecord.setField(2, new PactInteger(count));

			out.collect(outputRecord);
		}

	}

	public static class FilterSentimentMap extends MapStub {

		private final PactRecord outRecord = new PactRecord();
		private final PactString newsPaperNePair = new PactString();
		private final PactString newsPaper = new PactString();
		private final PactString namedEntity = new PactString();
		private final PactDouble averageSentimentValue = new PactDouble();
		private final PactInteger count = new PactInteger();
		private static final double THRESHOLD = 10;

		@Override
		public void map(PactRecord record, Collector<PactRecord> out)
				throws Exception {

			newsPaperNePair.setValue(record.getField(0, PactString.class));
			averageSentimentValue.setValue(record.getField(1, PactDouble.class).getValue());
			count.setValue(record.getField(2, PactInteger.class).getValue());

			newsPaper.setValue(newsPaperNePair.getValue().split("\t")[0]);
			namedEntity.setValue(newsPaperNePair.getValue().split("\t")[1]);

			if (count.getValue() > THRESHOLD) {
				outRecord.setField(0, newsPaper);
				outRecord.setField(1, namedEntity);
				outRecord.setField(2, averageSentimentValue);
				outRecord.setField(3, count);
				out.collect(outRecord);
			}
		}

	}
	
}