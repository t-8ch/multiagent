package com.dhbw.mas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomPaymentComputation implements IPaymentComputation {
	private Random rng;

	public RandomPaymentComputation(Random rng) {
		this.rng = rng;
	}

	@Override
	public PaymentMatrix computePaymentMatrix(DataSetCollection instances,
			int numAgents, int minPaymentValue, int maxPaymentValue) {
		int jobs = instances.getInstanceJobCount();
		PaymentMatrix result = new PaymentMatrix(jobs, numAgents);

		int numAgentJobs = instances.getInstanceJobCount() - 2;

		List<Integer> agentAssignments = new ArrayList<Integer>();
		for (int i = 0; i < numAgentJobs; ++i) {
			agentAssignments.add(i % numAgents);
		}

		Collections.shuffle(agentAssignments, this.rng);

		for (int i = 0; i < agentAssignments.size(); ++i) {
			int paymentValue = this.rng.nextInt(maxPaymentValue -minPaymentValue) + minPaymentValue + 1;
			result.updatePayment(i, agentAssignments.get(i), paymentValue);
		}

		return result;
	}

}