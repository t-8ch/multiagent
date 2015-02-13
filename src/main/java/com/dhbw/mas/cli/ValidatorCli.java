package com.dhbw.mas.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.dhbw.mas.PaymentMatrix;
import com.dhbw.mas.Validator;

public class ValidatorCli {
	
	@Parameter(names={"--help", "-h"}, help = true)
	private boolean help;
	
	@Parameter(names={"--paymentmatrix", "-p"}, required=true, converter=PaymentMatrixConverter.class)
	private PaymentMatrix paymentmatrix;
	
	@ParametersDelegate
	private DataSetParams dsp = new DataSetParams();


	private void run() throws Exception {
		new Validator().validate(dsp.datasets, paymentmatrix);
	}
	
	public static void main(String[] args) throws Exception {
		ValidatorCli validator = new ValidatorCli();
		JCommander c = new JCommander(validator, args);
		c.setProgramName(System.getProperty("app.name",
				ValidatorCli.class.getCanonicalName()));

		if (validator.help) {
			c.usage();
		} else {
			validator.run();
		}
	}

}
