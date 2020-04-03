package utfpr.tcc.vrp.solver.data;

import ilog.opl.IloOplModel;

import java.io.IOException;

public interface DataInput {
	void importOpl(IloOplModel oplModel) throws IOException;
}
