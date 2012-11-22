package org.infinispan.android.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.infinispan.android.app.cache.LocalCacheManager;
import org.infinispan.android.app.model.CacheElement;
import org.infinispan.container.DataContainer;
import org.infinispan.container.entries.InternalCacheEntry;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	private LocalCacheManager localCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		enableButtons(false, false);
		
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setOnItemClickListener(new CustomListClickListener());
				
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());

		localCache = new LocalCacheManager();
		new StartCacheTask().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.add_item:
				Intent intent = new Intent(this, AddActivity.class);
				startActivityForResult(intent, 1);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 1) {
			localCache.put(new Random().nextInt(10000), 
					new CacheElement(
					data.getStringExtra("value1"), 
					data.getStringExtra("value2")));
			refreshListView();
		}
	}

	/** Called when the start button is pressed */
	public void start(View view) {

		new StartCacheTask().execute();

	}

	/** Called when the stop button is pressed */
	public void stop(View view) {

		new StopCacheTask().execute();

	}
	
	/** Called when the delete button is pressed */
	public void delete(View view) {

		ListView listView = (ListView) findViewById(R.id.listView1);
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				String listItem = (String) listView.getItemAtPosition(index);
				localCache.remove(Integer.parseInt(listItem.split(" ")[0]));
			}
		}
		refreshListView();
		enableButtons(false, false);
	}

	/** Called when the modify button is pressed */
	public void modify(View view) {

		

	}
	
	private void enableButtons(boolean deleteButtonShow, 
			boolean modifyButtonShow) {
		Button button = (Button)findViewById(R.id.delete_item);
		button.setEnabled(deleteButtonShow);			
		button = (Button)findViewById(R.id.modify_item);
		button.setEnabled(modifyButtonShow);
	}
	
	private class StartCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			localCache.startCache();
			generateCacheElements(3);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			refreshListView();
		}

	}

	private class StopCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			localCache.stopCache();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			refreshListView();
		}

	}
	
	private class CustomListClickListener implements AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			ListView listView = (ListView)findViewById(R.id.listView1);
			int checkedCount = 0;
			for (int index = 0; index < listView.getAdapter().getCount(); index++) {
				if (listView.isItemChecked(index)) {
					checkedCount++;
				}
			}
			if (checkedCount < 1) {
				enableButtons(false, false);
			} else if (checkedCount == 1) {
				enableButtons(true, true);
			} else {
				enableButtons(true, false);
			}
		}
		
	}

	private void generateCacheElements(int elementsCount) {
		for (int i = 0; i < elementsCount; i++) {
			localCache.put(Integer.valueOf(i), new CacheElement(
					"1data" + i + i, "2data" + i + i));
		}
	}

	private void refreshListView() {
		List<String> listElements = new ArrayList<String>();
		DataContainer container = localCache.getAll();
		if (container != null) {
			for (InternalCacheEntry entry : container.entrySet()) {
				listElements.add(entry.getKey() + " " + entry.getValue());
			}
		}
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, listElements));
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);		
		listView.invalidateViews();
	}

}