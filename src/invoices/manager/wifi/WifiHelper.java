package invoices.manager.wifi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * WifiHelper provide helper methods to wifi states
 * 
 * @author jjankovi
 *
 */
public class WifiHelper {

	private static WifiHelper instance = null;
	
	private WifiManager wifiManager;
	
	private WifiHelper() {
		
	}
	
	public static WifiHelper getWifiHelper() {
		if (instance == null) {
			instance = new WifiHelper(); 
		}
		return instance;
	}
	
	/**
	 * Checks if device is connected to wireless network. At first, it checks if wifi interface is enabled. then
	 * the state of connected network (if any)
	 * 
	 * @param activity
	 * @return
	 */
	public boolean isConnectedToWifiNetwork(Activity activity) {
		wifiManager = (WifiManager) activity.
				getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			return false;
		}
		if (wifiManager.getConnectionInfo().getNetworkId() == -1) {
			return false;
		}
		return true;
	}
	
	public String getDeviceIpAddress(Activity activity) {
		wifiManager = (WifiManager) activity.
				getSystemService(Context.WIFI_SERVICE);
		
		int ipAdress = wifiManager.getConnectionInfo().getIpAddress();
		
		int intMyIp3 = ipAdress/0x1000000;
	    int intMyIp3mod = ipAdress%0x1000000;
	      
	    int intMyIp2 = intMyIp3mod/0x10000;
	    int intMyIp2mod = intMyIp3mod%0x10000;
	      
	    int intMyIp1 = intMyIp2mod/0x100;
	    int intMyIp0 = intMyIp2mod%0x100;
		
		return String.valueOf(intMyIp0)
		         + "." + String.valueOf(intMyIp1)
		         + "." + String.valueOf(intMyIp2)
		         + "." + String.valueOf(intMyIp3);
	}
	
}
