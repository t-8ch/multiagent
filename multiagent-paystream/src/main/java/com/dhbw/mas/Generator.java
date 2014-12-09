package com.dhbw.mas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Generator {
	
	public void generate(DataSetCollection instances, int numAgents, int minNegativePayment, int maxNegativePayment,
			String outputDirectory, String paymentFilePrefix) throws Exception {
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

	public static void main(String[] args) {
		try {
			new Generator().generate(new DataSetCollection(
					"./RCP/30/J3010_1.RCP", 
					"./RCP/30/J3014_1.RCP",
					"./RCP/30/J3018_1.RCP",
					"./RCP/30/J302_1.RCP",
					"./RCP/30/J3022_1.RCP",
					"./RCP/30/J3026_1.RCP",
					"./RCP/30/J3030_1.RCP",
					"./RCP/30/J3034_1.RCP",
					"./RCP/30/J3038_1.RCP",
					"./RCP/30/J3042_1.RCP",
					"./RCP/30/J3046_1.RCP",
					"./RCP/30/J306_1.RCP"), 
					5, -100, -50, System.getProperty("user.dir"), "cf30");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
}