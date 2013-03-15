package invoices.manager.activity;

import invoices.manager.jgroups.JgroupsHelper;
import invoices.manager.logger.LoggerFactory;
import invoices.manager.wifi.WifiHelper;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;

/**
 * 
 * @author jjankovi
 *
 */
public class SearchingDevicesActivity extends Activity {

	private final static String PORT = "[7800]";
	
	private static final Logger log = LoggerFactory.getLogger(
			SearchingDevicesActivity.class);
	
	private ProgressDialog progressDialog = null;
	
	private ListView foundDevices;
	private ListView connectedDevices;
	
	private Button joinButton;
	
	private RadioButton connectedButton;
	private RadioButton foundButton;
	
	private static int counter = 0;
	private static int counterDevices = 0;
	
	private static List<String> foundDevicesList = 
		Collections.synchronizedList(new ArrayList<String>());
	private static List<String> connectedDevicesList = 
			Collections.synchronizedList(new ArrayList<String>());
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_devices);
        loadViews();
        updateJoinButtonState();
        updateDevices(foundDevices, foundDevicesList);

    }

	private void loadViews() {
		foundDevices = (ListView)findViewById(R.id.foundDevicesList);
        foundDevices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        foundDevices.setOnItemClickListener(new CustomListClickListener());
        
        connectedDevices = (ListView)findViewById(R.id.connectedDevicesList);
        connectedDevices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        connectedDevices.setOnItemClickListener(new CustomListClickListener());
        
        joinButton = (Button)findViewById(R.id.join_item);
        
        connectedButton = (RadioButton)findViewById(R.id.connectedDevices);
        foundButton = (RadioButton)findViewById(R.id.foundDevices);
        
        connectedButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				connectedDevices.setEnabled(isChecked);
				foundButton.setChecked(!isChecked);
				updateJoinButtonState();
			}
		});
        
        foundButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				foundDevices.setEnabled(isChecked);
				connectedButton.setChecked(!isChecked);
				updateJoinButtonState();
			}
		});
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_searching_devices, menu);
        return true;
    }
	
    /** method is invoked when search button is pressed **/
    public void searchDevices(View view) {
    	
    	startDeviceSearching();
    	
    }
    
    /** method is invoked when Join button is pressed **/
    public void join(View view) {
    	
    	/** get devices which will be chosen to join to **/
    	Collection<String> devicesToJoin = getDevicesToJoin();
    	
    	/** add all devices chosen to join to into connected devices list **/
    	connectedDevicesList.clear();
    	Iterator<String> iter = devicesToJoin.iterator();
    	while (iter.hasNext()) {
    		String temp = iter.next();
    		temp = temp.split("\\[")[0]; // not contain port, only IP address
    		connectedDevicesList.add(temp);
    	}
    	updateDevices(connectedDevices, connectedDevicesList);
    	
    	/** devices to join to should contain local address as well **/
    	devicesToJoin.add(WifiHelper.getWifiHelper().getDeviceIpAddress(this) + PORT);
    	JgroupsHelper.getInstance().configureJgroups(this, devicesToJoin);
    	
    	/** check all connected devices at default **/
    	changeStateofAllDevices(connectedDevices, true);
    	
    	/** uncheck all found devices at default **/
    	changeStateofAllDevices(foundDevices, false);
    	
    	updateJoinButtonState();
    	connectedDevices.setEnabled(false);
    }
    
    /**
     * Join button is enabled only if at least one device 
     * in active devices list is checked. 
     */
    private void updateJoinButtonState() {
    	joinButton.setEnabled(
    			connectedButton.isChecked()?
    			updateJoinButtonState(connectedDevices):
    			updateJoinButtonState(foundDevices));
	}
    
    /**
     * Method checks if at least one item in list is checked
     * @param 		devices on which checking is performed
     * @return		true or false there is at 
     * 				least one item is checked
     */
    private boolean updateJoinButtonState(ListView devices) {
    	if (devices.getAdapter() != null) {
    		if (!devices.getAdapter().isEmpty()) {
        		for (int index = 0; index < devices.getAdapter().getCount(); index++) {
        			if (devices.isItemChecked(index)) {
        				return true;
        			}
        		}
        	}
    	}
    	return false;
    }
    
    /**
     * Gets devices to join according to which list is activated
     * @return
     */
    private Collection<String> getDevicesToJoin() {
    	
    	if (connectedButton.isChecked()) {
    		return getDevicesToJoin(connectedDevices);
    	} else {
    		return getDevicesToJoin(foundDevices);
    	}
    }
    
    /**
     * On defined devices there is iteration which returns 
     * list of all checked items
     * @param devices
     * @return
     */
    private Collection<String> getDevicesToJoin(ListView devices) {
    	Collection<String> devicesToJoin = new ArrayList<String>();
    	if (devices.getAdapter() != null) {
    		if (!devices.getAdapter().isEmpty()) {
        		for (int index = 0; index < devices.getAdapter().getCount(); index++) {
        			if (devices.isItemChecked(index)) {
        				devicesToJoin.add(devices.getItemAtPosition(index).toString() + PORT);
        			}
        		}
        	}
    	}
    	return devicesToJoin;
	}

	/**
     * according to ip address of device, discovering of accessible devices is started
     */
    private void startDeviceSearching() {
    	String myIpAddress = WifiHelper.getWifiHelper().getDeviceIpAddress(this);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.progressDialog = ProgressDialog.show(
				this, "Network scan", "Cluster devices are discovering. It may take a while...", true, false);
		
		foundDevicesList.clear();
		InvoicesMainActivity parent = (InvoicesMainActivity)getParent();
		String[] searchInterval = parent.getSearchInterval();
		runSearchingThreads(myIpAddress, searchInterval[0], searchInterval[1]);

    }
    
    private void runSearchingThreads(String myIpAddress, String downIP, String upperIP) {
    	int globalStart = Integer.parseInt(downIP);
    	int globalEnd = Integer.parseInt(upperIP);
    	int threadIterations = 5; // how many iterations is performed per thread
    	
    	int intervalCount = globalEnd - globalStart + 1; // interval between globalStart and globalEnd
    	int threadsCount = intervalCount / threadIterations; // how many threads will be used
    	if (intervalCount % threadIterations > 0) threadsCount++; // if one thread will run less than 5 iterations
    	
    	int localStart = globalStart;
    	int localEnd = localStart + threadIterations - 1;
    	for (int i = 0; i < threadsCount; i++) {
    		if (localEnd > globalEnd) localEnd = globalEnd; // the last thread may run less than 5 iterations
    		log.info("Thread #" + (i+1) + ", localStart: " + localStart + ", localEnd: " + localEnd);
    		new SearchThread(localStart, localEnd, myIpAddress).start();
    		localStart = localEnd + 1;
    		localEnd = localStart + threadIterations - 1;
    	}
    }
    
    /**
     * elements from parameter collection object are added into listview widget and invalidated 
     * 
     * @param searchDevices
     */
    private void updateDevices(ListView listView, List<String> devices) {
    	listView.setAdapter(new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_multiple_choice, 
				devices));
    	listView.setItemsCanFocus(false);
    	listView.invalidateViews();
	}
    
    private void changeStateofAllDevices(ListView devices, boolean state) {
    	if (devices.getAdapter() != null) {
    		for (int index = 0; index < devices.getAdapter().getCount(); index++) {
    			devices.setItemChecked(index, state);
        	}
    	}
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
		
		public SearchThread(int from, int to, String localIpAddress) {
			
			this.from = from;
			this.to = to;
			this.localIpAddress = localIpAddress;
			increaseCounter();
		}
		
		@Override
		public void run() {
			
			for (int i = from; i <= to; i++) {
    			String device = WifiHelper.getWifiHelper().getIpAddress(localIpAddress, i + "");
    			if (device.equals(localIpAddress)) continue;
    			try {
    				new Socket(device, 7800);
    				foundDevicesList.add(device);
    			} catch (Exception e) {
    			}
    			countDevices();
    		}
			decreaseCounter();
			runOnUiThread(new Runnable() {
				public void run() {
					
    	    		if (counter == 0) { // this is last thread
    	    			dismissProgressDialog();
    	    			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	    			updateDevices(foundDevices, foundDevicesList);
    	    			joinButton.setEnabled(false);
    	    			
    	    			log.info(counterDevices + " devices found");
    	    			log.info(foundDevicesList.size());
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
