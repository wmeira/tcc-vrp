package utfpr.tcc.vrp.solver.data;

import ilog.concert.IloException;
import ilog.opl.IloOplModel;

import java.io.IOException;

public interface DataOutput {
	void exportOpl(IloOplModel oplModel) throws IOException, IloException;
}
