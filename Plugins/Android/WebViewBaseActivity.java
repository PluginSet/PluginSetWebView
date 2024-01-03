package com.pluginset.webview;

import android.app.Activity;

public class WebViewBaseActivity extends Activity {
    public void closeWebView()
    {
        super.onBackPressed();
    }
}
