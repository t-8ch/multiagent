package com.dhbw.mas.cli;

import com.beust.jcommander.IStringConverter;
import com.dhbw.mas.PaymentMatrix;

public class PaymentMatrixConverter implements IStringConverter<PaymentMatrix> {

	@Override
	public PaymentMatrix convert(String value) {
		try {
			int paymentmatrix = Integer.valueOf(value);

			switch (paymentmatrix) {
			case 30:
				return PaymentMatrix.getBuiltinPaymentMatrix30();
			case 60:
				return PaymentMatrix.getBuiltinPaymentMatrix60();
			case 120:
				return PaymentMatrix.getBuiltinPaymentMatrix120();
			}
		} catch (Exception e) {
			/* Just interpret it as file */
		}
		
		throw new RuntimeException(
				String.format("Unsupported builtin paymentmatrix %s (loading from directory not yet implemented", value));
	}
}
