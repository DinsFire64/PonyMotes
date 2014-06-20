package com.dinsfire.ponymotes;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class RegularPreferences extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.settings);
    }
}
