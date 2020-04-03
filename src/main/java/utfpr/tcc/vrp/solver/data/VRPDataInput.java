package utfpr.tcc.vrp.solver.data;

import ilog.opl.IloOplDataSource;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;

import java.io.IOException;

import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.solver.model.MathematicalModel;

public class VRPDataInput implements DataInput {
	
	private MathematicalModel model;
	private Scenario scenario;
	
	public VRPDataInput(MathematicalModel model, Scenario scenario) {

		this.model = model;
		this.scenario = scenario;	
	}

	public void importOpl(IloOplModel oplModel) throws IOException {
		
		IloOplFactory oplFactory = IloOplFactory.getOplFactoryFrom(oplModel);
		IloOplDataSource dataInput = null;
		
		switch (model) {
			case VRPMPDTW: {
				dataInput = new VRPMPDTWDataInput(oplFactory, scenario);
				break;
			}
			default: {
				return;
			}
		}
		
		if (dataInput != null) {
			oplModel.addDataSource(dataInput);
		}
			
	}
	
	
	
	
}
