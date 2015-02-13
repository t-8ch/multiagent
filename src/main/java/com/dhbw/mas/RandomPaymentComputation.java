package com.dhbw.mas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomPaymentComputation implements IPaymentComputation {

	public RandomPaymentComputation() {
		super();
	}

	@Override
	public PaymentMatrix computePaymentMatrix(Random rng,
			DataSetCollection instances, int numAgents, int minPaymentValue,
			int maxPaymentValue) {
		int jobs = instances.getInstanceJobCount();
		PaymentMatrix result = new PaymentMatrix(jobs, numAgents);

		int numAgentJobs = instances.getInstanceJobCount() - 2;

		List<Integer> agentAssignments = new ArrayList<Integer>();
		for (int i = 0; i < numAgentJobs; ++i) {
			agentAssignments.add(i % numAgents);
		}

		Collections.shuffle(agentAssignments, rng);

		for (int i = 0; i < agentAssignments.size(); ++i) {
			int paymentValue = rng.nextInt(maxPaymentValue
					- minPaymentValue)
					+ minPaymentValue + 1;
			result.updatePayment(i + 1, agentAssignments.get(i), paymentValue);
		}

		return result;
	}

	@Override
	public void validateInput(int minPaymentValue, int maxPaymentValue) throws PadParams {
		if (!(minPaymentValue < maxPaymentValue)) {
			throw new PadParams("min is not smaller than max");
		}
	}

}