package com.dhbw.mas;

import java.util.Random;

public interface IPaymentComputation {

	PaymentMatrix computePaymentMatrix(Random rng, DataSetCollection instances,
			int numAgents, int param1, int param2);

	void validateInput(int param1, int param2) throws PadParams;

}
