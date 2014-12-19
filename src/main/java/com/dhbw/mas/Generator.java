package com.dhbw.mas;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import org.apache.commons.lang3.StringUtils;




public class Generator {
	
	public void generate(DataSetCollection instances, int numAgents, int minNegativePayment, int maxNegativePayment,
			File outputDirectory, String paymentFilePrefix) throws Exception {
		validateInput(instances, numAgents, minNegativePayment, maxNegativePayment);
		
		PaymentMatrix paymentMatrix = new PaymentMatrix(instances.getInstanceJobCount(), numAgents);
		
		computePaymentMatrix(instances, numAgents, minNegativePayment, maxNegativePayment, paymentMatrix);
		
		new Validator().validate(instances, paymentMatrix);
		
		paymentMatrix.writeAgentPaymentValuesToFiles(outputDirectory, paymentFilePrefix);
		
		System.out.println(paymentMatrix);
	}

	private void computePaymentMatrix(DataSetCollection instances, int numAgents, 
			int minNegativePayment, int maxNegativePayment, PaymentMatrix paymentMatrix) {
		int numAgentJobs = instances.getInstanceJobCount() - 2;
		int numNegativePaymentJobs = numAgentJobs / 2;
		
		List<Integer> agentAssignments = new ArrayList<Integer>();
		for(int i = 0; i < numAgentJobs; ++i) {
			agentAssignments.add(i % numAgents);
		}
		
		List<Integer> negativeAgentAssignments = new ArrayList<Integer>(agentAssignments.subList(0, numNegativePaymentJobs));
		Collections.shuffle(negativeAgentAssignments, new Random(System.currentTimeMillis()));
		List<Integer> positiveAgentAssignments = new ArrayList<Integer>(agentAssignments.subList(numNegativePaymentJobs, numAgentJobs));
		Collections.shuffle(positiveAgentAssignments, new Random(System.currentTimeMillis()));
		
		Map<Integer, Double> globalProbabilityMap = instances.getJobProbabilityDistributionInPath(0, instances.getInstanceJobCount() - 1);
		
		for(int i = 0; i < negativeAgentAssignments.size(); ++i) {
			int paymentValue = maxNegativePayment + (int)((1.0 - globalProbabilityMap.get(i + 1)) *
					(minNegativePayment - maxNegativePayment) * (0.5 + 0.5 * new Random().nextDouble()) );
			paymentMatrix.updatePayment(i + 1, negativeAgentAssignments.get(i), paymentValue);
		}
		for(int i = 0; i < positiveAgentAssignments.size(); ++i) {
			int paymentValue = Math.abs(maxNegativePayment) + Math.abs(minNegativePayment) + (int)(5 * (globalProbabilityMap.get(numNegativePaymentJobs + i + 1)) *
					Math.abs(maxNegativePayment) * new Random().nextDouble() );
			paymentMatrix.updatePayment(numNegativePaymentJobs + i + 1, positiveAgentAssignments.get(i), paymentValue);
		}
	}
	
	private void validateInput(DataSetCollection instances, int numAgents, int minNegativePayment, int maxNegativePayment) throws Exception {
		if(instances.getInstanceJobCount() < 1 || numAgents < 0 || (instances.getInstanceJobCount() - 2) % numAgents != 0 ||
				minNegativePayment >= maxNegativePayment || maxNegativePayment >= 0) {
			throw(new Exception("Bad generator input parameters"));
		}
	}

	private static int readIntOption(CommandLine cmd, String name) throws Exception {
		Long o = (Long)cmd.getParsedOptionValue(name);
		if (null == o) {
			throw new Exception("Invalid parameter " + name);
		}
		return o.intValue();
	}

	public static void main(String[] args) {
		Options options = new Options();
		Collection<Integer> validDataSets = new ArrayList<>();
		validDataSets.add(new Integer(30));
		validDataSets.add(new Integer(60));
		validDataSets.add(new Integer(120));

		options.addOption(
				OptionBuilder.withArgName(StringUtils.join(validDataSets, ' '))
				.withLongOpt("dataset").hasArg().isRequired()
				.withType(Number.class).create('d'));
		options.addOption(
				OptionBuilder.withArgName("int")
				.withLongOpt("agents").hasArg().isRequired()
				.withType(Number.class).create('g'));
		options.addOption(
				OptionBuilder.withArgName("int")
				.withLongOpt("min-negative-payment").hasArg()
				.isRequired().withType(Number.class).create('i'));
		options.addOption(
				OptionBuilder.withArgName("int")
				.withLongOpt("max-negative-payment").hasArg()
				.isRequired().withType(Number.class).create('a'));
		options.addOption(
				OptionBuilder.withArgName("PATH")
				.withLongOpt("output dir").hasArg().withType(File.class)
				.withDescription("where to write the output to (default: current directory)")
				.create('o'));
		options.addOption(
				OptionBuilder
				.withLongOpt("prefix").hasArg()
				.withDescription("Prefix to use for output files (default: cf<dataset>)")
				.create('p'));

		options.addOption("h", "help", false, "print this message");
		CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("generator", options);
				System.exit(0);
			}

			Integer numAgents, minNegativePayment, maxNegativePayment, datasetNumber;
			String paymentFilePrefix;
			File outputDirectory;
			DataSetCollection datasets;

			datasetNumber = readIntOption(cmd, "dataset");
			numAgents = readIntOption(cmd, "agents");
			minNegativePayment = readIntOption(cmd, "min-negative-payment");
			maxNegativePayment = readIntOption(cmd, "max-negative-payment");
			paymentFilePrefix = cmd.getOptionValue("prefix", "cf" + datasetNumber);
			outputDirectory = (File)cmd.getParsedOptionValue("output");

			if (outputDirectory == null) {
				outputDirectory = new File(System.getProperty("user.dir"));
			}

			if (!validDataSets.contains(datasetNumber)) {
				throw new Exception("Invalid dataset " + datasetNumber);
			} else {
				switch (datasetNumber) {
					case 30: datasets = DataSetCollection.getBuiltinDataSets30();
						 break;
					case 60: datasets = DataSetCollection.getBuiltinDataSets60();
						 break;
					case 120: datasets = DataSetCollection.getBuiltinDataSets120();
						  break;
					default: throw new Exception();
				}
			}

			new Generator().generate(datasets,
					numAgents, -1 * minNegativePayment,
					-1 * maxNegativePayment, outputDirectory,
					paymentFilePrefix);
			System.exit(0);
		} catch (MissingOptionException e) {
			System.out.println("Missing options: " + e.getMissingOptions());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("generator", options);
			System.exit(1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
