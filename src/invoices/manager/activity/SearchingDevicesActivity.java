package invoices.manager.activity;

import invoices.manager.jgroups.JgroupsHelper;
import invoices.manager.logger.LoggerFactory;
import invoices.manager.wifi.WifiHelper;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

/**
 * 
 * @author jjankovi
 *
 */
public class SearchingDevicesActivity extends Activity {

	private static final Logger log = LoggerFactory.getLogger(
			SearchingDevicesActivity.class);
	
	private CheckBox searchCheckbox;
	private ListView devices;
	
	private Button refreshButton;
	private Button joinButton;
	
	private static int counter = 0;
	private static int counterDevices = 0;
	
	private static List<String> searchDevices = 
		Collections.synchronizedList(new ArrayList<String>());
	
	private ProgressDialog progressDialog = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_devices);
        
        searchCheckbox = (CheckBox)findViewById(R.id.checkBox1);
        
        devices = (ListView)findViewById(R.id.listView1);
        devices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        devices.setOnItemClickListener(new CustomListClickListener());
        
        refreshButton = (Button)findViewById(R.id.refresh_searching);
        refreshButton.setEnabled(searchCheckbox.isChecked());
        
        joinButton = (Button)findViewById(R.id.join_item);
        updateJoinButtonState();
        
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_searching_devices, menu);
        return true;
    }
    
    /** method is invoked when checkbox is checked/unchecked **/
    public void searchingDevice(View view) {
    	if (searchCheckbox.isChecked()) {
    		startDeviceSearching();
    	} else {
    		updateDevices(new ArrayList<String>());
    		refreshButton.setEnabled(searchCheckbox.isChecked());
    		joinButton.setEnabled(searchCheckbox.isChecked());
    	}
    }
    
    /** method is invoked when Join button is pressed **/
    public void join(View view) {
    	
    	JgroupsHelper.getInstance().configureJgroups(this, getDevicesToJoin());
    	
    }
    
    /** method is invoked when Refresh button is pressed **/
    public void refreshSearching(View view) {
    	startDeviceSearching();
    }
    
    public void updateJoinButtonState() {
    	if (devices.getAdapter() != null) {
    		if (!devices.getAdapter().isEmpty()) {
        		for (int index = 0; index < devices.getAdapter().getCount(); index++) {
        			if (devices.isItemChecked(index)) {
        				joinButton.setEnabled(true);
        				return;
        			}
        		}
        	}
    	}
		joinButton.setEnabled(false);
	}
    
    private Collection<String> getDevicesToJoin() {
    	Collection<String> devicesToJoin = new ArrayList<String>();
    	if (devices.getAdapter() != null) {
    		if (!devices.getAdapter().isEmpty()) {
        		for (int index = 0; index < devices.getAdapter().getCount(); index++) {
        			if (devices.isItemChecked(index)) {
        				devicesToJoin.add(devices.getItemAtPosition(index).toString());
        			}
        		}
        	}
    	}
    	// add local address as well
    	devicesToJoin.add(WifiHelper.getWifiHelper().getDeviceIpAddress(this) + "[7800]");
    	return devicesToJoin;
    }
    
    /**
     * according to ip address of device, discovering of accessible devices is started
     */
    private void startDeviceSearching() {
    	String myIpAddress = WifiHelper.getWifiHelper().getDeviceIpAddress(this);
		this.progressDialog = ProgressDialog.show(
				this, "Devices discovering", "Devices on same network are discovering...", true, false);
		
		searchDevices.clear();
		runSearchingThreads(myIpAddress, 50);
    }
    
    private void runSearchingThreads(String myIpAddress, int number) {
    	int start = 0;
    	int end = 255/number + 1;
    	int count = end - start; 
    	for (int i = 0; i < number; i++) {
    		if (end + count > 255) {
    			end = 255;
    		}
    		new SearchThread(searchDevices, start, end, myIpAddress).start();
    		
    		start = end + 1;
    		end = start + count;
    	}
    	
    }
    
    /**
     * elements from parameter collection object are added into listview widget and invalidated 
     * 
     * @param searchDevices
     */
    public void updateDevices(List<String> searchDevices) {
		devices.setAdapter(new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_multiple_choice, 
				searchDevices));
		devices.setItemsCanFocus(false);
		devices.invalidateViews();
	}
    
    /**
     * thread safe method for increasing counter
     */
    public void increaseCounter() {
    	synchronized (this) {
			counter++;
		}
    }
    
    /**
     * thread safe method for decreasing counter
     */
    public void decreaseCounter() {
    	synchronized (this) {
			counter--;
		}
    } 
    
    class CustomListClickListener implements AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			updateJoinButtonState();
		}
    	
    }
    
    class SearchThread extends Thread {
		
		private int from;
    	private int to;
    	private String localIpAddress;
		
		public SearchThread(List<String> searchDevices, int from, int to, String localIpAddress) {
			
			this.from = from;
			this.to = to;
			this.localIpAddress = localIpAddress;
			increaseCounter();
		}
		
		@Override
		public void run() {
			
			for (int i = from; i <= to; i++) {
    			String device = localIpAddress.substring(
    					0, localIpAddress.lastIndexOf(".") + 1) + i;
    			if (device.equals(localIpAddress)) continue;
    			try {
    				if (device.equals("192.168.0.100")) {
    					System.out.println();
    				}
    				new Socket(device, 7800);
    				searchDevices.add(device);
    			} catch (Exception e) {
    			}
    			countDevices();
    		}
			runOnUiThread(new Runnable() {
				public void run() {
					decreaseCounter();
    	    		
    	    		if (counter == 0) { // this is last thread
    	    			dismissProgressDialog();
    	    			updateDevices(searchDevices);
    	    			refreshButton.setEnabled(true);
    	    			joinButton.setEnabled(false);
    	    			
    	    			log.info(counterDevices + " devices found");
    	    			log.info(searchDevices.size());
    	    			counterDevices = 0;
    	    		}
				}
				
				private void dismissProgressDialog() {
					if (progressDialog != null) {
	    				progressDialog.dismiss();
	    	        }
				}
			});
				
		}
			
		private void countDevices() {
    		synchronized (this) {
    			counterDevices++;
    		}
    	}	
		
	};

}
