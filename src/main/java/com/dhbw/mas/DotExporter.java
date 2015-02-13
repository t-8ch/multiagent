package com.dhbw.mas;

import java.awt.Color;

public class DotExporter {
	
	public static String generateDotRepresentation(DataSetInstance instance, 
			PaymentMatrix paymentMatrix, String name) {
		Color[] colors = generateColors(paymentMatrix.getNumAgents());
		String output = "digraph \"" + name + "\" {\n";
		
		for(int i = 0; i < instance.getJobCount(); ++i) {
			Job currentJob = instance.getJob(i);
			for(Job successor : instance.getJob(i).getSuccessors()) {
				output += "\t" + currentJob.getJobId() + " ->" + successor.getJobId() + ";\n";
			}
		}
		
		for(int i = 0; i < instance.getJobCount(); ++i) {
			if(paymentMatrix.getAssignedAgentForJob(i) < 0) {
				output += i + " [label=\"" + i + "\",penwidth=6,shape=box];\n";
			} else {
				int r = colors[paymentMatrix.getAssignedAgentForJob(i)].getRed();
				int g = colors[paymentMatrix.getAssignedAgentForJob(i)].getGreen();
				int b = colors[paymentMatrix.getAssignedAgentForJob(i)].getBlue();
				String colorcode = String.format("%f,%f,%f", r / 255.0f, g / 255.0f, b / 255.0f);
				
				output += i + " [label=\"" + i + "(" + paymentMatrix.getPaymentValueForJob(i) + ")\\n" + "Agent " + 
						paymentMatrix.getAssignedAgentForJob(i) + "\",color=\"" + colorcode + "\",penwidth=6];\n";
			}
			
			
		}
		
		
		output += "}";
		
		return output;
	}
	
	private static Color[] generateColors(int n) {
		Color[] cols = new Color[n];
		for(int i = 0; i < n; i++) {
			cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, 1.0f);
		}
		return cols;
	}
	
}
