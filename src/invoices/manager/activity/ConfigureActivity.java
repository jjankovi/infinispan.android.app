package invoices.manager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;

/**
 * ConfigureActivity is used for configuring the application
 * 
 * @author jjankovi
 *
 */
public class ConfigureActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static final String KEY_CACHE_MODE = "mode";
	private static final String KEY_L1_CACHE = "l1";
	private static final String KEY_OWNERS_NUMBER = "owners";
	private static final String KEY_CACHE_STORE = "store";

	private ListPreference cacheMode;
	private CheckBoxPreference l1Cache;
	private EditTextPreference owners;
	private CheckBoxPreference cacheStore;
	
	private String cacheModeOld;
	private boolean cacheStoreOld;
	private String numOwnersOld;
	private boolean l1CacheOld;
	
	private String cacheModeNew;
	private boolean cacheStoreNew;
	private String numOwnersNew;
	private boolean l1CacheNew;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);	
		
		SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
	    sharedPref.registerOnSharedPreferenceChangeListener(this); 
	    
	    if (sharedPref.getAll().size() == 0) {
    		cacheModeOld = "local";
    		cacheStoreOld = false;
    		numOwnersOld = "1";
    		l1CacheOld = false;
    	} else {
    		cacheModeOld = sharedPref.getString("mode", "local");
    		cacheStoreOld = sharedPref.getBoolean("store", false);
    		numOwnersOld = sharedPref.getString("owners", "1");
    		l1CacheOld = sharedPref.getBoolean("l1", false);
    	}
    	
	    
		cacheMode = (ListPreference)getPreferenceScreen().findPreference(KEY_CACHE_MODE);
		cacheMode.setSummary(cacheMode.getEntry());
		
		l1Cache = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_L1_CACHE);
		
		owners = (EditTextPreference)getPreferenceScreen().findPreference(KEY_OWNERS_NUMBER);
		owners.getEditText().setKeyListener(DigitsKeyListener.getInstance(false, true));
		owners.setSummary(owners.getText());
		
		setDistributedPreferences();
		cacheStore = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_CACHE_STORE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
		boolean newSettings = false;
		
		cacheModeNew = sharedPref.getString("mode", "local");
		if (!cacheModeNew.equals(cacheModeOld)) newSettings = true;
		
		cacheStoreNew = sharedPref.getBoolean("store", false);
		if (cacheStoreNew != cacheStoreOld) newSettings = true;
		
		numOwnersNew = sharedPref.getString("owners", "1");
		if (!numOwnersNew.equals(numOwnersOld)) newSettings = true;
		
		l1CacheNew = sharedPref.getBoolean("l1", false);
		if (l1CacheNew != l1CacheOld) newSettings = true;

		if (newSettings && MainActivity.cacheManager.isCacheStarted()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Warning").setMessage("Settings has been changed while Invoices " +
					"container is running. Press OK to apply settings and stop the container. " +
					"Press Cancel to reset settings.");
			dialog.setPositiveButton(R.string.ok, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.cacheManager.stopCache();
					finish();
				}
			});
			dialog.setNegativeButton(R.string.cancel, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					cacheStore.setChecked(cacheStoreOld);
					l1Cache.setChecked(l1CacheOld);
					cacheMode.setValue(cacheModeOld);
					owners.setText(numOwnersOld);
				}
			});
			dialog.create().show();
		} else {
			finish();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference pref = findPreference(key);

		if (key.equals(KEY_CACHE_MODE)) {
			ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	        setDistributedPreferences();
		} else if (key.equals(KEY_OWNERS_NUMBER)) {
			EditTextPreference editPref = (EditTextPreference) pref;
	        pref.setSummary(editPref.getText());
	    }
	}
	
	private void setDistributedPreferences() {
		
		if (!cacheMode.getSummary().equals("Distributed")) {
			l1Cache.setChecked(false);
			owners.setText("1");
		}
		
		l1Cache.setEnabled(cacheMode.getSummary().equals("Distributed"));
		owners.setEnabled(cacheMode.getSummary().equals("Distributed"));
	}
	
}
