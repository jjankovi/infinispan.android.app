package invoices.manager.network;

import java.net.ServerSocket;

/**
 * PortHelper provides tooling to network port manipulation 
 * 
 * @author jjankovi
 *
 */
public class PortHelper {

	private static PortHelper instance = null;
	
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
		new Thread(new Runnable() {
			public void run() {
				try {
					ServerSocket server = new ServerSocket(7800);
					while (true) {
						server.accept();
					}
				} catch (Exception exc) {
					
				}
			}
		}).start();
	}
	
}
