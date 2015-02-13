package com.dhbw.mas.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.dhbw.mas.DataSetInstance;
import com.dhbw.mas.DotExporter;
import com.dhbw.mas.PaymentMatrix;

public class DotExporterCli {
	@Parameter(names={"--help", "-h"}, help = true)
	private boolean help;
	
	@Parameter(names={"--output-file", "-f"}, converter=FileConverter.class)
	private File outputFile = new File("./out.dot");
	
	@Parameter(names={"--paymentfiles", "-p"}, required=true, variableArity = true)
	private List<String> paymentFiles;
	
	@Parameter(names={"--instancefile", "-i"}, required=true, converter=FileConverter.class)
	private File instanceFile = new File(".");
	
	private void run() throws Exception {
		DataSetInstance instance = new DataSetInstance(
				new InputStreamReader(new FileInputStream(instanceFile)), instanceFile.getName());
		
		List<InputStreamReader> paymentInputs = new ArrayList<InputStreamReader>();
		for(String paymentFile : paymentFiles) {
			paymentInputs.add(new InputStreamReader(new FileInputStream(paymentFile)));
		}
		PaymentMatrix paymentMatrix = new PaymentMatrix(paymentInputs);
		
		String dotOutput = 
				DotExporter.generateDotRepresentation(instance, paymentMatrix, instanceFile.getName());
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outputFile));
			bw.append(dotOutput);
			bw.flush();
		} finally {
			if (bw != null) {
				bw.close();
			}
		}

	}
	
	public static void main(String[] args) throws Exception {
		DotExporterCli dotexpcli = new DotExporterCli();
		JCommander c = new JCommander(dotexpcli, args);
		c.setProgramName(System.getProperty("app.name",
				GeneratorCli.class.getCanonicalName()));

		if (dotexpcli.help) {
			c.usage();
		} else {
			dotexpcli.run();
		}
	}
}
