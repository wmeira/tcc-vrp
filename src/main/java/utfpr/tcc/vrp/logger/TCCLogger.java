package utfpr.tcc.vrp.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TCCLogger {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;
	
	static public void setup() throws IOException {
		 
	    // Get the global logger to configure it		
		Logger logger = Logger.getLogger("");
		
	    fileTxt = new FileHandler(".//log//LOG.txt");	
	    
	    // Create txt Formatter
	    formatterTxt = new SimpleFormatter();
	    fileTxt.setFormatter(formatterTxt);
	    logger.addHandler(fileTxt);
	  }
}
