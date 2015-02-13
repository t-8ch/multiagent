package com.dhbw.mas;


public interface IPaymentComputation {
	
	PaymentMatrix computePaymentMatrix(DataSetCollection instances, int numAgents, 
			int minPaymentValue, int maxPaymentValue);
	
}
