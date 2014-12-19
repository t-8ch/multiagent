package com.dhbw.mas;

public class Validator {
	
	private static String[] class1payments = {
		"./Zahlungsströme/30/cf301_1.in",
		"./Zahlungsströme/30/cf302_1.in"
	};
	
	private static String[] class2payments = {
		"./Zahlungsströme/60/cf601_1.in",
		"./Zahlungsströme/60/cf602_1.in"
	};

	private static String[] class3payments = {
		"./Zahlungsströme/120/cf1201_1.in",
		"./Zahlungsströme/120/cf1202_1.in"
	};
	
	public static void main(String[] args) throws Exception {
		DataSetCollection dataSetCollectionClass1 = DataSetCollection.getBuiltinDataSets30();
		PaymentMatrix paymentMatrixClass1 = new PaymentMatrix(class1payments);
		DataSetCollection dataSetCollectionClass2 = DataSetCollection.getBuiltinDataSets60();
		PaymentMatrix paymentMatrixClass2 = new PaymentMatrix(class2payments);
		DataSetCollection dataSetCollectionClass3 = DataSetCollection.getBuiltinDataSets120();
		PaymentMatrix paymentMatrixClass3 = new PaymentMatrix(class3payments);
		
		new Validator().validate(dataSetCollectionClass1, paymentMatrixClass1);
		new Validator().validate(dataSetCollectionClass2, paymentMatrixClass2);
		new Validator().validate(dataSetCollectionClass3, paymentMatrixClass3);
	}

	public void validate(DataSetCollection dataSetCollection, PaymentMatrix paymentMatrix) {
		dataSetCollection.updatePaymentsFromPaymentMatrix(paymentMatrix);
		try {
			dataSetCollection.validatePayments();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}
