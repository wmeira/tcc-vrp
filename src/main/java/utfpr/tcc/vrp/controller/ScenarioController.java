package utfpr.tcc.vrp.controller;

import ilog.concert.IloException;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.http.HttpException;

import utfpr.tcc.vrp.exception.ExportDataOutputException;
import utfpr.tcc.vrp.exception.IODatException;
import utfpr.tcc.vrp.exception.ImportDataInputException;
import utfpr.tcc.vrp.exception.InfeasibleSolutionException;
import utfpr.tcc.vrp.exception.SolverException;
import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.model.ScenarioModel;
import utfpr.tcc.vrp.model.Solution;
import utfpr.tcc.vrp.prop.Path;
import utfpr.tcc.vrp.solver.config.CplexConfiguration;
import utfpr.tcc.vrp.solver.config.OplConfiguration;
import utfpr.tcc.vrp.solver.config.SolverExecutor;
import utfpr.tcc.vrp.solver.data.DataInput;
import utfpr.tcc.vrp.solver.data.DataOutput;
import utfpr.tcc.vrp.solver.data.StatusOutput;
import utfpr.tcc.vrp.solver.data.VRPDataInput;
import utfpr.tcc.vrp.solver.data.VRPDataOutput;
import utfpr.tcc.vrp.solver.model.MathematicalModel;
import utfpr.tcc.vrp.solver.model.ModelManager;
import utfpr.tcc.vrp.solver.model.VRPModelManager;
import utfpr.tcc.vrp.view.CreateEditScenarioView;
import utfpr.tcc.vrp.view.ScenarioView;
import utfpr.tcc.vrp.view.SolutionView;



public class ScenarioController extends Controller {
	
	private final static Logger logger = Logger.getLogger(ScenarioController.class.getName());	
	
	private ScenarioView view;
	private ScenarioModel model;
	
	private final ClockListener clockListener= new ClockListener();
	private Timer timer;
	private SolverWorker solverWorker;
		
	public ScenarioController(ScenarioView view, ScenarioModel model) {
		this.model = model;
		this.view = view;	
		this.timer = new Timer(1000, clockListener);
		
		//Add Listeners to the view.
		view.getBtnCreate().addActionListener(new BtnCreateListener());
		view.getBtnEdit().addActionListener(new BtnEditListener());
		view.getBtnDelete().addActionListener(new BtnDeleteListener());		
		view.getListScenarios().addListSelectionListener(new ListScenariosChangeListener());
		view.getBtnSolve().addActionListener(new BtnSolveListener());
		
		view.getListScenarios().setListData(model.getScenarios().values().toArray());	
		if(view.getListScenarios().getModel().getSize() >= 0) {
			view.getListScenarios().setSelectedIndex(0);
		}
		
		updateTableDetails();
	}
	
	private void updateListScenarios() {	
		model.regenerateScenariosMap();
		view.getListScenarios().setListData(model.getScenarios().values().toArray());		
	}
	
	private void cleanTableDetails() {
		DefaultTableModel dm = (DefaultTableModel) view.getTblDetails().getModel();
		for(int row = dm.getRowCount() - 1; row > -1 ; row--) {
			dm.removeRow(row);
		}
	}
	
	private void updateAll() {
		updateListScenarios();				
		if(view.getListScenarios().getModel().getSize() >= 0) {
			view.getListScenarios().setSelectedIndex(0);
		}
		updateTableDetails();
	}
	
	private void updateTableDetails() {
		Scenario scenarioSelected = (Scenario) view.getListScenarios().getSelectedValue();			
		cleanTableDetails();
		if(scenarioSelected == null ) {
			return;
		}			
		
		DefaultTableModel dm = (DefaultTableModel) view.getTblDetails().getModel();
		dm.setDataVector(scenarioSelected.getTableDetails(), view.COLUMN_IDENTIFIERS_SCENARIO_DETAILS);
	}
	
	private void createScenario() {
		CreateEditScenarioView createScenarioView = new CreateEditScenarioView(view);		
		CreateEditScenarioController createScenarioController = new CreateEditScenarioController(createScenarioView, null);
		
		createScenarioView.setAllVisible();	

		updateAll();
		
		String file = createScenarioController.getFileNewScenario();
		if(file != null) {
			view.getListScenarios().setSelectedValue(model.getScenario(file), true);
		}
	}
	
	private void editScenario() {
		Scenario scenario = (Scenario) view.getListScenarios().getSelectedValue();
		
		if(scenario == null) {
			int create = JOptionPane.showConfirmDialog(view, "Nenhum cenário selecionado, deseja criar um novo cenário?", 
					"Cenário", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			
			if(create==JOptionPane.YES_OPTION) {
				createScenario();
			}
			return;
		}
		
		CreateEditScenarioView editScenarioView = new CreateEditScenarioView(view);
		CreateEditScenarioController editScenarioController = new CreateEditScenarioController(editScenarioView, scenario);
		
		editScenarioView.setAllVisible();			
		
		updateAll();
		
		String file = editScenarioController.getFileNewScenario();
		if(file != null) {
			view.getListScenarios().setSelectedValue(model.getScenarios().get(file), true);
		}
	}
	
	private void setViewEnable(boolean enabled) {
		view.getBtnCreate().setEnabled(enabled);
		view.getBtnDelete().setEnabled(enabled);
		view.getBtnEdit().setEnabled(enabled);
		view.getListScenarios().setEnabled(enabled);
		view.getCbxModel().setEnabled(enabled);		
		view.getBtnSolve().setEnabled(enabled);
	}
	
	private void setProgressBarStatus(boolean enabled) {
		setViewEnable(!enabled);
		view.getProgressBar().setVisible(enabled);
		view.getProgressBar().setIndeterminate(enabled);		
	}
	
	private boolean executeMathematicalModel(MathematicalModel mathematicalModel, Scenario scenario, List<Object> results) throws InfeasibleSolutionException { 		

		boolean success = false;
		
		try {
			String basePath = Path.getInstance().getBasePath();
			File baseDir = new File(basePath);
			OplConfiguration oplConfiguration = new OplConfiguration(baseDir);
			CplexConfiguration cplexConfiguration = new CplexConfiguration(baseDir);
			oplConfiguration.setModoDebug(false);
			
			String modelsPath = Path.getInstance().getModelsPath();			
			oplConfiguration.setExternalDataPath(new File(modelsPath + mathematicalModel + ".dat"));
			
			ModelManager modelFile = VRPModelManager.getModel(mathematicalModel);

			Collection<DataInput> dataInputs = new ArrayList<DataInput>();
			dataInputs.add(new VRPDataInput(mathematicalModel, scenario));
			Collection<DataOutput> dataOutputs = new ArrayList<DataOutput>();
			dataOutputs.add(new VRPDataOutput(mathematicalModel, scenario, results));
			Collection<String> saida = new ArrayList<String>();
			dataOutputs.add(new StatusOutput(saida));
			SolverExecutor opl = new SolverExecutor(oplConfiguration, cplexConfiguration,
					modelFile, dataInputs, dataOutputs);
			opl.execute(cplexConfiguration);
			
			success = true;
			
		} catch (ImportDataInputException e) {
			JOptionPane.showMessageDialog(null,
											e.getMessage(),
											"Erro ao Importar Dados de Entrada", JOptionPane.ERROR_MESSAGE);
		} catch (IODatException e) {
			JOptionPane.showMessageDialog(null,
					e.getMessage(),
					"Erro de E/S para salvar modelo (.dat)", JOptionPane.ERROR_MESSAGE);
		} catch (SolverException e) {
			JOptionPane.showMessageDialog(null,
					"Verifique se o IBM ILOG CPLEX Studio v12.5 está corretamente instalado.\n\n" + e.getMessage(),
					"Erro ao executar o solucionador", JOptionPane.ERROR_MESSAGE);
		} catch (InfeasibleSolutionException e) {
			throw e;
			
		} catch (ExportDataOutputException e) {
			JOptionPane.showMessageDialog(null,
					e.getMessage(),
					"Erro ao Exportar Dados de Saída", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					e.getMessage(),
					"Erro ao E/S de dados", JOptionPane.ERROR_MESSAGE);
		} catch (IloException e) {
			JOptionPane.showMessageDialog(null,
					"Dat inválido ou incompleto.\n" + e.getMessage(),
					"Erro ao executar o modelo.", JOptionPane.ERROR_MESSAGE);
		}
		
		return success;
	}
	

	class BtnCreateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			createScenario();
		}
	}
	
	class BtnEditListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			editScenario();
		}
	}
	
	class BtnDeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			 
			Scenario scenarioSelected = (Scenario) view.getListScenarios().getSelectedValue();
			
			if(scenarioSelected == null) {
				return;
			}
			
			int answer = JOptionPane.showConfirmDialog(view,
					"Deseja realmente excluir o cenário \"" + scenarioSelected.getName() + "\" (" + scenarioSelected.getDateFormated() + ") ?", "Confirmação", JOptionPane.YES_NO_OPTION);

			if(answer == JOptionPane.YES_OPTION) {
				if(model.delete(scenarioSelected.getFile())) {
					JOptionPane.showMessageDialog(view, "Cenário deletado com sucesso!", "Cenário", JOptionPane.INFORMATION_MESSAGE);					
				} else {
					JOptionPane.showMessageDialog(view, "Erro ao deletar o cenário.", "Cenário", JOptionPane.ERROR_MESSAGE);	
				}
				
				updateAll();
			}		
		}
	}
	
	class ListScenariosChangeListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			updateTableDetails();					
		}	
	}
	
	class BtnSolveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
		
			Scenario scenario = (Scenario) view.getListScenarios().getSelectedValue();	

			if(scenario == null) {
				int create = JOptionPane.showConfirmDialog(view, "Nenhum cenário selecionado, deseja criar um novo cenário?", 
						"Cenário", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				
				if(create==JOptionPane.YES_OPTION) {
					createScenario();
				}
				return;
			}
			
			List<String> warnings = scenario.getScenarioWarnings();
			if(warnings.size() > 0) {
				String msg = "O cenário possui os seguintes problemas:\n"; 
				for(int i = 0; i < warnings.size(); i++) {
					msg = msg + "\n   - " + warnings.get(i);
				}
				msg = msg + "\n\n" + "Deseja alterar o cenário? Prosseguindo com o solucionador acarretará em uma solução com problemas.\n\n";
			
				int edit = JOptionPane.showConfirmDialog(view, msg, "Aviso de Cenário", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if(edit == JOptionPane.YES_OPTION) {
					editScenario();
					return;
				}
				
				if(edit == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			
			MathematicalModel mathematicalModel = (MathematicalModel) view.getCbxModel().getSelectedItem();
			
			int solve = JOptionPane.showConfirmDialog(view, "Encontrar uma solução pode demorar alguns minutos. Deseja continuar?" + "\n\nCenário Selecionado: " + scenario.getName() + "\nModelo Matemático: " + mathematicalModel 
					, "Solucionar Cenário", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			
			if(solve==JOptionPane.NO_OPTION) {
				return;
			}
			
        	setProgressBarStatus(true);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));			
			
			solverWorker = new SolverWorker(scenario, mathematicalModel);	
			solverWorker.execute();			
		}
	}
		
	class SolverWorker extends SwingWorker<Boolean, Void>
	{		
		private Scenario scenario;
		private MathematicalModel mathematicalModel;
		private boolean success = false;
		private String errorMessage = "";
		private String errorTitle = "";
		private SolverExecutor opl = null;
		private List<Object> results;
		
		private SolutionView solutionView;
		private SolutionController solutionController; 
	
		public SolverWorker(Scenario scenario, MathematicalModel mathematicalModel) {
			this.scenario = scenario;
			this.mathematicalModel = mathematicalModel;
			this.results = new ArrayList<Object>();
		}
		
	    protected Boolean doInBackground() throws Exception
	    {
	    	view.setTitle(ScenarioView.SOLVING_TITLE);
        	setProgressBarStatus(true);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));	
			//view.getBtnSolve().setText();
			clockListener.reset();
			timer.start();
			
			success = false;
			
			try {				
				String basePath = Path.getInstance().getBasePath();
				File baseDir = new File(basePath);
				OplConfiguration oplConfiguration = new OplConfiguration(baseDir);
				CplexConfiguration cplexConfiguration = new CplexConfiguration(baseDir);
				oplConfiguration.setModoDebug(false);
				
				String modelsPath = Path.getInstance().getModelsPath();
				
				oplConfiguration.setExternalDataPath(new File(modelsPath + mathematicalModel + ".dat"));
				
				ModelManager modelFile = VRPModelManager.getModel(mathematicalModel);
				
				Collection<DataInput> dataInputs = new ArrayList<DataInput>();
				dataInputs.add(new VRPDataInput(mathematicalModel, scenario));
				Collection<DataOutput> dataOutputs = new ArrayList<DataOutput>();
				dataOutputs.add(new VRPDataOutput(mathematicalModel, scenario, results));
				Collection<String> saida = new ArrayList<String>();
				dataOutputs.add(new StatusOutput(saida));
				opl = new SolverExecutor(oplConfiguration, cplexConfiguration,
						modelFile, dataInputs, dataOutputs);
				opl.execute(cplexConfiguration);
				
				success = true;
				
			} catch (ImportDataInputException e) {
				errorMessage = e.getMessage();
				errorTitle = "Erro ao Importar Dados de Entrada";
			} catch (IODatException e) {
				errorMessage = e.getMessage();
				errorTitle = "Erro de E/S para salvar modelo (.dat)";
			} catch (SolverException e) {
				errorMessage = "Verifique se o IBM ILOG CPLEX Studio v12.5 ou maior está corretamente instalado.";
				errorTitle = "Erro ao executar o solucionador";				
			} catch (InfeasibleSolutionException e) {
				errorMessage = 	"Para os veículos disponíveis NÃO foi possível encontrar uma SOLUÇÃO \n" + 
								"(rota) que atenda todos os serviços cadastrados e que satisfaça os \n" +
								" intervalos de disponibilidade definidos.\n\n" +
								"\t\tDeseja alterar o cenário?\n\n" +
								"Observação: verifique as capacidades dos veículos, variáveis de coletas\n" +
								" e entregas, intervalos de disponibilidade. A quantidade de veículos \n" +
								"pode ser insuficiente para atender todos os serviços durante o dia.\n\n";
						
				errorTitle = "Solução Infactível";				
			} catch (ExportDataOutputException e) {
				errorMessage = e.getMessage();
				errorTitle = "Erro ao Exportar Dados de Saída";
			} catch (IOException e) {
				errorMessage = e.getMessage();
				errorTitle = "Erro ao E/S de dados";
			} catch (IloException e) {
				errorMessage = "Dat inválido ou incompleto.\n" + e.getMessage();
				errorTitle = "Erro ao executar o modelo.";
				e.printStackTrace();
			}		
			
	        if(!success) {
				if(getErrorTitle().equals("Solução Infactível")) {
					int edit = JOptionPane.showConfirmDialog(view,  
							getErrorMessage(), getErrorTitle(), 
							JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
					
					if(edit==JOptionPane.YES_OPTION) {
						editScenario();
						updateAll();
					}						
				} else {
					JOptionPane.showMessageDialog(view,
							getErrorMessage(),
							getErrorTitle(), JOptionPane.ERROR_MESSAGE);						
				}					
				return false;
			} 		
			
	        view.setTitle(ScenarioView.MAPPING_TITLE);
	        
            //Solution obtained with success.	
	        Solution solution = (Solution) results.get(0);
			solutionView = new SolutionView(view);		
			
			boolean tryAgain = true;
			
			while(tryAgain) {
				tryAgain = false;
				try {
					solutionController = new SolutionController(solutionView, solution);	
					return true;
				} catch (IOException e) {
					int answer = JOptionPane.showConfirmDialog(view, "Não foi possível conectar ao serviço Bing para gerar mapas de rotas." +
							"\nVerifique a conexão com a internet.\n Tentar novamente? ", "Erro no serviço RESTful Bing Maps", 
							JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);	
					if(answer == JOptionPane.NO_OPTION) {
						solutionView.dispose();		
					} else {
						tryAgain = true;
					}											
				} catch (URISyntaxException e) {
					JOptionPane.showMessageDialog(view,
							"Sintaxe incorreta da URL do serviço para gerar os mapas da rota.", 
							"Erro no serviço RESTful Bing Maps", JOptionPane.ERROR_MESSAGE);
					solutionView.dispose();				
				} catch (HttpException e) {
					JOptionPane.showMessageDialog(view,
							"Problema ao ler respostas ou enviar mensagens ao serviço.", 
							"Erro no serviço RESTful Bing Maps", JOptionPane.ERROR_MESSAGE);		
				}
			}
			
			
			if(solutionController == null) {
				solutionView.dispose();
				return false;
			}
			
			return true;		
		}
	    
	    protected void done()
	    {	    	
			view.setTitle(ScenarioView.DEFAULT_TITLE);
			setProgressBarStatus(false);
	        view.setCursor(null);
	        timer.stop();
	        view.getBtnSolve().setText("Solucionar");
	        
	        try {
				if(get()) {
					solutionView.setVisible(true);
				}
			} catch (InterruptedException e) {
				logger.warning("Execução Interrompida. " + e.getMessage() );
				JOptionPane.showMessageDialog(view,
						"Execução Interrompida. " + e.getMessage(), 
						"Erro", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (ExecutionException e) {
				logger.warning("Exceção de Execução. " + e.getMessage() );
				JOptionPane.showMessageDialog(view,
						"Exceção de Execução. " + e.getMessage(), 
						"Erro", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			
	    }
	    
		public boolean getSuccess() {
			return success;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
		
		public String getErrorTitle() {
			return errorTitle;
		}
	}
	
	private class ClockListener implements ActionListener {

	    private int hours;
	    private int minutes;
	    private int seconds;
	    private String hour;
	    private String minute;
	    private String second;
	    
	    public ClockListener() {
	    	hours = 0;
	    	minutes = 0;
	    	seconds = 0;
	    }
	    
	    public void reset() {
	    	hours = 0;
	    	minutes = 0;
	    	seconds = 0;
	    }

	    public void actionPerformed(ActionEvent e) {
	        NumberFormat formatter = new DecimalFormat("00");
	        if (seconds == 60) {
	            seconds = 00;
	            minutes++;
	        }

	        if (minutes == 60) {
	            minutes = 00;
	            hours++;
	        }
	        
	        hour = formatter.format(hours);
	        minute = formatter.format(minutes);
	        second = formatter.format(seconds);
	        view.getBtnSolve().setText(String.valueOf(hour + ":" + minute + ":" + second));
	        seconds++;
	    }
	}
}
