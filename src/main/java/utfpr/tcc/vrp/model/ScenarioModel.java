package utfpr.tcc.vrp.model;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;

import utfpr.tcc.vrp.prop.Path;

public class ScenarioModel {
	
	private final static Logger logger = Logger.getLogger(ScenarioModel.class.getName());
	
	private Map<String, Scenario> scenarios;
	private String scenarioPath;
	private String scenarioValidatorPath;
	
	public ScenarioModel() {	
		Path path = Path.getInstance();
		scenarioPath = path.getScenarioPath();
		scenarioValidatorPath = path.getScenarioValidatorPath();
		
		this.scenarios = generateScenariosMap();		
 	}
	
	private Map<String, Scenario> generateScenariosMap() {
		Map<String, Scenario> scenariosMap = new HashMap<String, Scenario>();
		File[] files = getScenarioXmlFiles(scenarioPath);		
		File fileScenarioValidator = new File(scenarioValidatorPath);
		
		Scenario scenario; 
		for(File file : files) {			
			try {											
				scenario = getScenario(file, fileScenarioValidator);
				scenariosMap.put(scenario.getFile(), scenario);

			} catch(JDOMParseException e ) {
				logger.warning("Cenário " + file.getName() + " Inválido ou Incompleto.");
			}
			catch(JDOMException e) {	
				logger.warning("Problema ao validar o cenário " + file.getName() + ".");				
			} catch(IOException e) {
				logger.severe("Problema na leitura do arquivo " + file.getName() + ".");
			} 
		}
		
		//System.out.println("Número de cenários disponíveis: " + scenariosMap.size());
		return scenariosMap;
	}
	
	public void regenerateScenariosMap() {
		this.scenarios = generateScenariosMap();	
	}
	
	private File[] getScenarioXmlFiles(String scenarioFolderPath) {
		File folder = new File(scenarioFolderPath);
		
		FilenameFilter filter = new FilenameFilter() {			
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		};
		
		return folder.listFiles(filter);
	}
	
	private Scenario getScenario(File file, File xsdFile) throws JDOMException, IOException {
		XMLReaderJDOMFactory xsdScenarioValidator = new XMLReaderXSDFactory(xsdFile);		
		SAXBuilder sax = new SAXBuilder(xsdScenarioValidator);
		Document document = sax.build(file);
		Element eScenario = document.getRootElement();
		
		return new Scenario(eScenario, file.toString());		
	}

	public Map<String, Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(Map<String, Scenario> scenarios) {
		this.scenarios = scenarios;
	}		
	
	public Scenario getScenario(String name) {
		return scenarios.get(name);
	}	
	
	public boolean insert(Scenario scenario) {
		return scenario.save(true);		
	}	
	
	public boolean delete(String sfile) {		
		File file = new File(sfile);
		boolean isDeleted = file.delete();
		if(isDeleted) {
			scenarios.remove(sfile);			
		}		
		return isDeleted;
	}
	
}
