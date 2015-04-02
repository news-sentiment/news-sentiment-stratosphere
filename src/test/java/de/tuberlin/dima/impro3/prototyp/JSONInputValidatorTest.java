package de.tuberlin.dima.impro3.prototyp;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

import de.tuberlin.dima.impro3.prototyp.JSONInputValidator.JSONMap;
import eu.stratosphere.nephele.configuration.Configuration;
import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactString;

public class JSONInputValidatorTest {
	
	@Test
	public void testJSONInputValidatorMap() throws Exception {
		ArrayList<String> jsonDatas = new ArrayList<String>();
		
		JSONMap map = new JSONMap();
		map.open(new Configuration());
		JSONInputValidatorMapCollector out = new JSONInputValidatorMapCollector(jsonDatas);
		
		Scanner scanner = new Scanner(new File("src/test/resources/1000_newsdump_utf8.json"));
		
		while(scanner.hasNextLine()){
			String jsonString = (String) scanner.nextLine();
			PactRecord record = new PactRecord(new PactString(jsonString));
			map.map(record, out);		
		}
		
		scanner.close();
		out.close();
	}
	
	class JSONInputValidatorMapCollector implements Collector<PactRecord> {
		
		ArrayList<String> jsonDatas;
		
		public JSONInputValidatorMapCollector(ArrayList<String> jsonDatas) {
			this.jsonDatas = jsonDatas;
		}

		@Override
		public void close() {
		}

		@Override
		public void collect(PactRecord record) {
			PactString jsonData = record.getField(0, PactString.class);
			jsonDatas.add(jsonData.getValue());
		}
		
	}

}

