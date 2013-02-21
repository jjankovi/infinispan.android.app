package invoices.manager.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class ConfigureActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static final String KEY_CACHE_MODE = "mode";
//	private static final String KEY_CACHE_STORE = "store";
//	
	private ListPreference cacheMode;
//	private CheckBoxPreference cacheStore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);	
		
		SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
	    sharedPref.registerOnSharedPreferenceChangeListener(this); 
	    
		cacheMode = (ListPreference)getPreferenceScreen().findPreference(KEY_CACHE_MODE);
		cacheMode.setSummary(cacheMode.getEntry());
//		cacheStore = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_CACHE_STORE);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference pref = findPreference(key);

	    if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	    }
	}
	
	
	
}
