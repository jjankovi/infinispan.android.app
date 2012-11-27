package org.infinispan.android.app;

import org.infinispan.android.app.cache.LocalCacheManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private static LocalCacheManager localCache = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (localCache == null) {
			localCache = new LocalCacheManager();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** Called when add item button is pressed */
	public void items(View view) {
		
		Intent intent = new Intent(this, ListActivity.class);
		startActivityForResult(intent, 0);
		
	}
	
	/** Called when configure button is pressed */
	public void configure(View view) {
		
		Intent intent = new Intent(this, ConfigureActivity.class);
		startActivityForResult(intent, 0);
		
	}
	
	/** Called when shutdown button is pressed */
	public void shutdown(View view) {
	
		new ApplicationShutDownTask().execute();
	
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
			localCache.stopCache();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			MainActivity.localCache = null;
			finish();
		}

	}
		
}
