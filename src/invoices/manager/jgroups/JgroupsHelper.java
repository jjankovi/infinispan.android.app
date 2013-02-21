package invoices.manager.jgroups;

import invoices.manager.logger.LoggerFactory;

import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author jjankovi
 *
 */
public class JgroupsHelper {

	private static final Logger log = LoggerFactory.getLogger(JgroupsHelper.class);
	
	public static final String PORT = "7800";
	
	private static JgroupsHelper instance = null;
	
	private JgroupsHelper() {
		
	}
	
	public static JgroupsHelper getInstance() {
		if (instance == null) {
			instance = new JgroupsHelper();
		}
		return instance;
	}
	
	public void configureJgroups(Context context, Collection<String> devices) {
		
		String initialDevices = "";
		for (String device : devices) {
			initialDevices = initialDevices.concat(device +  ",");
		}
		initialDevices = initialDevices.substring(0, initialDevices.length() - 1);
		
		Properties props = System.getProperties();
		props.setProperty("jgroups.tcpping.initial_hosts", initialDevices);
		
		log.info("Devices: " + initialDevices);
		
		Toast.makeText(context, "Join to devices was performed", 
    			Toast.LENGTH_LONG).show();
	
	}
	
}