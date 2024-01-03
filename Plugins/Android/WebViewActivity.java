package com.pluginset.webview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class WebViewActivity extends WebViewBaseActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.web_view_content);
        View bar = findViewById(R.id.web_view_bar);
        WebViewHelper.initWebViewBar(this, bar, webView);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        WebViewHelper.initWebViewSettings(this, webView, url, null);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
