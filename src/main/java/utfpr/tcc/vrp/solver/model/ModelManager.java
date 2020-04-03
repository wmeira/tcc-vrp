package utfpr.tcc.vrp.solver.model;

import java.io.IOException;

public interface ModelManager {
	
	/** Retorno o nome do Modelo. */
	String getModelName();
	
	/** Retorna o conteúdo do modelo. */
	String getModelContent() throws IOException;	
}