package utfpr.tcc.vrp.solver.data;

import ilog.concert.IloException;
import ilog.opl.IloCplex;
//import ilog.cplex.IloCplex;  CPLEX <= 12.5
import ilog.opl.IloOplModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class StatusOutput implements DataOutput {
	
	private final static Logger logger = Logger.getLogger(StatusOutput.class.getName());	
	
	Collection<String> Saida = new ArrayList<String>();

	public StatusOutput(Collection<String> saida) {
		this.Saida = saida;
	}

	public void exportOpl(IloOplModel oplModel) throws IOException {
		try {
			IloCplex cplex = oplModel.getCplex();
			Saida.add(cplex.getStatus().toString());
			Saida.add(String.valueOf((cplex.getObjValue())));
		}
		catch (IloException e) {
			logger.severe("Erro ao exportar em StatusOutput. \n" + e.getMessage());
		}
	}
}