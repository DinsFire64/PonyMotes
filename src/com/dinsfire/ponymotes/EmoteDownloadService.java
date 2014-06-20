package com.dinsfire.ponymotes;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class EmoteDownloadService extends IntentService {

    final public static String BASE_URL = "http://www.dinsfire.com/emoteCache/";
    final public static String SERVER_DB_URL = "http://www.dinsfire.com/emoteCache/mobileDatabase.db";
    final public static String BASE_LOCAL = FileIO.FULL_EMOTE_PATH;

    public static final int UPDATE_PROGRESS = 8344;
    public static final int ERROR = 1212;

    public EmoteDownloadService() {
	super("EmoteDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	ArrayList<String> listOfEmotes = intent.getStringArrayListExtra("listOfEmotes");

	ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");

	boolean error = false;
	int i = 0;

	while (!error && i < listOfEmotes.size()) {

	    String emoteName = listOfEmotes.get(i);

	    Bundle resultData = new Bundle();

	    resultData.putInt("progress", i + 1);
	    resultData.putInt("size", listOfEmotes.size());
	    resultData.putString("emoteName", emoteName);
	    receiver.send(UPDATE_PROGRESS, resultData);

	    if (Download.from(BASE_URL + emoteName + ".png", BASE_LOCAL + emoteName + ".png")) {
		receiver.send(ERROR, resultData);
		error = true;
	    }

	    i++;
	}

    }

}
