package com.pluginset.webview;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class WebViewPreloadActivity extends WebViewBaseActivity {

    private static WebView webView;

    public Button closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_preload_view);
        super.onCreate(savedInstanceState);
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(webView);
            }
        } else {
            finish();
            return;
        }

        LinearLayout.LayoutParams webViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(webViewLP);
        LinearLayout root = findViewById(R.id.web_view_preload_root);
        root.addView(webView);

        View bar = findViewById(R.id.web_view_bar);
        WebViewHelper.initWebViewBar(this, bar, webView);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        WebViewHelper.destroyPreloadWebView(this);
        super.finish();
    }

    public static void setWebView(WebView webView) {
        WebViewPreloadActivity.webView = webView;
    }

    public static WebView getWebView() {
        return webView;
    }
}
