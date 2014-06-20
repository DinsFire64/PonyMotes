package com.dinsfire.ponymotes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Download {

    public static final int TIMEOUT = 5000;

    public static boolean from(String url, String local) {

	boolean error = false;

	try {
	    Log.i("downloading", url);

	    URL u = new URL(url);
	    HttpURLConnection c = (HttpURLConnection) u.openConnection();
	    // c.setConnectTimeout(TIMEOUT);
	    // c.setReadTimeout(TIMEOUT);
	    // c.setRequestMethod("GET");
	    // c.setDoOutput(true);
	    c.connect();

	    FileOutputStream f = new FileOutputStream(local);

	    InputStream in = c.getInputStream();

	    byte[] buffer = new byte[1024];
	    int len1 = 0;
	    while ((len1 = in.read(buffer)) > 0) {
		f.write(buffer, 0, len1);
	    }

	    f.close();

	} catch (FileNotFoundException e) {
	    if (e.getMessage() != null)
		Log.d("downloaderFileNotFoundException", e.getMessage());
	} catch (IOException e) {
	    if (e.getMessage() != null)
		Log.d("downloaderIOException", e.getMessage());
	    error = true;
	} catch (Exception e) {
	    if (e.getMessage() != null)
		Log.d("downoaderOtherException", e.getMessage());

	    error = true;
	}

	return error;
    }

    public static final boolean isConnectedToMobile(Context context) {
	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	return activeNetwork != null && activeNetwork.isConnected();
    }

    public static void setUserAgent(PackageManager p) {
	PackageInfo pInfo;
	try {
	    pInfo = p.getPackageInfo("com.dinsfire.ponymotes", 0);
	    System.setProperty("http.agent", pInfo.packageName + " " + pInfo.versionName);
	} catch (NameNotFoundException e) {
	    Log.e("NameNotFoundException", e.toString());
	    System.setProperty("http.agent", "?????");
	}
    }

}
