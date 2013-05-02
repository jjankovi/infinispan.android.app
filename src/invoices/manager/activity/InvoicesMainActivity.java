package invoices.manager.activity;

import invoices.manager.model.Invoice;
import invoices.manager.network.PortHelper;
import invoices.manager.wifi.WifiHelper;

import java.util.Properties;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

/**
 * 
 * @author jjankovi
 *
 */
@SuppressWarnings("deprecation")
public class InvoicesMainActivity extends TabActivity {

	private final static int SEACH_PARAM_DIALOG = 1;
	private final static int FROM_IP_ADDRESS_DIALOG = 2;
	private final static int TO_IP_ADDRESS_DIALOG = 3;
	
	private static String fromIpAddress = "1";
	private static String toIpAddress = "255";
	
	private Menu menuRef;
	
	private MenuItem cacheStop;
	private MenuItem refresh;
	private MenuItem addItem;
	private MenuItem editSearchParam;
	private TextView fromAddressLabel;
	private TextView toAddressLabel;
	private ProgressDialog progressDialog;
	
	private TabHost tabHost;
	
	private boolean invoicesMenuItemsVisibilityState;
	private boolean searchParamsVisibilityState;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_invoices_main);
                
        tabHost = getTabHost();
        TabHost.TabSpec spec;
        
        Intent intent;
        
        intent = new Intent().setClass(this, InvoicesActivity.class);
        
        spec = tabHost.newTabSpec("invoices").setIndicator("Invoices").setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, SearchingDevicesActivity.class);
        
        spec = tabHost.newTabSpec("devices").setIndicator("Devices").setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
        
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				setSearchParamMenuItemVisibilityState();
			}
		});
    
        PortHelper.getInstance().listeningOnPort(7800);
        
        Properties props = System.getProperties();
        props.setProperty("jgroups.tcpping.initial_hosts", 
        		WifiHelper.getWifiHelper().getDeviceIpAddress(this) + "[7800]");
            
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
    		getMenuInflater().inflate(R.menu.activity_invoices_main, menu);
    		
    		cacheStop = menu.findItem(R.id.cacheStop);
    		refresh = menu.findItem(R.id.refresh);
    		addItem = menu.findItem(R.id.add_item);
    		editSearchParam = menu.findItem(R.id.searching_interval);
        	
    		invoicesMenuItemsVisibilityState = MainActivity.cacheManager.isCacheStarted();
    		
    		setMenuItemsVisibilityState(invoicesMenuItemsVisibilityState);
    		setSearchParamMenuItemVisibilityState();
    	}
    	return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
    		menuRef = menu;
        	menuRef.clear();
        	
        	getMenuInflater().inflate(R.menu.activity_invoices_main, menuRef);
        	menuRef.removeItem(R.id.menu_dummy);
        	
        	cacheStop = menuRef.findItem(R.id.cacheStop);
    		refresh = menuRef.findItem(R.id.refresh);
    		addItem = menuRef.findItem(R.id.add_item);
    		editSearchParam = menuRef.findItem(R.id.searching_interval);
        	
    		invoicesMenuItemsVisibilityState = MainActivity.cacheManager.isCacheStarted();
    		setMenuItemsVisibilityState(invoicesMenuItemsVisibilityState);
    		setSearchParamMenuItemVisibilityState();
    		
    		return invoicesMenuItemsVisibilityState || tabHost.getCurrentTab() == 1;
    	} 
    	return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.add_item:
			Intent intent = new Intent(this, AddActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.refresh:
			updateInvoicesActivityUI();
			break;
		case R.id.cacheStop:
			progressDialog = ProgressDialog.show(
					this, "Cache stopping", "Cache is stopping. It may take a while...", true, false);
			new StopCache().execute();
			break;
		case R.id.searching_interval:
			showDialog(SEACH_PARAM_DIALOG);
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
			updateInvoicesActivityUI();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) {
		
		switch (id) {
		case SEACH_PARAM_DIALOG:
			LayoutInflater inflater = LayoutInflater.from(this);
			final View dialogView = inflater.inflate(R.layout.dialog_searching_interval, null);
			final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle("Set interval to scan")
				.setView(dialogView)				
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(id);
					}
				})
				.create();
			alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					final TextView errorText = (TextView)dialogView.findViewById(R.id.error_from_to);
					errorText.setVisibility(View.GONE);
					Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							int from = Integer.parseInt(WifiHelper.getWifiHelper().getSubnetIPAddressNode(
									fromAddressLabel.getText().toString()));
							int to = Integer.parseInt(WifiHelper.getWifiHelper().getSubnetIPAddressNode(
									toAddressLabel.getText().toString()));
							if (from > to) {
								errorText.setText("'Start of interval' must be lower than 'End of interval'!");
								errorText.setTextColor(Color.RED);
								errorText.setVisibility(View.VISIBLE);
								return;
							}
							fromIpAddress = from + "";
							toIpAddress = to + "";
							alertDialog.dismiss();
						}
					});
				}
			});
			return alertDialog;
		case FROM_IP_ADDRESS_DIALOG:
			inflater = LayoutInflater.from(this);
			final View dialogView1 = inflater.inflate(R.layout.input_ip_address, null);
			final AlertDialog alertDialog1 = new AlertDialog.Builder(this)
				.setTitle("Specify IP Address")
				.setView(dialogView1)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(id);
					}
				})
				.create();
			alertDialog1.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					final TextView errorText = (TextView)dialogView1.findViewById(R.id.error);
					errorText.setVisibility(View.GONE);
					Button b = alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							TextView tv = (TextView)dialogView1.findViewById(R.id.inputIpAddress);
							String error = validation(tv);
							
							if (error != null) {
								errorText.setText(error);
								errorText.setVisibility(View.VISIBLE);
								errorText.setTextColor(Color.RED);
								return;
							}
							
							fromAddressLabel.setText(tv.getText().toString());
							alertDialog1.dismiss();
						}
					});
				}
			});			
			return alertDialog1;
		case TO_IP_ADDRESS_DIALOG:
			inflater = LayoutInflater.from(this);
			final View dialogView2 = inflater.inflate(R.layout.input_ip_address, null);
			final AlertDialog alertDialog2 = new AlertDialog.Builder(this)
				.setTitle("Specify IP Address")
				.setView(dialogView2)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(id);
					}
				})
				.create();
			alertDialog2.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					Button b = alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE);
					final TextView errorText = (TextView)dialogView2.findViewById(R.id.error);
					b.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							TextView tv = (TextView)dialogView2.findViewById(R.id.inputIpAddress);
							String error = validation(tv);
							
							if (error != null) {
								errorText.setText(error);
								errorText.setVisibility(View.VISIBLE);
								errorText.setTextColor(Color.RED);
								return;
							}
							
							toAddressLabel.setText(tv.getText().toString());
							alertDialog2.dismiss();
						}
					});
				}
			});
			return alertDialog2;
		}
		return null;	
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch (id) {
		case SEACH_PARAM_DIALOG:
			TextView localIpAddressText = (TextView)dialog.findViewById(R.id.localIpAddressText);
			localIpAddressText.setText(WifiHelper.getWifiHelper().getDeviceIpAddress(this));
			
			fromAddressLabel = (TextView)dialog.findViewById(R.id.fromIpAddressLabel);
			fromAddressLabel.setText(WifiHelper.getWifiHelper().getIpAddress(
					localIpAddressText.getText().toString(), fromIpAddress));
			toAddressLabel = (TextView)dialog.findViewById(R.id.toIpAddressLabel);
			toAddressLabel.setText(WifiHelper.getWifiHelper().getIpAddress(
					localIpAddressText.getText().toString(), toIpAddress));
			
			LinearLayout layout = (LinearLayout)dialog.findViewById(R.id.fromIpAddressRow);
			layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(FROM_IP_ADDRESS_DIALOG);
				}
			});	
			layout = (LinearLayout)dialog.findViewById(R.id.toIpAddressRow);
			layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(TO_IP_ADDRESS_DIALOG);
				}
			});	
			break;
		case FROM_IP_ADDRESS_DIALOG:
			EditText input = (EditText) dialog.findViewById(R.id.inputIpAddress);
			input.setText(fromAddressLabel.getText().toString());
			input.setSelection(input.getText().toString().length());
			break;
		case TO_IP_ADDRESS_DIALOG:
			input = (EditText) dialog.findViewById(R.id.inputIpAddress);
			input.setText(toAddressLabel.getText().toString());
			input.setSelection(input.getText().toString().length());
			break;
		}
		
	}
	
	public void setMenuItemsVisibilityState(boolean state) {
		invoicesMenuItemsVisibilityState = state;
		cacheStop.setVisible(invoicesMenuItemsVisibilityState);
    	refresh.setVisible(invoicesMenuItemsVisibilityState);
    	addItem.setVisible(invoicesMenuItemsVisibilityState);
	}
	
	public String[] getSearchInterval() {
		return new String[]{fromIpAddress, toIpAddress};
	}
	
	private void updateInvoicesActivityUI() {

		((InvoicesActivity)getLocalActivityManager().getActivity("invoices")).updateItems();
		((InvoicesActivity)getLocalActivityManager().getActivity("invoices")).updateUI();
		
	}
	
	private void setSearchParamMenuItemVisibilityState() {
		searchParamsVisibilityState = tabHost.getCurrentTab() == 1;
		
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			editSearchParam.setVisible(searchParamsVisibilityState);
		} else {
			if (editSearchParam != null && !searchParamsVisibilityState) {
				menuRef.removeItem(R.id.searching_interval);
			}
		}
	}
	
	/**
	 * Validate IP address
	 * @param tv
	 * @return
	 */
	private String validation(TextView tv) {
		String error = null;
		String input = tv.getText().toString();
		String regex = "^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$";
		
		/* format is x.x.x.x and x is numeric */
		if (!input.matches(regex)) {
			error = "IP address is not valid!";
		}
		
		/* subnet ip address matches the local one */
		if (error == null) {
			String subnet = WifiHelper.getWifiHelper().getSubnetAddress(input);
			if (!subnet.equals(WifiHelper.getWifiHelper().getSubnetAddress(
				WifiHelper.getWifiHelper().getDeviceIpAddress(getApplicationContext())))) {								
				error = "IP address of subnet is not the local one!";
			}
		}
		
		/* ip address ends with numeric value in range 0-255 */
		if (error == null) {
			int localAddress = Integer.parseInt(WifiHelper.getWifiHelper().getSubnetIPAddressNode(input));
			if (localAddress > 255) {
				error = "IP address should end in range 0-255!";
			}
		}
		return error;
	}
	
	private class StopCache extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			MainActivity.cacheManager.stopCache();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (InvoicesMainActivity.this.progressDialog != null) {
				InvoicesMainActivity.this.progressDialog.dismiss();
            }
			updateInvoicesActivityUI();
			setMenuItemsVisibilityState(false);
		}
		
	}
	
}
