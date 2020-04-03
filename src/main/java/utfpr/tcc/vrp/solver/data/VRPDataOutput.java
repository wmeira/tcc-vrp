package utfpr.tcc.vrp.solver.data;

import java.io.IOException;
import java.util.List;

import ilog.concert.IloException;
import ilog.opl.IloOplModel;
import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.solver.model.MathematicalModel;

public class VRPDataOutput implements DataOutput {
	
	private MathematicalModel model;
	private Scenario scenario;
	private List<Object> results;
	
	public VRPDataOutput(MathematicalModel model, Scenario scenario, List<Object> results) {

		this.model = model;
		this.scenario = scenario;	
		this.results = results;
	}
	
	public void exportOpl(IloOplModel oplModel) throws IOException, IloException {
		
		switch (model) {
			case VRPMPDTW: {
				VRPMPDTWDataOutput dataOutput = new VRPMPDTWDataOutput(oplModel, scenario, results);
				dataOutput.FulfillResults();
				break;
			}
			default: {
				return;
			}
		}
	}
	
}
