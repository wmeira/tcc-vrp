package utfpr.tcc.vrp.model;

import java.util.List;

import utfpr.tcc.vrp.solver.model.MathematicalModel;

public class Solution {

	private Scenario scenario;
	private MathematicalModel mathematicalModel;
	private List<Route> routes;
	private double executionTime; //How much time it took to solve the scenario. In seconds.
	private String solutionStatus;
	
	private double totalDistanceTraveled = 0;
	private double totalTimeTraveled = 0;
	
	private List<String> solutionProblems;
	
	public Solution(Scenario scenario, 
					MathematicalModel mathematicalModel, 
					List<Route> routes, 
					double executionTime, 
					String solutionStatus, 
					List<String> solutionProblems) {
		
		this.scenario = scenario;
		this.mathematicalModel = mathematicalModel;
		this.routes = routes;
		this.executionTime = executionTime;
		this.solutionStatus = solutionStatus;
		this.solutionProblems = solutionProblems;
		
		calculateTotalDistanceTimeTraveled();
	}
	
	private void calculateTotalDistanceTimeTraveled() {
		totalDistanceTraveled = 0;
		totalTimeTraveled = 0;
		for(Route route : routes) {
			totalDistanceTraveled += route.getTotalDistanceTraveled();
			totalTimeTraveled += route.getTotalTimeTraveled();
		}
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public MathematicalModel getMathematicalModel() {
		return mathematicalModel;
	}

	public void setMathematicalModel(MathematicalModel mathematicalModel) {
		this.mathematicalModel = mathematicalModel;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public double getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}

	public double getTotalDistanceTraveled() {
		return totalDistanceTraveled;
	}

	public void setTotalDistanceTraveled(double totalDistanceTraveled) {
		this.totalDistanceTraveled = totalDistanceTraveled;
	}

	public double getTotalTimeTraveled() {
		return totalTimeTraveled;
	}

	public void setTotalTimeTraveled(double totalTimeTraveled) {
		this.totalTimeTraveled = totalTimeTraveled;
	}

	public String getSolutionStatus() {
		return solutionStatus;
	}

	public void setSolutionStatus(String solutionStatus) {
		this.solutionStatus = solutionStatus;
	}

	public List<String> getSolutionProblems() {
		return solutionProblems;
	}

	public void setSolutionProblems(List<String> solutionProblems) {
		this.solutionProblems = solutionProblems;
	}
}
