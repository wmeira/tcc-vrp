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
	private File pathFile = new File( "path.properties" );
	private File keyFile = new File( "key.properties" );
	private String scenarioPath;
	private String scenarioValidatorPath;
	private String iconsPath;
	private String modelsPath;
	private String basePath;
	private String bingKey;
	
	public static Path getInstance(){
		if (path == null){
			path = new Path();
			return path;
		}		
		return path;		
	}
	
	private Path() {
		Properties pathProperties = loadProperties(pathFile);
		Properties keyProperties = loadProperties(keyFile);

        scenarioPath = pathProperties.getProperty( "scenarioPath");      
        scenarioValidatorPath = pathProperties.getProperty("scenarioValidatorPath");
        iconsPath = pathProperties.getProperty("iconsPath");    
        modelsPath = pathProperties.getProperty("modelsPath");
        basePath = pathProperties.getProperty("basePath");
        bingKey = keyProperties.getProperty("bingKey");
	}
	
	private Properties loadProperties(File file) {
		Properties properties = new Properties();
		try {  
			properties.load( new FileInputStream( file ) ); 
        } catch (FileNotFoundException ex) {  
        	logger.severe("Não foi possível encontrar o arquivo " + file.getName());
            ex.printStackTrace();  
        } catch (IOException ex) {  
        	logger.severe("Erro de E/S ao ler o arquivo " + file.getName() + ".\n" + ex.getMessage());
            ex.printStackTrace();  
        }
		return properties;
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
	
	public String getBingKey() {
		return bingKey;
	}
}
