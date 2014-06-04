package com.dinsfire.ponymotes;

import android.content.SharedPreferences;

public class Prefrences {
	
	private SharedPreferences prefs;
	
	public Prefrences(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	public boolean includeNSFW() {
		return prefs.getBoolean("prefIncludeNSFW", false);
	}
	
	public boolean wantToDownloadEmotes() {
		return prefs.getBoolean("prefDownloadEmotes", true);
	}
	
	public boolean wantToDownloadMissingEmotes() {
		return prefs.getBoolean("prefDownloadMissingEmotes", true);
	}
	
	public boolean disableOnMobile() {
		return prefs.getBoolean("prefDisableMobile", false);
	}
	
	public boolean altSharingMethod() {
		return prefs.getBoolean("prefAltSharingMethod", false);
	}
	
	public boolean onboardingSession() {
		return prefs.getBoolean("prefShowOnboardingSession", true);
	}
	
	public void turnOnboardingSessionOff() {
		prefs.edit().putBoolean("prefShowOnboardingSession", false).commit();
	}
	
	public String searchLimit() {
		return prefs.getString("prefSearchResultLimit", "200");
	}
	
	public int numOfColumns() {
		try {
			return Integer.valueOf(prefs.getString("prefNumOfColumns", "3"));
		} catch(Exception e) {
			return 3;
		}
	}

}
