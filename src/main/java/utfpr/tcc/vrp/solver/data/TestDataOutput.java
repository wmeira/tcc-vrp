package utfpr.tcc.vrp.solver.data;

import ilog.opl.IloOplModel;

import java.util.List;

import utfpr.tcc.vrp.model.Scenario;

public class TestDataOutput {
	
	private IloOplModel oplModel;
	private Scenario scenario;
	private List<Object> results;
	
	public TestDataOutput(IloOplModel oplModel, Scenario scenario, List<Object> results) {

		super();
		this.oplModel = oplModel;
		this.results = results;
		this.scenario = scenario;
	}
	
	public void FulfillResults() {
		
		int t; 
		
		
		int x1 = oplModel.getElement("x1").asInt();
		int x2 = oplModel.getElement("x2").asInt();
		int r = oplModel.getElement("r").asInt();
		
		
		results.add(x1);
		results.add(x2);
		results.add(r);
	}
}
