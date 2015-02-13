package com.dhbw.mas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AscedingPaymentComputation implements IPaymentComputation {

	public AscedingPaymentComputation() {
		super();
	}

	@Override
	public PaymentMatrix computePaymentMatrix(Random rng, DataSetCollection instances,
			int numAgents, int minNegativePayment, int maxNegativePayment) {
		int numAgentJobs = instances.getInstanceJobCount() - 2;
		int numNegativePaymentJobs = numAgentJobs / 2;

		PaymentMatrix paymentMatrix = new PaymentMatrix(instances.getInstanceJobCount(), numAgents);

		List<Integer> agentAssignments = new ArrayList<Integer>();
		for (int i = 0; i < numAgentJobs; ++i) {
			agentAssignments.add(i % numAgents);
		}

		List<Integer> negativeAgentAssignments = new ArrayList<Integer>(
				agentAssignments.subList(0, numNegativePaymentJobs));
		Collections.shuffle(negativeAgentAssignments, rng);
		List<Integer> positiveAgentAssignments = new ArrayList<Integer>(
				agentAssignments.subList(numNegativePaymentJobs, numAgentJobs));
		Collections.shuffle(positiveAgentAssignments, rng);

		Map<Integer, Double> globalProbabilityMap = instances
				.getJobProbabilityDistributionInPath(0,
						instances.getInstanceJobCount() - 1);

		for (int i = 0; i < negativeAgentAssignments.size(); ++i) {
			int paymentValue = maxNegativePayment
					+ (int) ((1.0 - globalProbabilityMap.get(i + 1))
							* (minNegativePayment - maxNegativePayment) * (0.5 + 0.5 * rng
							.nextDouble()));
			paymentMatrix.updatePayment(i + 1, negativeAgentAssignments.get(i),
					paymentValue);
		}
		for (int i = 0; i < positiveAgentAssignments.size(); ++i) {
			int paymentValue = Math.abs(maxNegativePayment)
					+ Math.abs(minNegativePayment)
					+ (int) (5
							* (globalProbabilityMap.get(numNegativePaymentJobs
									+ i + 1)) * Math.abs(maxNegativePayment) * rng
								.nextDouble());
			paymentMatrix.updatePayment(numNegativePaymentJobs + i + 1,
					positiveAgentAssignments.get(i), paymentValue);
		}
		
		return paymentMatrix;
	}

	@Override
	public void validateInput(int minNegativePayment, int maxNegativePayment) throws PadParams {
		if (!(minNegativePayment < maxNegativePayment)) {
			throw new PadParams("min is not smaller than max");
		}
	}

}
