package utfpr.tcc.vrp.solver.config;

import java.io.File;

public class CplexConfiguration {
	
	private File basePath = new File(System.getProperty("user.dir"));
	
	public CplexConfiguration(File path) {

		this.basePath = path;
	}
	
	public CplexConfiguration(CplexConfiguration configuration) {
		this.basePath = configuration.basePath;
	}
	
	public File getBasePath() {
		return basePath;
	}
	
	public void setBasePath(File path) {
		this.basePath = path;
	}


}



