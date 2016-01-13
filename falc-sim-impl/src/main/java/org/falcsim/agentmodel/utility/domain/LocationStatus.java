package org.falcsim.agentmodel.utility.domain;

public class LocationStatus {
	
	private int numOfResidentsStart;
	private int numOfWorkersStart;
	
	private int numOfResidentsFuture;
	private int numOfWorkersFuture;

	private int numOfResidentsBeforeMove;
	private int numOfWorkersBeforeMove;
	
	private int numOfResidentsActualState;
	private int numOfWorkersActualState;
	
	public synchronized void updateCurrentResidents(int val){
		numOfResidentsActualState += val;
	}
	public synchronized void updateCurrentWorkers(int val){
		numOfWorkersActualState += val;
	}
	
	public int getNumOfResidentsStart() {
		return numOfResidentsStart;
	}
	public void setNumOfResidentsStart(int numOfResidentsStart) {
		this.numOfResidentsStart = numOfResidentsStart;
	}
	public int getNumOfWorkersStart() {
		return numOfWorkersStart;
	}
	public void setNumOfWorkersStart(int numOfWorkersStart) {
		this.numOfWorkersStart = numOfWorkersStart;
	}

	public int getNumOfResidentsFuture() {
		return numOfResidentsFuture;
	}
	public void setNumOfResidentsFuture(int numOfResidentsFuture) {
		this.numOfResidentsFuture = numOfResidentsFuture;
	}
	public int getNumOfWorkersFuture() {
		return numOfWorkersFuture;
	}
	public void setNumOfWorkersFuture(int numOfWorkersFuture) {
		this.numOfWorkersFuture = numOfWorkersFuture;
	}

	public int getNumOfResidentsBeforeMove() {
		return numOfResidentsBeforeMove;
	}
	public void setNumOfResidentsBeforeMove(int numOfResidentsBeforeMove) {
		this.numOfResidentsBeforeMove = numOfResidentsBeforeMove;
	}
	public int getNumOfWorkersBeforeMove() {
		return numOfWorkersBeforeMove;
	}
	public void setNumOfWorkersBeforeMove(int numOfWorkersBeforeMove) {
		this.numOfWorkersBeforeMove = numOfWorkersBeforeMove;
	}

	public int getNumOfResidentsActualState() {
		return numOfResidentsActualState;
	}
	public void setNumOfResidentsActualState(int numOfResidentsActualState) {
		this.numOfResidentsActualState = numOfResidentsActualState;
	}
	public int getNumOfWorkersActualState() {
		return numOfWorkersActualState;
	}
	public void setNumOfWorkersActualState(int numOfWorkersActualState) {
		this.numOfWorkersActualState = numOfWorkersActualState;
	}

	
}
