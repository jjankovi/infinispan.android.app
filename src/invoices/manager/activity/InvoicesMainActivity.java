package invoices.manager.activity;

import invoices.manager.model.Invoice;
import invoices.manager.network.PortHelper;
import invoices.manager.wifi.WifiHelper;

import java.util.Properties;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * 
 * @author jjankovi
 *
 */
public class InvoicesMainActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_main);
                
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        
        Intent intent;
        
        intent = new Intent().setClass(this, InvoicesActivity.class);
        
        spec = tabHost.newTabSpec("invoices").setIndicator("Invoices").setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, SearchingDevicesActivity.class);
        
        spec = tabHost.newTabSpec("devices").setIndicator("Devices").setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
        
    
        PortHelper.getInstance().listeningOnPort(7800);
        
        Properties props = System.getProperties();
        props.setProperty("jgroups.tcpping.initial_hosts", 
        		WifiHelper.getWifiHelper().getDeviceIpAddress(this) + "[7800]");
        
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_invoices_main, menu);
        return true;
    }
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		if (!MainActivity.cacheManager.isCacheStarted()) {
			Toast.makeText(this, "You have to load invoices", 
					Toast.LENGTH_LONG).show();
			return false;
		}
		
		switch (item.getItemId()) {
		case R.id.add_item:
			Intent intent = new Intent(this, AddActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.refresh:
			updateChildUI();
			break;
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Invoice invoice = (Invoice)data.getExtras().getSerializable("item");
			if (invoice != null) {
				MainActivity.cacheManager.put(invoice.getId(), invoice);
			}
			updateChildUI();
		}
	}
	
	private void updateChildUI() {
		((InvoicesActivity)getLocalActivityManager().getCurrentActivity()).updateItems();
		((InvoicesActivity)getLocalActivityManager().getCurrentActivity()).updateUI();
	}
	
}
