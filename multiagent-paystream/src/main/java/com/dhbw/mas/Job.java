package com.dhbw.mas;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Job {

	private final int id;
	private final List<Job> predecessors = new LinkedList<Job>();
	private final int[] ressourceUsage;
	private final int successorCount;
	private final List<Job> successors = new LinkedList<Job>();
	private int[] tmpsuccessors;
	private final int totalduration;
	private double payment = 0.0;
	private int assignedAgent = -1;

	/**
	 * Creates a Job due to manuel entered data
	 * 
	 * @param id
	 * @param totalduration
	 * @param ressourceUsage
	 * @param successorCount
	 * @param tmpsuccessors
	 */
	public Job(final int id, final int totalduration,
			final int[] ressourceUsage, final int successorCount,
			final int[] tmpsuccessors) {
		this.totalduration = totalduration;
		this.successorCount = successorCount;
		this.ressourceUsage = ressourceUsage;
		this.tmpsuccessors = tmpsuccessors;
		this.id = id;
	}

	/**
	 * Creates a job by using a line from RCP file
	 * 
	 * @param id
	 * @param jobString
	 * @param ressourceCount
	 */
	public Job(final int id, final String jobString, final int ressourceCount) {
		int offset = 0;
		this.id = id;
		this.totalduration = Integer
				.parseInt(get8Substring(offset++, jobString));
		this.ressourceUsage = new int[ressourceCount];
		for (int i = 0; i < ressourceCount; i++) {
			this.ressourceUsage[i] = Integer.parseInt(get8Substring((offset++),
					jobString));
		}
		this.successorCount = Integer.parseInt(get8Substring(offset++,
				jobString));
		this.tmpsuccessors = new int[this.successorCount];
		for (int i = 0; i < this.successorCount; i++) {
			this.tmpsuccessors[i] = Integer.parseInt(get8Substring(offset++,
					jobString));
		}

	}

	/**
	 * this is used by link to connect the predecessors do not use it elsewhere
	 * 
	 * @param predecessor
	 */
	protected void addPredecessor(final Job predecessor) {
		this.predecessors.add(predecessor);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Job) {
			return ((Job) obj).id == this.id;
		} else {
			return false;
		}
	}

	/**
	 * returns true if there is a downlink
	 */
	public boolean existDownLink(final int jobid) {
		for (final Job child : this.successors) {
			if (child.getJobId() == jobid) {
				return true;
			}
			return child.existDownLink(jobid);
		}
		return false; // hat keine kinder ^^
	}

	/**
	 * extracts a 8byte substring
	 * 
	 * @param nr
	 * @param str
	 * @return
	 */
	private String get8Substring(final int nr, final String str) {
		return str.substring(nr * 8, nr * 8 + 8).trim();
	}

	/**
	 * returns the job id
	 * 
	 * @return
	 */
	public int getJobId() {
		return this.id;
	}

	/**
	 * returns the predecessor of this job
	 * 
	 * @return
	 */
	public Job getPredecessor(final int i) {
		return this.predecessors.get(i);
	}

	/**
	 * returns
	 */
	public int getPredecessorCount() {
		return this.predecessors.size();
	}

	public List<Job> getPredecessors() {
		return this.predecessors;
	}

	/**
	 * returns ressource usage (start counting with 0)
	 * 
	 * @param i
	 * @return
	 */
	public int getRessourceUsage(final int i) {
		return this.ressourceUsage[i];
	}

	public int[] getRessourceUsages() {
		return this.ressourceUsage;
	}

	/**
	 * returns a successor start counting with 0
	 * 
	 * @param i
	 * @return
	 */
	public Job getSuccessor(final int i) {
		return this.successors.get(i);
	}

	/**
	 * returns the count of successors
	 * 
	 * @return
	 */
	public int getSuccessorCount() {
		return this.successorCount;
	}

	public List<Job> getSuccessors() {
		return this.successors;
	}

	/**
	 * returns total duration
	 * 
	 * @return
	 */
	public int getTotalduration() {
		return this.totalduration;
	}

	@Override
	public int hashCode() {
		return this.id % 10;
	}

	/**
	 * links the jobs do not use this!
	 * 
	 * @param jobs
	 * @throws Exception
	 */
	public void link(final List<Job> jobs) throws Exception {
		if (this.tmpsuccessors == null) {
			throw new Exception("Do not use link twice");
		}
		for (final int i : this.tmpsuccessors) {
			if (i > 0) {
				this.successors.add(jobs.get(i - 1));
				jobs.get(i - 1).addPredecessor(this);
			} else {
				throw new Exception("Cannot have 0 has successor!");
			}
		}
		this.tmpsuccessors = null;
	}

	@Override
	public String toString() {

		/*String jobString = "Job (" + this.id + ") TotalUseage: "
				+ this.totalduration + " Usage: ";
		for (final int i : this.ressourceUsage) {
			jobString += i + " ";
		}

		jobString += "\n SuccessorCount " + this.getSuccessorCount() + ": ";
		int i = 0;
		for (final Job successor : this.successors) {
			i++;
			jobString += "\n\t Successor" + i + ": " + successor.id;
		}
		jobString += "\n PredecessorCount " + this.getPredecessorCount() + ": ";
		i = 0;
		for (final Job predecessor : this.predecessors) {
			i++;
			jobString += "\n\t Predecessor" + i + ": " + predecessor.id;
		}

		jobString += "\n";

		return jobString;*/
		
		return "" + this.id;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

	public int getAssignedAgent() {
		return assignedAgent;
	}

	public void setAssignedAgent(int assignedAgent) {
		this.assignedAgent = assignedAgent;
	}
	
	public static List<Job> getJobListsUnion(List<List<Job>> jobLists) {
		Set<Job> set = new HashSet<Job>();
		
		for(List<Job> list : jobLists) {
			set.addAll(list);
		}
		
        return new ArrayList<Job>(set);
	}

}
