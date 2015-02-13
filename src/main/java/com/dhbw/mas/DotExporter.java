package com.dhbw.mas;

public class DotExporter {
	
	public static String generateDotRepresentation(DataSetInstance instance, 
			PaymentMatrix paymentMatrix, String name) {
		String output = "digraph \"" + name + "\" {\n";
		
		for(int i = 0; i < instance.getJobCount(); ++i) {
			Job currentJob = instance.getJob(i);
			for(Job successor : instance.getJob(i).getSuccessors()) {
				output += "\t" + currentJob.getJobId() + " ->" + successor.getJobId() + ";\n";
			}
		}
		
		for(int i = 0; i < instance.getJobCount(); ++i) {
			String colorcode = "";
			if(paymentMatrix.getAssignedAgentForJob(i) < 0) {
				colorcode = "gold1";
			} else {
				colorcode = "grey" + (20 + paymentMatrix.getAssignedAgentForJob(i) * 5);
			}
			
			output += i + " [label=\"" + i + "(" + paymentMatrix.getPaymentValueForJob(i) + ")" + 
					"\",color=" + colorcode + ",penwidth=6];\n";
		}
		
		
		output += "}";
		
		return output;
	}
	
}
