package com.dhbw.mas;

public class Validator {
	public void validate(DataSetCollection dataSetCollection, PaymentMatrix paymentMatrix) {
		dataSetCollection.updatePaymentsFromPaymentMatrix(paymentMatrix);
		try {
			dataSetCollection.validatePayments();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}
