package utfpr.tcc.vrp.solver.data;

import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplFactory;
import utfpr.tcc.vrp.model.Scenario;

public class TestDataInput extends IloCustomOplDataSource {
	
	private Scenario scenario;
	
	public TestDataInput(IloOplFactory oplFactory, Scenario scenario) {

        super(oplFactory);
        this.scenario = scenario;
   }

	@Override
	public void customRead() {
		
		IloOplDataHandler handler = this.getDataHandler();
		
		// TEMPO_LIMITE = ...;
		handler.startElement("t");
        handler.addIntItem(5);
        handler.endElement();
        
	}
	
	
}
