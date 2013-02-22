package invoices.manager.activity;

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
 * 
 * @author jjankovi
 *
 */
public class ConfigureActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static final String KEY_CACHE_MODE = "mode";
	private static final String KEY_L1_CACHE = "l1";
	private static final String KEY_OWNERS_NUMBER = "owners";
//	private static final String KEY_CACHE_STORE = "store";
//	
	private ListPreference cacheMode;
	private CheckBoxPreference l1Cache;
	private EditTextPreference owners;
//	private CheckBoxPreference cacheStore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);	
		
		SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
	    sharedPref.registerOnSharedPreferenceChangeListener(this); 
	    
		cacheMode = (ListPreference)getPreferenceScreen().findPreference(KEY_CACHE_MODE);
		cacheMode.setSummary(cacheMode.getEntry());
		
		l1Cache = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_L1_CACHE);
		
		owners = (EditTextPreference)getPreferenceScreen().findPreference(KEY_OWNERS_NUMBER);
		owners.getEditText().setKeyListener(DigitsKeyListener.getInstance(false, true));
		owners.setSummary(owners.getText());
		
		setDistributedPreferences();
//		cacheStore = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_CACHE_STORE);
	}

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
		
		l1Cache.setEnabled(cacheMode.getSummary().equals("Distributed"));
		owners.setEnabled(cacheMode.getSummary().equals("Distributed"));
	}
	
}
