package com.dhbw.mas;

import java.io.File;
import java.util.Random;

public class Generator {
	private Random rng;
	private IPaymentComputation paymentcomputation;

	public void generate(DataSetCollection instances, int numAgents,
			int param1, int param2,
			File outputDirectory, String paymentFilePrefix) throws Exception {
		
		this.validateInput(instances, numAgents);
		this.paymentcomputation.validateInput(param1, param2);

		PaymentMatrix paymentMatrix = this.paymentcomputation
				.computePaymentMatrix(this.rng, instances, numAgents, param1,
						param2);

		new Validator().validate(instances, paymentMatrix);

		paymentMatrix.writeAgentPaymentValuesToFiles(outputDirectory,
				paymentFilePrefix);

		System.out.println(paymentMatrix);
	}

	private void validateInput(DataSetCollection instances, int numAgents
			) throws Exception {
		if (instances.getInstanceJobCount() < 1 || numAgents < 0
				|| (instances.getInstanceJobCount() - 2) % numAgents != 0) {
			throw (new Exception("Bad generator input parameters"));
		}
	}

	public Generator(long rngSeed, IPaymentComputation paymentComputation) {
		this.rng = new Random(rngSeed);
		this.paymentcomputation = paymentComputation;
	}
}
