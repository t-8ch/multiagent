package com.dhbw.mas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PaymentMatrix {
	private int numInstanceJobs = 0;
	private int numAgents = 0;
	private int[][] paymentMatrix = null;
	
	/**
	 * Creates an empty payment matrix.
	 * 
	 * @param numInstanceJobs
	 * @param numAgents
	 */
	public PaymentMatrix(int numJobs, int numAgents) {
		initializeMatrix(numJobs, numAgents);
	}
	
	/**
	 * Creates a payment matrix based on a agent job mapping.
	 * @param agentJobMap
	 * @throws Exception 
	 */
	public PaymentMatrix(Map<Integer, List<Integer>> agentJobMap) throws Exception {
		validatePaymentMap(agentJobMap);
		generatePaymentMatrixFromAgentJobMap(agentJobMap);
		validatePaymentMatrix();
	}
	
	/**
	 * Creates a filled payment matrix from payment stream files.
	 * 
	 * @param paymentFilenames
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public PaymentMatrix(String... paymentFilenames) throws Exception {
		Map<Integer, List<Integer>> paymentMap = generateJobAgentMapFromPaystreamFiles(paymentFilenames);
		validatePaymentMap(paymentMap);
		generatePaymentMatrixFromAgentJobMap(paymentMap);
		validatePaymentMatrix();
	}

	private Map<Integer, List<Integer>> generateJobAgentMapFromPaystreamFiles(
			String... paymentFilenames) throws FileNotFoundException,
			IOException {
		Map<Integer, List<Integer>> jobAgentMap = new HashMap<Integer, List<Integer>>();
		int paymentFileCtr = 0;
		for(String paymentFile : paymentFilenames) {
			jobAgentMap.put(paymentFileCtr, new ArrayList<Integer>());
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(paymentFile));
				String lineContent = null;
				while (null != (lineContent = br.readLine())) {
					if(lineContent.isEmpty()) {
						continue;
					}
					int payment = Integer.parseInt(lineContent);
					jobAgentMap.get(paymentFileCtr).add(payment);
				}
			} finally {
				if(br != null) {
					br.close();
				}
			}
			++paymentFileCtr;
		}
		return jobAgentMap;
	}
	
	public void writeAgentPaymentValuesToFiles(File directory, String filenamePrefix) throws Exception {
		if(!directory.isDirectory()) {
			throw(new Exception("Given directory string for paystream output is not a folder."));
		}
		for(int i = 0; i < getNumAgents(); ++i) {
			writeAgentPaymentValuesToFile(i, directory.getCanonicalPath() + "/" + filenamePrefix + i + ".in");
		}
	}
	
	public void writeAgentPaymentValuesToFile(int agentId, String paymentFilename) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(paymentFilename));
			for(int i = 0; i < getNumInstanceJobs(); ++i) {
				if(getAssignedAgentForJob(i) == agentId) {
					bw.write(new Integer(getPaymentValueForJob(i)).toString());
				} else {
					bw.write("0");
				}
				if(i < getNumInstanceJobs() - 1) {
					bw.newLine();
				}
			}
			bw.flush();
		} finally {
			if(bw != null) {
				bw.close();
			}
		}
		
	}

	public void generatePaymentMatrixFromAgentJobMap(Map<Integer, List<Integer>> paymentMap) {
		initializeMatrix(paymentMap.get(0).size(), paymentMap.size());
		for(int i = 0; i < paymentMap.size(); ++i) {
			for(int j = 0; j < paymentMap.get(i).size(); ++j) {
				paymentMatrix[j][i] = paymentMap.get(i).get(j);
			}
		}
	}
	
	/**
	 * Validates the payment matrix and throws an exception if invalid.
	 * 
	 * @throws Exception
	 */
	public void validatePaymentMatrix() throws Exception {
		int sum = 0;
		for(int j = 0; j < numInstanceJobs; ++j) {
			boolean validRow = false;
			for(int i = 0; i < numAgents; ++i) {
				int paymentValue = paymentMatrix[j][i];
				if(paymentValue != 0) {
					if(!validRow) {
						validRow = true;
					} else {
						throw(new Exception("Multiple payment values in different agents for one job."));
					}
				}
				if((j == 0 || j == (numInstanceJobs - 1))) {
					if(paymentValue != 0) {
						throw(new Exception("Dummy job has non-zero payment value for agent " + i));
					} else {
						validRow = true;
					}
				}
				sum += paymentValue;
			}
			if(!validRow) {
				throw(new Exception("Job " + j + " has no payment value for all agents."));
			}
		}
		if(sum <= 0) {
			throw(new Exception("Non-positive sum for payment jobs overall."));
		}
	}

	private void validatePaymentMap(Map<Integer, List<Integer>> paymentMap) throws Exception {
		if(paymentMap.size() < 1) {
			throw(new Exception("Payment map contains no payment streams"));
		}
		Iterator<Entry<Integer, List<Integer>>> iterator = paymentMap.entrySet().iterator();
		int jobSize = -1;
		while(iterator.hasNext()) {
			if(jobSize == -1) {
				jobSize = iterator.next().getValue().size();
			} else {
				if(jobSize != iterator.next().getValue().size()) {
					throw(new Exception("Payment streams differ in job sizes."));
				}
			}
		}
	}

	private void initializeMatrix(int numJobs, int numAgents) {
		this.numInstanceJobs = numJobs;
		this.numAgents = numAgents;
		paymentMatrix = new int[numJobs][numAgents];
		for(int i = 0; i < numJobs; ++i) {
			for(int j = 0; j < numAgents; ++j) {
				paymentMatrix[i][j] = 0;
			}
		}
	}
	
	public int getPaymentValueForJob(int jobId) {
		for(int i = 0; i < numAgents; ++i) {
			if(paymentMatrix[jobId][i] != 0) {
				return paymentMatrix[jobId][i];
			}
		}
		return 0;
	}
	
	public int getAssignedAgentForJob(int jobId) {
		for(int i = 0; i < numAgents; ++i) {
			if(paymentMatrix[jobId][i] != 0) {
				return i;
			}
		}
		return -1;
	}
	
	public void updatePayment(int numJob, int numAgent, int paymentValue) {
		paymentMatrix[numJob][numAgent] = paymentValue;
	}
	
	public String getPaymentFileOutputForAgent(int agentNum) {
		String payments = "";
		for(int i = 0; i < numInstanceJobs; ++i) {
			payments += paymentMatrix[i];
			if(i < numInstanceJobs - 1) {
				payments += "\n";
			}
		}
		return payments;
	}
	
	public String toString() {
		String out = "";
		for(int i = 0; i < numInstanceJobs; ++i) {
			for(int j = 0; j < numAgents; ++j) {
				out += paymentMatrix[i][j] + "   ";
			}
			out += "\n";
		}
		return out;
	}

	public int getNumInstanceJobs() {
		return numInstanceJobs;
	}

	public int getNumAgents() {
		return numAgents;
	}
	
}
