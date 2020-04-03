package utfpr.tcc.vrp.solver.config;


import ilog.concert.IloException;
import ilog.opl.IloCplex;
import ilog.opl.IloCplex.Status;
//import ilog.cplex.IloCplex; CPLEX <= 12.5
//import ilog.cplex.IloCplex.Status; CPLEX <= 12.5
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import utfpr.tcc.vrp.exception.ExportDataOutputException;
import utfpr.tcc.vrp.exception.IODatException;
import utfpr.tcc.vrp.exception.ImportDataInputException;
import utfpr.tcc.vrp.exception.InfeasibleSolutionException;
import utfpr.tcc.vrp.exception.SolverException;
import utfpr.tcc.vrp.solver.data.DataInput;
import utfpr.tcc.vrp.solver.data.DataOutput;
import utfpr.tcc.vrp.solver.model.ModelManager;

public class SolverExecutor {
	
	private final static Logger logger = Logger.getLogger(SolverExecutor.class.getName());	
	
	/** OPL configuration object. */
	private final OplConfiguration oplConfiguration;
	
	/** Cplex configuration object. */
	private final CplexConfiguration cplexConfiguration;
	
	/** OPL object's declaration. */
	private IloOplFactory oplFactory;
	private IloOplModel oplModel;
	
	/** Model access.*/
	private final ModelManager modelManager;
	private final Collection<DataInput> dataInputs;
	private final Collection<DataOutput> dataOutputs;
	
	public SolverExecutor(OplConfiguration oplConfiguration, CplexConfiguration cplexConfiguration, ModelManager modelManager, Collection<DataInput> dataInputs, Collection<DataOutput> dataOutputs) {
		
		super();
		this.oplConfiguration = new OplConfiguration(oplConfiguration);
		this.cplexConfiguration = new CplexConfiguration(cplexConfiguration);
		this.modelManager = modelManager;
		this.dataInputs = Collections.unmodifiableCollection(dataInputs);
		this.dataOutputs = Collections.unmodifiableCollection(dataOutputs);
	}
	
	public void execute(CplexConfiguration configuracaoCplex) throws ImportDataInputException, IODatException, SolverException, InfeasibleSolutionException, ExportDataOutputException, IOException, IloException {
		this.oplFactory.setDebugMode(false);
		this.oplFactory.setDebugModeWarning(false);
		this.oplFactory = new IloOplFactory();
					
		IloOplErrorHandler errHandler = this.oplFactory.createOplErrorHandler();
		IloOplSettings settings = this.oplFactory.createOplSettings(errHandler);
		settings.setSkipWarnNeverUsedElements(true);
		
		settings.setDisplayPrecision(10);
		
		String textModel = modelManager.getModelContent();
		IloOplModelSource oplModelSource = this.oplFactory.createOplModelSourceFromString(textModel, modelManager.getModelName());
		IloOplModelDefinition oplModelDefinition = this.oplFactory.createOplModelDefinition(oplModelSource,settings);
		
		IloCplex cplex = this.oplFactory.createCplex();
			this.oplModel = this.oplFactory.createOplModel(oplModelDefinition, cplex);		

	
		/****************************** 
		 **********DATAINPUT***********
		 *****************************/
		try{
			for (DataInput dataInput : this.dataInputs) {
				dataInput.importOpl(this.oplModel);
			}
		} catch (Exception exp) {
			logger.severe("Erro ao importar dados para o dataInput: " + exp.getMessage());
			throw new ImportDataInputException(exp.getMessage());
		}
		
		//SAVE DAT file
		if (this.oplConfiguration.hasExternalDataPath()) {
			try {
				saveDat(this.oplConfiguration.getExternalDataPath());
			} catch(IOException e) {
				logger.severe("Problema de E/S para salvar .dat.\n" + e.getMessage());
				throw new IODatException();
			}
			
		}
		this.oplModel.generate();
		for (DataInput dataInput : this.dataInputs) {
			if (dataInput instanceof Closeable) ((Closeable)dataInput).close();
		}
								
		/****************************** 
		 ***********SOLVE**************
		 *****************************/
		
		//Set parameters to minimize the memory usage.
		//NodeFileInd = 2 -> saves on the hard drive when the memory surpass the limit.			
		cplex.setParam(IloCplex.IntParam.NodeFileInd, 2);
		//WorkMem - > compact the memory after the limit.
		cplex.setParam(IloCplex.DoubleParam.WorkMem, 2);
		//cplex.setParam(IloCplex.DoubleParam.TreLim, 86400000);
		
		try{
			if (cplex.solve()) {
				this.oplModel.postProcess();
			}
		}
		catch(Exception exp){
			logger.severe("Erro ao solucionar modelo: " + exp.getMessage());
			throw new SolverException(exp.getMessage());
		}
		
		//System.out.println(cplex.getStatus());
		//logger.debug(cplex.getStatus());
		
		if ( cplex.getStatus() == Status.Error){
			logger.severe("Erro na execução do CPLEX.");
			throw new SolverException("Erro na execução do CPLEX");
		}
		else if (cplex.getStatus() == Status.Infeasible || cplex.getStatus() == Status.InfeasibleOrUnbounded){
			logger.warning("Solução Infactível");
			throw new InfeasibleSolutionException("Solução Infactível");
		}
		
		/****************************** 
		 *********DATAOUTPUT***********
		 *****************************/
		try {
			for (DataOutput dataOutput : this.dataOutputs) {
				dataOutput.exportOpl(this.oplModel);
			}
		} catch (Exception exp) {
			logger.severe("Erro ao exportar dados para o dataOutput.");
			throw new ExportDataOutputException(exp.getMessage());
		}
		
		//End the executor and delete the objects
		cplex.end();
		
	}
	
	/**
	 * Save the .dat file 
	 * @param path = path where the .dat will be saved. 
	 */
	 protected void saveDat(File path) throws IOException {
		if (! path.getParentFile().exists()) {
			path.getParentFile().mkdirs();
		}
		OutputStream os = new FileOutputStream(path);
		this.oplModel.printExternalData(os);
		os.close();

	}
	
}
