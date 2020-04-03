package utfpr.tcc.vrp.solver.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import utfpr.tcc.vrp.prop.Path;

public class PathModelManager implements ModelManager {
	
	private final static Logger logger = Logger.getLogger(PathModelManager.class.getName());
	
	private final String modelName;
	private String filePath;
	
	public PathModelManager(String modelName, String filePath) {
		super();
		this.modelName = modelName;
		this.filePath = filePath;
	}

	public String getModelName() {
		return modelName;
	}


	public String getModelContent() throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line, results = "";
		while( ( line = reader.readLine() ) != null)
		{
			results += line + "\n";
		}
		reader.close();
		
		return results;
	}
}
