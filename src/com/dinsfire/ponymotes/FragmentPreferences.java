package com.dinsfire.ponymotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class FragmentPreferences extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// Display the fragment as the main content.
	getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    @SuppressLint("NewApi")
    public static class PrefsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Load the preferences from an XML resource
	    addPreferencesFromResource(R.xml.settings);

	}

	/*
	 * implements OnSharedPreferenceChangeListener {
	 * 
	 * @Override public void onResume() { super.onResume();
	 * getPreferenceManager
	 * ().getSharedPreferences().registerOnSharedPreferenceChangeListener
	 * (this);
	 * 
	 * }
	 * 
	 * @Override public void onPause() {
	 * getPreferenceManager().getSharedPreferences
	 * ().unregisterOnSharedPreferenceChangeListener(this); super.onPause();
	 * }
	 * 
	 * public void onSharedPreferenceChanged(SharedPreferences
	 * sharedPreferences, String key) {
	 * 
	 * if(key.equals("prefDirectory")) { Preference pref =
	 * findPreference(key); //EditTextPreference editPref =
	 * (EditTextPreference) pref;
	 * pref.setSummary(sharedPreferences.getString(key, ""));
	 * 
	 * }
	 * 
	 * }
	 */

    }

}