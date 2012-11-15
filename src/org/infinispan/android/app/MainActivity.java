package org.infinispan.android.app;

import org.infinispan.android.app.cache.LocalCacheManager;
import org.infinispan.container.DataContainer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private LocalCacheManager localCache;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		localCache = new LocalCacheManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		getBaseContext();
		return true;
	}

	/** Called when the start button is pressed */
	public void start(View view) {

		new StartCacheTask().execute();

	}

	/** Called when the stop button is pressed */
	public void stop(View view) {

		new StopCacheTask().execute();

	}
	
	/** Called when the put is pressed */
	public void put(View view) {

		EditText key = (EditText)findViewById(R.id.editText1);
		EditText value = (EditText)findViewById(R.id.editText2);
		localCache.put(key.getText().toString(), value.getText().toString());
		Toast msg = Toast.makeText(getBaseContext(),
				"Element (" + key.getText().toString() + ": " + 
				value.getText().toString() + ") was added into infinispan", Toast.LENGTH_SHORT);
		msg.show();
		key.setText(null);
		value.setText(null);
	}
	
	/** Called when the put is pressed */
	public void remove(View view) {
		
		EditText key = (EditText)findViewById(R.id.editText1);
		String value = localCache.remove(key.getText().toString());
		Toast msg = null;
		if (value!=null) {
			msg = Toast.makeText(getBaseContext(),
					"Element (" + key.getText().toString() + ": " + 
					value + ") was removed from infinispan", Toast.LENGTH_SHORT);
			key.setText(null);
			EditText valueText = (EditText)findViewById(R.id.editText2);
			valueText.setText(null);
		} else {
			msg = Toast.makeText(getBaseContext(),
					"Element (" + key.getText().toString() + 
					") was not found in cache, thus not removed", Toast.LENGTH_SHORT);
		}
		msg.show();
		
	}
	
	/** Called when the values is pressed */
	public void values(View view) {
		
		DataContainer entries = localCache.getAll();
		Toast msg = Toast.makeText(getBaseContext(), 
				entries.entrySet().toString(), Toast.LENGTH_SHORT);
		msg.show();
		
	}
	
	private class StartCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			localCache.startCache();
			return null;
		}
		
	}
	
	private class StopCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			localCache.stopCache();
			return null;
		}
		
	}

}