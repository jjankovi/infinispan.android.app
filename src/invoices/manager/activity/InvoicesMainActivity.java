package invoices.manager.activity;

import invoices.manager.network.PortHelper;
import invoices.manager.wifi.WifiHelper;

import java.net.ServerSocket;
import java.util.Properties;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;

public class InvoicesMainActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_invoices_main);
        
        
        Resources res = getResources(); 
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
	
}
