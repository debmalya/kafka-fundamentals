package com.luxoft.eas026.streams;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

public class UpperCaseProcessor implements Processor<String, String, String, String> {

	private ProcessorContext<String,String> context;

	@Override
	public void init(ProcessorContext<String, String> context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(Record<String, String> record) {

		String key = record.key();
		String transformedValue = record.value().toUpperCase();
		Record<String, String> transformedRecord = new Record<>(key, transformedValue, System.nanoTime());
		context.forward(transformedRecord);
	}

}
