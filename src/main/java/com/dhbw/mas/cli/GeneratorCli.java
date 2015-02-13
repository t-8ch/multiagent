package com.dhbw.mas.cli;

import java.io.File;
import java.util.Random;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.dhbw.mas.DataSetCollection;
import com.dhbw.mas.Generator;

public class GeneratorCli {
	
	@Parameter(names={"--help", "-h"}, help = true)
	private boolean help;
	
	@Parameter(names={"--max-negative-payment", "-a"}, required=true)
	private int maxNegativePayment;

	@Parameter(names={"--min-negative-payment", "-i"}, required=true)
	private int minNegativePayment;

	@Parameter(names={"--agents", "-g"}, required=true)
	private int agents;
	
	@Parameter(names={"--output-dir", "-o"}, converter=FileConverter.class)
	private File outputDirectory = new File(".");
	
	@Parameter(names={"--output-file-prefix", "-p"})
	private String outputFilePrefix = "cf";

	@Parameter(names={"--random-seed", "-r"})
	private long randomSeed = new Random().nextLong();
	
	@ParametersDelegate
	private DataSetParams dsp = new DataSetParams();
	
	private void run() throws Exception {
		DataSetCollection datasets = dsp.datasets;

		String filePrefix = outputFilePrefix + 
				String.format("%03d", datasets.getInstanceJobCount());
		
			new Generator(randomSeed).generate(datasets,
					agents, -1 * minNegativePayment,
					-1 * maxNegativePayment, outputDirectory,
					filePrefix);
	}
	
	public static void main(String[] args) throws Exception {
		GeneratorCli generator = new GeneratorCli();
		JCommander c = new JCommander(generator, args);
		c.setProgramName(System.getProperty("app.name",
				GeneratorCli.class.getCanonicalName()));

		if (generator.help) {
			c.usage();
		} else {
			generator.run();
		}
	}

}