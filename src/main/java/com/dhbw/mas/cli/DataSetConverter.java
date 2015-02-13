package com.dhbw.mas.cli;

import com.beust.jcommander.IStringConverter;
import com.dhbw.mas.DataSetCollection;

public class DataSetConverter implements IStringConverter<DataSetCollection> {

	@Override
	public DataSetCollection convert(String value) {
		try {
			int builtinDataSet = Integer.valueOf(value);

			switch (builtinDataSet) {
			case 30:
				return DataSetCollection.getBuiltinDataSets30();
			case 60:
				return DataSetCollection.getBuiltinDataSets60();
			case 120:
				return DataSetCollection.getBuiltinDataSets120();
			}
		} catch (Exception e) {
			/* Just interpret it as file */
		}
		
		throw new RuntimeException(
				String.format("Unsupported builtin dataset {} (loading from directory not yet implemented", value));
	}
}