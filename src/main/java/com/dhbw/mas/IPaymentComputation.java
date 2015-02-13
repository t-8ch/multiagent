package com.dhbw.mas;


public interface IPaymentComputation {
	
	PaymentMatrix computePaymentMatrix(DataSetCollection instances, int numAgents, 
			int param1, int param2);
	
}
