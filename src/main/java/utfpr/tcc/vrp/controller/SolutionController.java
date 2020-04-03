package utfpr.tcc.vrp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.http.HttpException;

import utfpr.tcc.vrp.model.Route;
import utfpr.tcc.vrp.model.Solution;
import utfpr.tcc.vrp.view.RoutePanel;
import utfpr.tcc.vrp.view.SolutionView;

public class SolutionController extends Controller {
	
	private SolutionView view; //view
	private Solution model = null; //model
	
	public SolutionController(SolutionView view, Solution model) throws URISyntaxException, HttpException, IOException {
		this.view = view;
		this.model = model;
		
		loadInfo();
		
		if(model.getSolutionProblems().size() > 0) {
			JOptionPane.showMessageDialog(view, 
					"Não foi possível encontrar uma solução ótima \n" +
					"que atenda todas as restrições do cenário.\n" +
					"A tela de solução apresenta a solução com \n"+ 
					"menor número de problemas, solução relaxada.  \n\n" +
					"Os pontos de relaxação estão indicados na \n" + "" +
					"caixa de texto problemas.", 
					"Solução Relaxada", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void loadInfo() throws URISyntaxException, HttpException, IOException {
		
		view.setTitle("Solução para o Cenário \""+ model.getScenario().getName() + "\" utilizando o modelo \"" + model.getMathematicalModel() + "\"");
		
		view.setTextTotalDistanceTraveled(model.getTotalDistanceTraveled());
		view.setTextTotalTimeTraveled(model.getTotalTimeTraveled());		
		view.setTextStatus(model.getSolutionStatus());
		view.setTextExecutionTime(model.getExecutionTime());
		for(String problem : model.getSolutionProblems()) {
			view.getTxtProblems().append(problem + "\n");
		}

		List<Route> routes = model.getRoutes();
		RoutePanel routePanel;
		String title;
		for(int i = 0; i < routes.size(); i++) {
			routePanel = new RoutePanel(model.getScenario(), routes.get(i));
			title = routes.get(i).getVehicle().getName();
			view.getTabRoutes().addTab(title, routePanel);
		}
	}
}
