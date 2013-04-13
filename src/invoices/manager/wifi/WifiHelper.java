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
	
	/**
	 * Returns IP address of android device
	 * @param activity
	 * @return
	 */
	public String getDeviceIpAddress(Context activity) {
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
	
	/**
	 * 
	 * @param deviceAddress
	 * @param subnetAddress
	 * @return
	 */
	public String getIpAddress(String deviceAddress, String subnetAddress) {
		return getSubnetAddress(deviceAddress) + subnetAddress;
	}
	
	/**
	 * IP Address of local subnet
	 * @param ipAddress
	 * @return
	 */
	public String getSubnetAddress(String ipAddress) {
		try {
			return ipAddress.substring(0, ipAddress.lastIndexOf(".") + 1);
		} catch (IndexOutOfBoundsException ioobe) {
			return "";
		}
	}
	
	/**
	 * IP address associated of subnet - local address node of device
	 * @param ipAddress
	 * @return
	 */
	public String getSubnetIPAddressNode(String ipAddress) {
		try {
			return ipAddress.substring(ipAddress.lastIndexOf(".") + 1);
		} catch (IndexOutOfBoundsException ioobe) {
			return "";
		}
	}
}
