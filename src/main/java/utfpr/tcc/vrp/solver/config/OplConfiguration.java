package utfpr.tcc.vrp.solver.config;

import java.io.File;

public class OplConfiguration {
	
	private File basePath = new File(System.getProperty("user.dir"));
	
	private boolean debugMode = true;

	public OplConfiguration setModoDebug(boolean debugMode) {
		this.debugMode = debugMode;
		return this;
	}

	// SALVAR DAT
	private File externalDataPathOpl = null;

	public File getExternalDataPath() {
		return this.externalDataPathOpl;
	}

	public OplConfiguration setExternalDataPath(File path) {
		this.externalDataPathOpl = path;
		return this;
	}

	public boolean hasExternalDataPath() {
		return this.externalDataPathOpl != null;
	}

	public OplConfiguration(File path) {
		this.basePath = path;
	}

	public OplConfiguration(OplConfiguration configuration) {
		this.debugMode = configuration.debugMode;
		this.basePath = configuration.basePath;
		this.externalDataPathOpl = configuration.externalDataPathOpl;
	}
}
