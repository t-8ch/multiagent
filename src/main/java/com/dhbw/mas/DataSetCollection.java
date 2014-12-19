package com.dhbw.mas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.InputStreamReader;

public class DataSetCollection {
	private List<DataSetInstance> instanceList = new ArrayList<DataSetInstance>();
	private int instanceJobCount = 0;
	
	private static DataSetCollection getBuiltinDataSets(final String subdir, final String builtins[]) throws Exception {
		List<DataSetInstance> instances = new ArrayList<>();

		for(String builtin: builtins) {
			instances.add(new DataSetInstance(new InputStreamReader(
						DataSetCollection.class.getClassLoader().getResourceAsStream("RCP/" + subdir + "/" + builtin + ".RCP")), builtin));
		}
		return new DataSetCollection(instances);
	}

	public static DataSetCollection getBuiltinDataSets30() throws Exception {
		return getBuiltinDataSets("30", new String[] {
			"J3010_1",
			"J3014_1",
			"J3018_1",
			"J3022_1",
			"J3026_1",
			"J302_1",
			"J3030_1",
			"J3034_1",
			"J3038_1",
			"J3042_1",
			"J3046_1",
			"J306_1",
		});
	}

	public static DataSetCollection getBuiltinDataSets60() throws Exception {
		return getBuiltinDataSets("60", new String[] {
			"J6010_1",
			"J6014_1",
			"J6018_1",
			"J6022_1",
			"J6026_1",
			"J602_1",
			"J6030_1",
			"J6034_1",
			"J6038_1",
			"J6042_1",
			"J6046_1",
			"J606_1",
		});
	}

	public static DataSetCollection getBuiltinDataSets120() throws Exception {
		return getBuiltinDataSets("120", new String[] {
			"X10_1",
			"X15_1",
			"X20_1",
			"X25_1",
			"X30_1",
			"X35_1",
			"X40_1",
			"X45_1",
			"X50_1",
			"X55_1",
			"X5_1",
			"X60_1",
		});
	}

	private DataSetCollection(final List<DataSetInstance> instanceList) throws Exception {
		this.instanceList = instanceList;
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
