package de.tuberlin.dima.impro3.prototyp;

import org.json.JSONObject;

import eu.stratosphere.pact.common.contract.FileDataSink;
import eu.stratosphere.pact.common.contract.FileDataSource;
import eu.stratosphere.pact.common.contract.MapContract;
import eu.stratosphere.pact.common.io.RecordOutputFormat;
import eu.stratosphere.pact.common.io.TextInputFormat;
import eu.stratosphere.pact.common.plan.Plan;
import eu.stratosphere.pact.common.plan.PlanAssembler;
import eu.stratosphere.pact.common.plan.PlanAssemblerDescription;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.MapStub;
import eu.stratosphere.pact.common.stubs.StubAnnotation.ConstantFields;
import eu.stratosphere.pact.common.stubs.StubAnnotation.OutCardBounds;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

/**
 * This class validates the json input data 
 */
public class JSONInputValidator implements PlanAssembler,
		PlanAssemblerDescription {

	@Override
	public String getDescription() {
		return "Parameters: [noSubTasks] [input] [output]";
	}

	@Override
	public Plan getPlan(String... args) {

		// parse job parameters
		int noSubTasks = (args.length > 0 ? Integer.parseInt(args[0]) : 1);
		String dataInput = (args.length > 1 ? args[1] : "");
		String output = (args.length > 2 ? args[2] : "");

		FileDataSource source = new FileDataSource(TextInputFormat.class,
				dataInput, "Input Lines");
		source.setDegreeOfParallelism(noSubTasks);
		
		MapContract jsonMap = MapContract
				.builder(JSONMap.class)
				.input(source)
				.name("JSONMap")
				.build();

		FileDataSink out = new FileDataSink(RecordOutputFormat.class, output, jsonMap, "JSON Validation");
		
		RecordOutputFormat.configureRecordFormat(out)
		.recordDelimiter('\n')
		.fieldDelimiter('\t')
		.lenient(true)
		.field(PactString.class, 0);
		
		return new Plan(out, "JSONVAlidator Pact Implementation");
	}

	/**
	 * Map PACT converts a PactRecord containing one json object and emit
	 * invalid json data
	 * 
	 */
	@ConstantFields(fields={})
	@OutCardBounds(lowerBound=0, upperBound=OutCardBounds.UNBOUNDED)
	public static class JSONMap extends MapStub {
		
		// initialize reusable mutable objects
		private PactRecord outputRecord = new PactRecord();
		private PactString jsonData = new PactString();
		
		@Override
		public void map(PactRecord jsonInput, Collector<PactRecord> out)
				throws Exception {
						
			PactString pactString = jsonInput.getField(0, PactString.class);
			JSONObject jsonObject = new JSONObject(pactString.getValue()); 
			
			if (!jsonObject.has("d")) {
				System.err.println("json data does not contain attribude 'd'");
				jsonData.setValue(pactString);
				outputRecord.setField(0, jsonData);
				out.collect(outputRecord);
				return;
			}
			
			JSONObject news = jsonObject.getJSONObject("d");
				
			if (!news.has("Document_Content_News_Body")) {
				System.err.println("json data doen not contain 'Document_Content_News_Body'");
				jsonData.setValue(pactString);
				outputRecord.setField(0, jsonData);
				out.collect(outputRecord);
				return;
			}
			
			if (!news.has("Document_Content_News_Publication_Name")) {
				System.err.println("json data doen not contain 'Document_Content_News_Publication_Name'");
				jsonData.setValue(jsonInput.getField(0, PactString.class));
				outputRecord.setField(0, jsonData);
				out.collect(outputRecord);
				return;
			}
			
		}

	}

}
