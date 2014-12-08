package com.dhbw.mas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataSetCollection {
	private List<DataSetInstance> instanceList = new ArrayList<DataSetInstance>();
	private int instanceJobCount = 0;
	
	public DataSetCollection(String... instanceFilenames) throws Exception {
		for(String instanceFilename : instanceFilenames) {
			instanceList.add(new DataSetInstance(instanceFilename));
		}
		validateInstances();
		instanceJobCount = instanceList.get(0).getJobCount();
	}

	private void validateInstances() throws Exception {
		if(instanceList == null || instanceList.size() == 0) {
			throw(new Exception("Instance collection contains no instances"));
		}
		
		int currentJobSize = 0;
		for(DataSetInstance instance : instanceList) {
			if(currentJobSize <= 0) {
				currentJobSize = instance.getJobCount();
			} else if(currentJobSize != instance.getJobCount()) {
				throw(new Exception("Instance collection has different job sizes"));
			}
		}
	}
	
	public void validatePayments() throws Exception {
		for(DataSetInstance instance : instanceList) {
			instance.validatePaymentSums();
		}
	}
	
	public boolean hasPathInAllInstances(int startJobId, int endJobId) {
		for(int instCtr = 0; instCtr < instanceList.size(); ++instCtr) {
			if(!instanceList.get(instCtr).hasPath(startJobId, endJobId)) {
				return false;
			}
		}
		return true;
	}
	
	public Map<Integer, Double> getJobProbabilityDistributionInPath(int startJobId, int endJobId) {
		List<List<Job>> paths = new ArrayList<List<Job>>();
		for(int instCtr = 0; instCtr < instanceList.size(); ++instCtr) {
			paths.addAll(instanceList.get(instCtr).getValidPathsBetweenJobs(startJobId, endJobId));
		}
		List<Job> jobs = Job.getJobListsUnion(paths);
		
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		for(Job job : jobs) {
			countMap.put(job.getJobId(), 0);
		}
		
		int pathCtr = 0;
		for(List<Job> path : paths) {
			for(Job job : path) {
				countMap.put(job.getJobId(), countMap.get(job.getJobId()) + 1);
			}
			++pathCtr;
		}
		Map<Integer, Double> probabilityMap = new HashMap<Integer, Double>();
		
		Iterator<Integer> jobIdIterator = countMap.keySet().iterator();
		while(jobIdIterator.hasNext()) {
			int jobId = jobIdIterator.next();
			probabilityMap.put(jobId, (double)(countMap.get(jobId)) / (double)pathCtr);
		}
		
		return probabilityMap;
	}
	
	public void updatePaymentsFromPaymentMatrix(PaymentMatrix paymentMatrix) {
		for(DataSetInstance instance : instanceList) {
			for(Job job : instance.getJobList()) {
				job.setPayment(paymentMatrix.getPaymentValueForJob(job.getJobId()));
				job.setAssignedAgent(paymentMatrix.getAssignedAgentForJob(job.getJobId()));
			}
		}
	}

	public List<DataSetInstance> getInstanceList() {
		return instanceList;
	}

	public int getInstanceJobCount() {
		return instanceJobCount;
	}
	
}
