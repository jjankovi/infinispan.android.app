package invoices.manager.activity;

import invoices.manager.controller.CacheManager;
import invoices.manager.dialog.AboutDialog;
import invoices.manager.logger.LoggerFactory;
import invoices.manager.wifi.WifiHelper;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.infinispan.configuration.cache.CacheMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * MainActivity is the first activity started right after application 
 * run
 * 
 * @author jjankovi
 *
 */
public class MainActivity extends Activity {
	
	private Logger log = LoggerFactory.getLogger(MainActivity.class);
	
	public static CacheManager cacheManager = null;
	
	private static final int RESULT_SETTINGS = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
        
        initiateCacheManager();
        
        Properties props = System.getProperties();
        props.setProperty("java.net.preferIPv4Stack", "true");
        
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			AboutDialog aboutDialog = new AboutDialog(this);
			aboutDialog.setTitle("Invoices Manager");
			aboutDialog.show();
			break;
			
		case R.id.shutdown2:
			Editor prefEditor = PreferenceManager
            	.getDefaultSharedPreferences(this).edit();
			prefEditor.clear();
			prefEditor.commit();
			new ApplicationShutDownTask().execute();
			break;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Process.killProcess(Process.myPid());
	}
	
    
    /** Called when invoices item button is pressed */
	public void invoices(View view) {
		
		handleWifiState();
		
	}
	
	/** 
	 * 	Called when configure button is pressed.
	 * 	After settings are done, cache settings 
	 *  are initiated again according to configuration
	 *  settings
	 *  
	 */
	public void configure(View view) {
		
		Intent intent = new Intent(this, ConfigureActivity.class);
		startActivityForResult(intent, RESULT_SETTINGS);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case RESULT_SETTINGS:
			setCache(); 		// set cache according to configure options
			break;
		}
		
	}
	
	private void setCache() {
		
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
		
		String cacheModeNew = sharedPrefs.getString("mode", "local");
		String numOwnersNew = "1";
		boolean l1CacheNew = false;
		
		if (cacheModeNew.equals("distributed")) {
			cacheManager.cacheConfiguration().setCacheMode(CacheMode.DIST_ASYNC);
			numOwnersNew = sharedPrefs.getString("owners", "1");
			l1CacheNew = sharedPrefs.getBoolean("l1", false);
		} else if(cacheModeNew.toLowerCase().equals("local")) {
			cacheManager.cacheConfiguration().setCacheMode(CacheMode.LOCAL);
		} else if (cacheModeNew.toLowerCase().equals("replicated")) {
			cacheManager.cacheConfiguration().setCacheMode(CacheMode.REPL_ASYNC);
		}
		
		boolean cacheStoreNew = sharedPrefs.getBoolean("store", false);
		
		cacheManager.cacheConfiguration().setNumOwners(Integer.parseInt(numOwnersNew));
		cacheManager.cacheConfiguration().setL1Cache(l1CacheNew);
		cacheManager.cacheConfiguration().setCacheStore(cacheStoreNew);
		
	}
	
	private void handleWifiState() {
		 
		if(!WifiHelper.getWifiHelper().isConnectedToWifiNetwork(this)) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Wifi Network").
						  setMessage("To access to Invoices you have to be " +
								  	 "connected to wireless network (3G is not sufficient). " +
								  	 "You will now be redirected to wireless network settings...");
			dialogBuilder.setPositiveButton(R.string.ok, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
			});
			dialogBuilder.setNegativeButton(R.string.cancel, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			dialogBuilder.create().show();
		}else {
			Intent intent = new Intent(this, InvoicesMainActivity.class);
			startActivity(intent);
		}
	}

	private void initiateCacheManager() {
    	
		if (cacheManager == null) {
			cacheManager = new CacheManager();
		}
		
	}

	/**
	 * Async task to shutdown app, performing two tasks:
	 * 
	 * #1 static local cache attribute is set to null -> force to create a new
	 * 	  local cache when activity is opened again
	 * #2 method Activity.finished() is called to finish main activity
	 * 
	 * @author jjankovi
	 *
	 */
	private class ApplicationShutDownTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			cacheManager.stopCache();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			MainActivity.cacheManager = null;
			log.info("Application is shutting down");
			finish();
		}

	}
	
}
