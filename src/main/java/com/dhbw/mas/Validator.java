package com.dhbw.mas;

public class Validator {
	
	private static String[] class1instances = {
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
		"./RCP/30/J306_1.RCP"
	};
	private static String[] class1payments = {
		"./Zahlungsströme/30/cf301_1.in",
		"./Zahlungsströme/30/cf302_1.in"
	};
	
	private static String[] class2instances = {
		"./RCP/60/J6010_1.RCP", 
		"./RCP/60/J6014_1.RCP",
		"./RCP/60/J6018_1.RCP",
		"./RCP/60/J602_1.RCP",
		"./RCP/60/J6022_1.RCP",
		"./RCP/60/J6026_1.RCP",
		"./RCP/60/J6030_1.RCP",
		"./RCP/60/J6034_1.RCP",
		"./RCP/60/J6038_1.RCP",
		"./RCP/60/J6042_1.RCP",
		"./RCP/60/J6046_1.RCP",
		"./RCP/60/J606_1.RCP"
	};
	private static String[] class2payments = {
		"./Zahlungsströme/60/cf601_1.in",
		"./Zahlungsströme/60/cf602_1.in"
	};
	
	private static String[] class3instances = {
		"./RCP/120/X10_1.RCP", 
		"./RCP/120/X15_1.RCP",
		"./RCP/120/X20_1.RCP",
		"./RCP/120/X25_1.RCP",
		"./RCP/120/X30_1.RCP",
		"./RCP/120/X35_1.RCP",
		"./RCP/120/X40_1.RCP",
		"./RCP/120/X45_1.RCP",
		"./RCP/120/X5_1.RCP",
		"./RCP/120/X50_1.RCP",
		"./RCP/120/X55_1.RCP",
		"./RCP/120/X60_1.RCP"
	};
	private static String[] class3payments = {
		"./Zahlungsströme/120/cf1201_1.in",
		"./Zahlungsströme/120/cf1202_1.in"
	};
	
	public static void main(String[] args) throws Exception {
		DataSetCollection dataSetCollectionClass1 = new DataSetCollection(class1instances);
		PaymentMatrix paymentMatrixClass1 = new PaymentMatrix(class1payments);
		DataSetCollection dataSetCollectionClass2 = new DataSetCollection(class2instances);
		PaymentMatrix paymentMatrixClass2 = new PaymentMatrix(class2payments);
		DataSetCollection dataSetCollectionClass3 = new DataSetCollection(class3instances);
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
