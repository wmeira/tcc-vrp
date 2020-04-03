package utfpr.tcc.vrp.main;

import java.io.IOException;

import utfpr.tcc.vrp.controller.ScenarioController;
import utfpr.tcc.vrp.logger.TCCLogger;
import utfpr.tcc.vrp.model.ScenarioModel;
import utfpr.tcc.vrp.view.ScenarioView;

/**
 * Universidade Tecnológica Federal do Paraná - UTFPR Engenharia de Computação
 * 
 * Trabalho de Conclusão de Curso - TCC
 * 
 * Project: Problema de Roteamento de Veículos com Entregas e Coletas Mistas e
 * Janelas de Tempo: Aplicação em uma Empresa da Região Metropolitana de
 * Curitiba.
 * 
 * Author: William Hitoshi Tsunoda Meira
 * 
 * Date: 06/20/2013
 */

public class Main {
		
	public static void main(String[] args) {
		
		try {
			TCCLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
		    throw new RuntimeException("Problema ao criar os arquivos de log.");
		}
		
		ScenarioModel model = new ScenarioModel();
		ScenarioView view = new ScenarioView(model);
		ScenarioController controller = new ScenarioController(view, model);
		
		view.setVisible(true);
	}

}
