package org.infinispan.android.app;

import org.infinispan.android.app.cache.LocalCacheManager;
import org.infinispan.container.DataContainer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private LocalCacheManager localCache;
	
	private boolean cacheStarted;
	
	private Button startButton;
	private Button stopButton;
	private Button valuesButton;
	private Button putButton;
	private Button removeButton;
	
	private EditText key;
	private EditText value;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		localCache = new LocalCacheManager();
		cacheStarted = false;
		redrawUI();
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
	
	private void redrawUI() {
		
		key = (EditText)findViewById(R.id.editText1);
		key.setEnabled(cacheStarted);
		value = (EditText)findViewById(R.id.editText2);
		value.setEnabled(cacheStarted);
		startButton = (Button)findViewById(R.id.button1);
		startButton.setEnabled(!cacheStarted);
		stopButton = (Button)findViewById(R.id.button2);
		stopButton.setEnabled(cacheStarted);
		putButton = (Button)findViewById(R.id.button3);
		putButton.setEnabled(cacheStarted);
		valuesButton = (Button)findViewById(R.id.button4);
		valuesButton.setEnabled(cacheStarted);
		removeButton = (Button)findViewById(R.id.button5);
		removeButton.setEnabled(cacheStarted);
		
	}
	
	private class StartCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			localCache.startCache();
			cacheStarted = true;
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			redrawUI();
		}
		
	}
	
	private class StopCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			localCache.stopCache();
			cacheStarted = false;
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			redrawUI();
		}
		
	}

}