package com.dinsfire.ponymotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;

public class EmotePopup extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	/*
	 * // Figure out what to do based on the intent type if (intent.
	 * getType().indexOf("image/") != -1) { // Handle intents with image
	 * data ... } else if (intent.getType().equals("text/plain")) { //
	 * Handle intents with text ... }
	 */

	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	    this.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	} else {
	    this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	setContentView(R.layout.activity_emote_popup);

	// getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.abs__dialog_full_holo_dark));
	// getWindow().getDecorView().setBackgroundColor(0);

	if (savedInstanceState == null) {

	    SearchFragment search = new SearchFragment();

	    Intent intent = getIntent();
	    Uri data = intent.getData();

	    if (data != null) {
		search.setPickEmoteCode();
		// search.search(data.getLastPathSegment());
	    } else {
		search.setPickPictureMode();
	    }

	    getSupportFragmentManager().beginTransaction().add(R.id.container, search).commit();

	}

    }
}
