package org.infinispan.android.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.infinispan.android.app.cache.LocalCacheManager;
import org.infinispan.android.app.model.CacheElement;
import org.infinispan.container.DataContainer;
import org.infinispan.container.entries.InternalCacheEntry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ListActivity extends Activity {

	private static LocalCacheManager localCache = null;
	
	private ProgressDialog progressDialog = null;
	
	private int modifedItemId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setOnItemClickListener(new CustomListClickListener());
				
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		
		this.progressDialog = ProgressDialog.show(
				this, "Starting the application", "Local cache starting...", true, false);
		
		if (localCache == null) {
			localCache = new LocalCacheManager();
		}
		
		/** start local cache at startup **/
		new StartApplication().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int id;
			
			/** if request code was 1 we are 
			 *  going to add a new item **/
			if (requestCode == 1) {
				id = new Random().nextInt(10000);
				
				/** if request code was not 1 we are 
				 *  going to modify an existing item **/
			} else {
				id = modifedItemId;
			}
			localCache.put(id, 
					new CacheElement(
					data.getStringExtra("value1"), 
					data.getStringExtra("value2")));
			updateItems();
			updateUI();
		}
	}
	
	/** Called when the select all button is pressed */
	public void selectAll(View view) {
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			listView.setItemChecked(index, true);
			updateUI();
		}
		
	}
	
	/** Called when the deselect all button is pressed */
	public void deselectAll(View view) {
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			listView.setItemChecked(index, false);
			updateUI();
		}
		
	}
	
	/** Called when the add button is pressed */
	public void add(View view) {
		
		Intent intent = new Intent(this, AddActivity.class);
		startActivityForResult(intent, 1);
		
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
		updateItems();
		updateUI();
	}

	/** Called when the modify button is pressed */
	public void modify(View view) {
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				String listItem = (String) listView.getItemAtPosition(index);
				modifedItemId = Integer.parseInt(listItem.split(" ")[0]);
				String value1 = listItem.split(" ")[1];
				String value2 = listItem.split(" ")[2];
				Intent intent = new Intent(this, AddActivity.class);
				intent.putExtra("value1", value1);
				intent.putExtra("value2", value2);
				startActivityForResult(intent, 2);
			}
		}
	}
	
	/**
	 * Async task to start the application, performing:
	 * 
	 * #1 if local cache is not started, then start a new instance
	 * #2 generate 3 random elements into cache
	 * #3 update items in list view and ui
	 * #4 after all work is done, then dismiss Progress dialog
	 * 
	 * @author jjankovi
	 *
	 */
	private class StartApplication extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (!localCache.isCacheStarted()) {
				localCache.startCache();
				generateCacheElements(3);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateItems();
			updateUI();
			if (ListActivity.this.progressDialog != null) {
                ListActivity.this.progressDialog.dismiss();
            }
		}

	}
	
	/**
	 * Custom listener for ListView to update UI when state of checked items
	 * has changed
	 * 
	 * @author jjankovi
	 *
	 */
	private class CustomListClickListener implements AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			updateUI();
		}
		
	}

	/**
	 * generate random elements into local cache
	 * 
	 * @param elementsCount number of elements to be generated
	 */
	private void generateCacheElements(int elementsCount) {
		for (int i = 0; i < elementsCount; i++) {
			localCache.put(Integer.valueOf(i), new CacheElement(
					"1data" + i + i, "2data" + i + i));
		}
	}

	/**
	 * transfer items from local cache into list view. Needed when
	 * CRUD operations on local cache are performed, and when
	 * application is started
	 * 
	 */
	private void updateItems() {
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
	
	/**
	 * update selected items counter and enable state of modification buttons
	 * 
	 */
	private void updateUI() {
		
		/** obtain the count of selected items in list view **/
		ListView listView = (ListView)findViewById(R.id.listView1);
		int checkedCount = 0;
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				checkedCount++;
			}
		}
		/** update selected items counter **/
		Button button = (Button)findViewById(R.id.select_count);
		button.setText(checkedCount + " items");
		
		/** show delete/modify buttons according to count of selected items **/
		if (checkedCount < 1) {
			enableButtons(false, false);
		} else if (checkedCount == 1) {
			enableButtons(true, true);
		} else {
			enableButtons(true, false);
		}
	}
	
	private void enableButtons(boolean deleteButtonShow, 
			boolean modifyButtonShow) {
		Button button = (Button)findViewById(R.id.delete_item);
		button.setEnabled(deleteButtonShow);			
		button = (Button)findViewById(R.id.modify_item);
		button.setEnabled(modifyButtonShow);
	}

}