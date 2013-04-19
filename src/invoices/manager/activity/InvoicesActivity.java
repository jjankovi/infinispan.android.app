package invoices.manager.activity;

import invoices.manager.adapter.InvoiceListAdapter;
import invoices.manager.model.Invoice;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 
 * @author jjankovi
 *
 */
public class InvoicesActivity extends Activity {

	private ProgressDialog progressDialog = null;
	
	private ListView listView;
	private RelativeLayout relativeLayout;
	
	private Button selectCount;
	private Button deleteItem;
	private Button modifyItem;
	private Button loadInvoices;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		loadViewObjects();
		updateItems();
		updateUI();
		
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_list, menu);
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
	
	/** Called when the select all button is pressed */
	public void selectAll(View view) {
		
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			((InvoiceListAdapter)listView.getAdapter()).select(Integer.valueOf(index));
			listView.setItemChecked(index, true);
		}
		updateUI();
	}
	
	/** Called when the deselect all button is pressed */
	public void deselectAll(View view) {
		
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			((InvoiceListAdapter)listView.getAdapter()).deselect(Integer.valueOf(index));
			listView.setItemChecked(index, false);
			updateUI();
		}
		
	}
	
	/** Called when the load invoices is pressed */
	public void loadInvoices(View view) {
		
		this.progressDialog = ProgressDialog.show(
				this, "Invoices loading", "Invoices are loading. It may take a while...", true, false);
		
		/** start local cache at startup **/
		new StartApplication().execute();
	}
	
	/** Called when the delete button is pressed */
	public void delete(View view) {

		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				Invoice listItem = (Invoice) listView.getItemAtPosition(index);
				MainActivity.cacheManager.remove(listItem.getId());
			}
		}
		updateItems();
		updateUI();
	}

	/** Called when the modify button is pressed */
	public void modify(View view) {
		
		for (int index = 0; index < listView.getAdapter().getCount(); index++) {
			if (listView.isItemChecked(index)) {
				Invoice listItem = (Invoice)listView.getItemAtPosition(index);
				Intent intent = new Intent(this, AddActivity.class);
				intent.putExtra("item", MainActivity.cacheManager.get(listItem.getId()));
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			loadInvoices.setVisibility(View.INVISIBLE);
			relativeLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			
			((InvoicesMainActivity)getParent()).setMenuItemsVisibilityState(true);
			
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
			((InvoiceListAdapter)listView.getAdapter()).toggleSelected(Integer.valueOf(arg2));
			updateUI();
		}
		
	}

	/**
	 * transfer items from local cache into list view. Needed when
	 * CRUD operations on local cache are performed, and when
	 * application is started
	 * 
	 */
	public void updateItems() {
		List<Invoice> listElements = new ArrayList<Invoice>();
		DataContainer container = MainActivity.cacheManager.getAll();
		if (container != null) {
			for (InternalCacheEntry entry : container.entrySet()) {
				listElements.add((Invoice) entry.getValue());
			}
		}
		listView.setAdapter(new InvoiceListAdapter(listElements, this));
		listView.setItemsCanFocus(false);
		listView.invalidateViews();
	}
	
	/**
	 * update UI views of this activity according to existing state
	 * 
	 */
	public void updateUI() {
		
		updateCounterAndButtons();
		updateRelativeOrListLayout();
		
	}
	
	/** update selected items counter and enable state of modification buttons **/
	private void updateCounterAndButtons() {
		
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
	
	/**
	 * Enables or disables delete and modify button
	 * @param deleteButtonShow
	 * @param modifyButtonShow
	 */
	private void enableButtons(boolean deleteButtonShow, 
			boolean modifyButtonShow) {
		deleteItem.setEnabled(deleteButtonShow);			
		modifyItem.setEnabled(modifyButtonShow);
	}
	
	/**
	 * According to cache state either list with items
	 * are shown or button to load items is shown
	 */
	private void updateRelativeOrListLayout() {
		listView.setVisibility(
				MainActivity.cacheManager.isCacheStarted()?
				View.VISIBLE:
				View.INVISIBLE);
		loadInvoices.setVisibility(MainActivity.cacheManager.isCacheStarted()?
				View.INVISIBLE:
				View.VISIBLE);
		relativeLayout.setVisibility(MainActivity.cacheManager.isCacheStarted()?
				View.GONE:
				View.VISIBLE);
		
	}

}