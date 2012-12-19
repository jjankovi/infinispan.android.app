package invoices.manager.logger;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;


public class LoggerFactory {

	public static final LogConfigurator logConfigurator = new LogConfigurator();
	
	public static Logger getLogger(Class<?> clazz) {
		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "myapp_" +clazz.getCanonicalName() + ".log");
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.configure();
		return Logger.getLogger(clazz);
	}
	
}
