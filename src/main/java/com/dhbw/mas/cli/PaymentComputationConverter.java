package com.dhbw.mas.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.dhbw.mas.AscedingPaymentComputation;
import com.dhbw.mas.IPaymentComputation;
import com.dhbw.mas.RandomPaymentComputation;

public class PaymentComputationConverter implements
		IStringConverter<IPaymentComputation> {

	@Override
	public IPaymentComputation convert(String value) {
		switch (value) {
		case "ascending":
			return new AscedingPaymentComputation();
		case "random":
			return new RandomPaymentComputation();
		default:
			throw new ParameterException("Invalid PaymentComputation method: " + value);
		}
	}

}
