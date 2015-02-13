package com.dhbw.mas.cli;

import com.beust.jcommander.Parameter;
import com.dhbw.mas.DataSetCollection;

public class DataSetParams {
	@Parameter(names={"--dataset", "-d"},
			required=true, converter=DataSetConverter.class,
			description = "RCP datasets to use either integer or path to directory of datasets")
	public DataSetCollection datasets;
}
