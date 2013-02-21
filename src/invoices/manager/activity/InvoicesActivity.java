package invoices.manager.activity;

import invoices.manager.model.Invoice;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.container.DataContainer;
import org.infinispan.container.entries.InternalCacheEntry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.ClipData.Item;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class InvoicesActivity extends Activity {

	private ProgressDialog progressDialog = null;
	
	private ListView listView;
	private RelativeLayout relativeLayout;
	
	private Button selectCount;
	private Button deleteItem;
	private Button modifyItem;
	private Button loadInvoices;

	private MenuItem addItem;
	private MenuItem refreshItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_list);
		loadViewObjects();
		updateItems();
		updateUI();
		
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
	}
	
	private void loadViewObjects() {
		
		listView = (ListView)findViewById(R.id.elements);
		loadInvoices = (Button)findViewById(R.id.load_invoices);
		relativeLayout = (RelativeLayout)findViewById(R.id.RelativeLayout01);
		selectCount = (Button)findViewById(R.id.select_count);
		deleteItem = (Button)findViewById(R.id.delete_item);
		modifyItem = (Button)findViewById(R.id.modify_item);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new CustomListClickListener());
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		addItem = menu.getItem(0);
	    refreshItem = menu.getItem(1);
	    
	    addItem.setVisible(MainActivity.cacheManager.isCacheStarted());
		refreshItem.setVisible(MainActivity.cacheManager.isCacheStarted());
		
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_list, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.add_item:
			Intent intent = new Intent(this, AddActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.refresh:
			updateItems();
			updateUI();
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
			updateItems();
			updateUI();
		}
	}
	
	/** Called when the add imagebutton is pressed */
	public void addInvoice(View view) {
		
		Intent intent = new Intent(this, AddActivity.class);
		startActivityForResult(intent, 1);
		
	}
	
	/** Called when the refresh imagebutton is pressed */
	public void refresh(View view) {
		
		updateItems();
		updateUI();
		
	}
	
	/** Called when the select all button is pressed */
	public void selectAll(View view) {
		
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			listView.setItemChecked(index, true);
			updateUI();
		}
		
	}
	
	/** Called when the deselect all button is pressed */
	public void deselectAll(View view) {
		
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			listView.setItemChecked(index, false);
			updateUI();
		}
		
	}
	
	/** Called when the load invoices is pressed */
	public void loadInvoices(View view) {
		
		this.progressDialog = ProgressDialog.show(
				this, "Invoices loading", "Invoices are loading...", true, false);
		
		/** start local cache at startup **/
		new StartApplication().execute();
	}
	
	/** Called when the delete button is pressed */
	public void delete(View view) {

		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				String listItem = (String) listView.getItemAtPosition(index);
				MainActivity.cacheManager.remove(Integer.parseInt(listItem.split(" ")[0]));
			}
		}
		updateItems();
		updateUI();
	}

	/** Called when the modify button is pressed */
	public void modify(View view) {
		
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				String listItem = (String) listView.getItemAtPosition(index);
				Intent intent = new Intent(this, AddActivity.class);
				intent.putExtra("item", MainActivity.cacheManager.get(
						Integer.parseInt(listItem.split(" ")[0])));
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
			if (!MainActivity.cacheManager.isCacheStarted()) {
				MainActivity.cacheManager.startCache();
//				generateCacheElements(3);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			loadInvoices.setVisibility(View.INVISIBLE);
			relativeLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			
			updateItems();
			updateUI();
			
			if (InvoicesActivity.this.progressDialog != null) {
                InvoicesActivity.this.progressDialog.dismiss();
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
//	private void generateCacheElements(int elementsCount) {
//		for (int i = 0; i < elementsCount; i++) {
//			MainActivity.cacheManager.put(Integer.valueOf(i), 
//					new Invoice("item" + i, i+20, "information" + i, "manufacturer" + i));
//		}
//		MainActivity.cacheManager.put(12, new Invoice(1, new GregorianCalendar(2012, 12, 19), 
//				new GregorianCalendar(2012, 12, 19), (long)12.12, 
//				"Jaroslav", "D. Makovickeho", "Martin"));
//	}

	/**
	 * transfer items from local cache into list view. Needed when
	 * CRUD operations on local cache are performed, and when
	 * application is started
	 * 
	 */
	private void updateItems() {
		List<String> listElements = new ArrayList<String>();
		DataContainer container = MainActivity.cacheManager.getAll();
		if (container != null) {
			for (InternalCacheEntry entry : container.entrySet()) {
				listElements.add(entry.getKey() + " " + entry.getValue());
			}
		}
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, listElements));
		listView.setItemsCanFocus(false);
		listView.invalidateViews();
	}
	
	/**
	 * update UI of activity
	 * 
	 */
	private void updateUI() {
		
		updateCounterAndButtons();
		updateRelativeOrListLayout();
		
	}
	
	/** update selected items counter and enable state of modification buttons **/
	private void updateCounterAndButtons() {
		
//		addButton.setEnabled(MainActivity.cacheManager.isCacheStarted());
//		refreshButton.setEnabled(MainActivity.cacheManager.isCacheStarted());
		
		/** obtain the count of selected items in list view **/
		int checkedCount = 0;
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				checkedCount++;
			}
		}
		/** update selected items counter **/
		selectCount.setText(checkedCount + " items");
		
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
		deleteItem.setEnabled(deleteButtonShow);			
		modifyItem.setEnabled(modifyButtonShow);
	}
	
	/**  **/
	private void updateRelativeOrListLayout() {
		/**  **/
		if (!MainActivity.cacheManager.isCacheStarted()) {
			listView.setVisibility(View.INVISIBLE);
		} else {
			loadInvoices.setVisibility(View.INVISIBLE);
			relativeLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}

}