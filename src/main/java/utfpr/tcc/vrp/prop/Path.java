package utfpr.tcc.vrp.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Path {
	
	private final static Logger logger = Logger.getLogger(Path.class.getName());
	
	private static Path path;
	private File file = new File( "path.properties" );  
	private String scenarioPath;
	private String scenarioValidatorPath;
	private String iconsPath;
	private String modelsPath;
	private String basePath;
	
	public static Path getInstance(){
		if (path == null){
			path = new Path();
			return path;
		}		
		return path;		
	}
	
	private Path() {
		Properties properties = new Properties();          
        try {  
            properties.load( new FileInputStream( file ) );  
        } catch (FileNotFoundException ex) {  
        	logger.severe("Não foi possível encontrar o arquivo path.properties.");
            ex.printStackTrace();  
        } catch (IOException ex) {  
        	logger.severe("Erro de E/S ao ler o arquivo path.properties.\n" + ex.getMessage());
            ex.printStackTrace();  
        }       
        
        scenarioPath = properties.getProperty( "scenarioPath");      
        scenarioValidatorPath = properties.getProperty("scenarioValidatorPath");
        iconsPath = properties.getProperty("iconsPath");    
        modelsPath = properties.getProperty("modelsPath");
        basePath = properties.getProperty("basePath");
	}

	public String getScenarioPath() {		
		return scenarioPath;
	}	
	
	public String getScenarioValidatorPath() {
		return scenarioValidatorPath;
	}
	
	public String getIconsPath() {
		return iconsPath;
	}
	
	public String getModelsPath() {
		return modelsPath;
	}
	
	public String getBasePath() {
		return basePath;
	}
}
