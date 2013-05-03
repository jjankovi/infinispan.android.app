package invoices.manager.network;

import invoices.manager.logger.LoggerFactory;

import java.net.ServerSocket;

import org.apache.log4j.Logger;

/**
 * PortHelper provides tooling to network port manipulation 
 * 
 * @author jjankovi
 *
 */
public class PortHelper {

	private Logger log = LoggerFactory.getLogger(PortHelper.class);
	
	private static PortHelper instance = null;
	
	private static boolean portOpened = false;
	
	private PortHelper() {
		
	}
	
	/**
	 * Creates an instance of PortHelper
	 *
	 * @return		instance of PortHelper
	 */
	public static PortHelper getInstance() {
		if (instance == null) {
			instance = new PortHelper();
		}
		return instance;
	}
	
	/**
	 * Opens port with given number and let it opened
	 * for listening to requests
	 *
	 * @param 		port which should be opened
	 */
	public void listeningOnPort(int port) {
		if (portOpened) {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					ServerSocket server = new ServerSocket(7800);
					while (true) {
						openPort();
						server.accept();
					}
				} catch (Exception exc) {
					
				}
			}	
		}).start();
	}
	
	public void openPort() {
		if (!portOpened) {
			portOpened = true;
			log.info("Port 7800 is opened and ready for requests");
		}
	}
	
}
