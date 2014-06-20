package com.dinsfire.ponymotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebActivity extends Activity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_web);

	Bundle bundle = getIntent().getExtras();
	String url = bundle.getString("url");
	String title = bundle.getString("title");

	setTitle(title);

	webView = (WebView) findViewById(R.id.webview);

	WebSettings webSettings = webView.getSettings();
	webSettings.setJavaScriptEnabled(true);

	webView.loadUrl(url);
    }
}
