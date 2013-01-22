package invoices.manager.activity;

import invoices.manager.cache.CacheManager;

import org.infinispan.configuration.cache.CacheMode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	public static CacheManager cacheManager = null;
	
	private static final int RESULT_SETTINGS = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (cacheManager == null) {
			cacheManager = new CacheManager();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** Called when invoices item button is pressed */
	public void invoices(View view) {
		
		Intent intent = new Intent(this, InvoicesActivity.class);
		startActivity(intent);
		
	}
	
	/** 
	 * 	Called when configure button is pressed.
	 * 	After settings are done, cache settings 
	 *  are initiated again according to configure
	 *  settings
	 *  
	 */
	public void configure(View view) {
		
		Intent intent = new Intent(this, ConfigureActivity.class);
		startActivityForResult(intent, RESULT_SETTINGS);
		
	}
	
	/** Called when shutdown button is pressed */
	public void shutdown(View view) {
	
		new ApplicationShutDownTask().execute();
	
	}
	
	/** Called when help button is pressed */
	public void help(View view) {
	
	
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
		
		setCacheMode(sharedPrefs.getString("mode", "NULL"));
		setCacheStore(sharedPrefs.getBoolean("store", false));
		
	}

	private void setCacheMode(String cacheMode) {
		
		if (cacheMode.toLowerCase().equals("local")) {
			cacheManager.setCacheMode(CacheMode.LOCAL);
		}else if (cacheMode.toLowerCase().equals("replicated")) {
			cacheManager.setCacheMode(CacheMode.REPL_ASYNC);
		}else if (cacheMode.toLowerCase().equals("distributed")) {
			cacheManager.setCacheMode(CacheMode.DIST_ASYNC);
		}
		cacheManager.cacheInitialization();
		
	}
	
	private void setCacheStore(boolean setCacheStore) {
		
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
			finish();
		}

	}
	
}