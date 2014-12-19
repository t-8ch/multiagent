package com.dhbw.mas;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataSetInstance implements Iterable<Job>,Collection<Job> {
	private final List<Job> jobList = new ArrayList<Job>();

	private int[] ressourceMax;
	private int durationSum;
	private String instanceFilename = "";
	
	/**
	 * creates a job list from a file
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public DataSetInstance(final Reader data, final String instanceFilename) throws Exception {
		this.instanceFilename = instanceFilename;
		processInstanceFile(data);
		validateJobList();
	}

	private void processInstanceFile(final Reader data) throws Exception, IOException {
		BufferedReader br = null;
		int durationsum = 0;
		int jobCount = 0;
		int ressourceCount = 0;
		int[] ressourceMax = {};
		try {
			br = new BufferedReader(data);
	
			// Read Headline
			// Expect Integers two in 8 byte char sequences
			final String headLine = br.readLine();
			if (headLine == null) {
				throw new Exception(
						"This is not a valid RCP file. HeadLine is missing");
			}
	
			try {
				jobCount = Integer.parseInt(get8Substring(0, headLine));
				ressourceCount = Integer.parseInt(get8Substring(1, headLine));
			} catch (final Exception e) {
				throw new Exception(
						"This is not a valid RCP file. HeadLine is corrupt. Expecting two 8 charsequences containing numbers!");
			}
	
			// Read RessourceLine
			// Expect as much ressources we read in Headline (8byte char sequences)
			final String ressourceLine = br.readLine();
			if (ressourceLine == null) {
				throw new Exception(
						"This is not a valid RCP file. RessourceLine is missing");
			}
			int offset = 0;
			ressourceMax = new int[ressourceCount];
			try {
				for (int i = 0; i < ressourceCount; i++) {
					ressourceMax[i] = Integer.parseInt(get8Substring(offset++,
							ressourceLine));
				}
			} catch (final Exception e) {
				throw new Exception(
						"This is not a valid RCP file. RessourceLine is corrupt. Expecting "
								+ ressourceCount
								+ " charsequences containing numbers!");
			}
	
			// reading all jobs
			// we expect the exact job count otherwise error!
			for (int jobid = 0; jobid < jobCount; jobid++) {
				final String jobLine = br.readLine();
				if (jobLine == null) {
					throw new Exception(
							"This is not a valid RCP file. At least one jobline is missing. Missing for Job: "
									+ jobid);
				}
	
				final Job j = new Job(jobid, jobLine, ressourceCount);
				this.jobList.add(j);
	
				durationsum += j.getTotalduration();
			}
			// no more lines expected
			if (br.readLine() != null) {
				throw new Exception(
						"This file is corrupt. We have at least one line to much!");
			}
			
			// now link the jobs
			// this means we are transforming from numbers into references to
			// classes!
			for (final Job j : this.jobList) {
				j.link(this.jobList);
			}
		} catch(Exception ee) {
			throw new Exception("General exception: " + ee.getMessage());
		} finally {
			if(br != null) {
				br.close();
			}
		}
		this.ressourceMax = ressourceMax;
		this.durationSum = durationsum;
	}
	
	private void validateJobList() throws Exception {
		if(jobList.get(0).getPredecessorCount() != 0) {
			throw(new Exception("Invalid predecessor for initial dummy job"));
		}
		if(jobList.get(jobList.size() - 1).getSuccessorCount() != 0) {
			throw(new Exception("Invalid successor for last dummy job"));
		}
		for(int i = 1; i < jobList.size() - 1; ++i) {
			if(getJob(i).getSuccessorCount() < 1) {
				throw(new Exception("Invalid successor count for job " + i));
			}
			if(getJob(i).getPredecessorCount() < 1) {
				throw(new Exception("Invalid predecessor count for job " + i));
			}
			if(getJob(i).getTotalduration() < 1) {
				throw(new Exception("Invalid duration for job " + i));
			}
			if(!hasPath(0, i)) {
				throw(new Exception("Job with id " + i + " has no path from initial dummy job."));
			}
			if(!hasPath(i, jobList.size() - 1)) {
				throw(new Exception("Job with id " + i + " has no path to end dummy job."));
			}
		}
	}

	public List<Job> getJobList() {
		return jobList;
	}

	private String get8Substring(final int nr, final String str) {
		return str.substring(nr * 8, nr * 8 + 8).trim();
	}

	public Job getJob(final int i) {
		return this.jobList.get(i);
	}

	public int getJobCount() {
		return this.jobList.size();
	}

	public int getRessourceCount() {
		return this.ressourceMax.length;
	}

	public int getRessourceMax(final int i) {
		return this.ressourceMax[i];
	}

	public int[] getRessourceMaxArray() {
		return this.ressourceMax;
	}

	public int getDurationSum() {
		return this.durationSum;
	}

	public Iterator<Job> iterator() {
		return this.jobList.iterator();
	}

	public boolean add(Job arg0) {
		return this.jobList.add(arg0);
	}

	public boolean addAll(Collection<? extends Job> arg0) {
		return this.jobList.addAll(arg0);
	}

	public void clear() {
		this.jobList.clear();
		
	}

	public boolean contains(Object arg0) {
		return this.jobList.contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return this.jobList.containsAll(arg0);
	}

	public boolean isEmpty() {
		return this.jobList.isEmpty();
	}

	public boolean remove(Object arg0) {
		return this.jobList.remove(arg0);
	}

	public boolean removeAll(Collection<?> arg0) {
		return this.jobList.removeAll(arg0);
	}

	public boolean retainAll(Collection<?> arg0) {
		return this.jobList.retainAll(arg0);
	}

	public int size() {
		return this.jobList.size();
	}

	public Object[] toArray() {
		return this.jobList.toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		return this.jobList.toArray(arg0);
	}
	
	/*
	 * Implements depth search
	 */
	public boolean hasPath(int startJobId, int endJobId) {
		if(startJobId == endJobId) {
			return true;
		}
		Job startJob = getJob(startJobId);
		for(Job successor : startJob.getSuccessors()) {
			if(hasPath(successor.getJobId(), endJobId)) {
				return true;
			}
		}
		return false;
	}
	
	public List<List<Job>> getValidPathsBetweenJobs(int startJobId, int endJobId) {
		List<List<Job>> pathsList = new ArrayList<List<Job>>();
		if(!hasPath(startJobId, endJobId)) {
			return pathsList;
		}
		
		List<Job> pathStack = new ArrayList<Job>();
		pathStack.add(getJob(startJobId));
		
		computeValidPathsBetweenJobs(startJobId, endJobId, pathsList, pathStack);
		
		return pathsList;
	}
	
	private void computeValidPathsBetweenJobs(int startJobId, int endJobId, List<List<Job>> pathsList, List<Job> pathStack) {
		List<Job> newPathStack = new ArrayList<Job>(pathStack);
		if(startJobId == endJobId) {
			pathsList.add(newPathStack);
		} else {
			Job startJob = getJob(startJobId);
			for(Job successor : startJob.getSuccessors()) {
				if(hasPath(successor.getJobId(), endJobId)) {
					newPathStack.add(getJob(successor.getJobId()));
					computeValidPathsBetweenJobs(successor.getJobId(), endJobId, pathsList, newPathStack);
					newPathStack.remove(newPathStack.size() - 1);
				}
			}
		}
	}
	
	public List<Job> getJobListForAgent(int agentId) {
		List<Job> agentJobList = new ArrayList<Job>();
		for(Job job : jobList) {
			if(job.getAssignedAgent() == agentId) {
				agentJobList.add(job);
			}
		}
		return agentJobList;
	}
	
	public void validatePaymentSums() throws Exception {
		if(getJobsPaymentSum() < 0.0) {
			throw(new Exception("Overall payment sum for instance is not positive: " + getJobsPaymentSum()));
		}
		
		List<Integer> testedAgents = new ArrayList<Integer>();
		for(Job job : jobList) {
			if(job.getAssignedAgent() >= 0 && !testedAgents.contains(job.getAssignedAgent())) {
				validateAgentPaymentSums(job.getAssignedAgent());
				testedAgents.add(job.getAssignedAgent());
			}
		}
	}
	
	private double getJobsPaymentSum() {
		double sum = 0.0;
		for(Job job : jobList) {
			sum += job.getPayment();
		}
		return sum;
	}
	
	private void validateAgentPaymentSums(int agentId) throws Exception {
		validateJobList();
		List<Job> agentJobList = getJobListForAgent(agentId);
		
		//each agent job must have a positive payment sum for its jobs
		double jobPaymentSumForAgent = 0.0;
		for(Job agentJob : agentJobList) {
			jobPaymentSumForAgent += agentJob.getPayment();
		}
		if(jobPaymentSumForAgent <= 0.0) {
			throw(new Exception("Agent " + agentId + " has non-positive payment sum " + jobPaymentSumForAgent + " for all agent jobs in instance " + instanceFilename));
		}
		
		for(List<Job> validPath : getValidPathsBetweenJobs(0, jobList.size() - 1)) {
			double currentPaymentSumForPath = 0.0;
			for(Job pathJob : validPath) {
				if(pathJob.getAssignedAgent() == agentId) {
					currentPaymentSumForPath += pathJob.getPayment();
				}
			}
			if(currentPaymentSumForPath < 0.0) {
				System.out.println("Agent " + agentId + " has non-positive payment sum " + currentPaymentSumForPath + " for path " + validPath + " in instance " + instanceFilename);
				//throw(new Exception("Agent " + agentId + " has non-positive payment sum for path " + validPath + " in instance " + instanceFilename));
			}
		}	
	}
	
	public PaymentMatrix generatePaymentMatrix() throws Exception {
		Map<Integer, List<Integer>> agentJobMap = new HashMap<Integer, List<Integer>>();
		for(int i = 0; i < jobList.size(); ++i) {
			if(getJob(i).getAssignedAgent() >= 0 && !agentJobMap.containsKey(getJob(i).getAssignedAgent())) {
				agentJobMap.put(i, new ArrayList<Integer>());
			}
		}
		for(int i = 0; i < jobList.size(); ++i) {
			int assignedAgentId = getJob(i).getAssignedAgent();
			for(Integer agentId : agentJobMap.keySet()) {
				if(agentId == assignedAgentId) {
					agentJobMap.get(agentId).add((int)getJob(i).getPayment());
				} else {
					agentJobMap.get(agentId).add(0);
				}
			}
		}
		return new PaymentMatrix(agentJobMap);
	}
	
}
