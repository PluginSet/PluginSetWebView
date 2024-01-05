package com.pluginset.webview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unity3d.player.R;

import com.pluginset.core.IPluginSetCallback;

public class WebViewHelper {
    private static final int ErrorCode_Repeated = -1;
    private static final int ErrorCode_WebError = -2;
    private static final int ErrorCode_HttpError = -3;
    private static final int ErrorCode_Unload = -4;

    private static IPluginSetCallback _preloadCallback;
    private static WebViewJavaScriptInterface _javaScriptInterface;

    public static void openUrl(final Activity currentActivity, final String url, WebViewJavaScriptInterface javaScriptInterface) {
        _javaScriptInterface = javaScriptInterface;

        currentActivity.runOnUiThread(() -> {
            Intent intent = new Intent(currentActivity, WebViewActivity.class);
            intent.putExtra("url", url);
            currentActivity.startActivity(intent);
        });
    }

    public static void openPreloadWebView(final Activity currentActivity, final IPluginSetCallback callback) {
        if (WebViewPreloadActivity.getWebView() == null) {
            if (callback != null) {
                callback.onFailed(ErrorCode_Unload, "web view unload");
            }
            return;
        }

        currentActivity.runOnUiThread(() -> {
            if (WebViewPreloadActivity.getWebView() == null) {
                if (callback != null) {
                    callback.onFailed(ErrorCode_Unload, "web view unload");
                }
                return;
            }

            Intent intent = new Intent(currentActivity, WebViewPreloadActivity.class);
            currentActivity.startActivity(intent);
            if (callback != null) {
                callback.onSuccess("show web view");
            }
        });

    }

    public static void preloadWebView(final Activity currentActivity, final String url, final IPluginSetCallback callback, WebViewJavaScriptInterface javaScriptInterface) {
//        Log.d("WebViewHelper", "preloadWebView 000000000000000000000000 " + url);
        if (WebViewPreloadActivity.getWebView() != null) {
            if (callback != null) {
                callback.onFailed(ErrorCode_Repeated, "repeated request");
            }
            return;
        }

        _javaScriptInterface = javaScriptInterface;
        currentActivity.runOnUiThread(() -> {
            final WebView webView = new WebView(currentActivity){
                @Override
                protected void onDetachedFromWindow() {
//                    Log.d("WebViewHelper", "onDetachedFromWindow ************************");
                    super.onDetachedFromWindow();
                    if (WebViewPreloadActivity.getWebView() == this)
                    {
                        WebViewPreloadActivity.setWebView(null);
                    }
                }
            };
            WebViewPreloadActivity.setWebView(webView);
            initWebViewSettings(currentActivity, webView, url, callback);
        });
    }

    public static void destroyPreloadWebView(final Activity currentActivity)
    {
        final WebView webView0 = WebViewPreloadActivity.getWebView();
        if (webView0 == null)
            return;

        currentActivity.runOnUiThread(() -> {
            final WebView webView = WebViewPreloadActivity.getWebView();
            if (webView == null)
                return;

            webView.stopLoading();
            webView.clearCache(true);
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }

            webView.destroy();
            WebViewPreloadActivity.setWebView(null);
        });
    }

    public static void initWebViewBar(WebViewBaseActivity activity, View bar, WebView webView)
    {
        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        PackageManager packageManager = activity.getPackageManager();

        Drawable appIcon = applicationInfo.loadIcon(packageManager);
        ImageView icon = bar.findViewById(R.id.web_view_icon);
        icon.setImageDrawable(appIcon);

        CharSequence appName = applicationInfo.loadLabel(packageManager);
        TextView textView = bar.findViewById(R.id.web_view_name);
        textView.setText(appName);

        Button closeBtn = bar.findViewById(R.id.web_view_close);
        closeBtn.setOnClickListener((view) -> {
            activity.closeWebView();
        });
    }

    public static void initWebViewSettings(final Activity currentActivity, WebView webView, String url, IPluginSetCallback callback)
    {
//        Log.d("WebViewHelper", "initWebViewSettings 111111111111111111111111 " + url);
        _preloadCallback = callback;
        WebViewClient client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                Log.d("WebViewHelper", "shouldOverrideUrlLoading >>> ");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    String url = request.getUrl().toString();
//                    Log.d("WebViewHelper", "shouldOverrideUrlLoading >>> " + url);
                    //支持google play
                    if (url.startsWith("market:")
                            || url.startsWith("https://play.google.com/store/")
                            || url.startsWith("http://play.google.com/store/")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        currentActivity.startActivity(intent);
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                Log.d("WebViewHelper", "onPageStarted >>> " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                Log.d("WebViewHelper", "onPageFinished >>> " + url + " progress:: " + view.getProgress());
                super.onPageFinished(view, url);
                if (callback == null || _preloadCallback == null) return;

                if (view.getProgress() < 100) {
                    return;
                }

                IPluginSetCallback callback1 = _preloadCallback;
                _preloadCallback = null;
                callback1.onSuccess("page finished");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                Log.d("WebViewHelper", "onReceivedError >>> " + error.toString());
                super.onReceivedError(view, request, error);
                if (callback == null || _preloadCallback == null) return;
                IPluginSetCallback callback1 = _preloadCallback;
                _preloadCallback = null;
                callback1.onFailed(ErrorCode_WebError, error.toString());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//                Log.d("WebViewHelper", "onReceivedHttpError >>> " + errorResponse.toString());
                super.onReceivedHttpError(view, request, errorResponse);
                if (callback == null || _preloadCallback == null) return;
                IPluginSetCallback callback1 = _preloadCallback;
                _preloadCallback = null;
                callback1.onFailed(ErrorCode_HttpError, errorResponse.toString());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                Log.d("WebViewHelper", "onReceivedSslError >>> " + error.toString());
                super.onReceivedSslError(view, handler, error);
                if (callback == null || _preloadCallback == null) return;
                IPluginSetCallback callback1 = _preloadCallback;
                _preloadCallback = null;
                callback1.onFailed(ErrorCode_HttpError, error.toString());
            }
        };

        DownloadListener downloadListener = (downloadUrl, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(downloadUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            currentActivity.getApplicationContext().startActivity(intent);
        };
        if (_javaScriptInterface != null) {
            webView.addJavascriptInterface(_javaScriptInterface, "WebViewActivityInterface");
        }
        webView.setWebViewClient(client);
        webView.setDownloadListener(downloadListener);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 允许javascript执行
        webSettings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要，开启DOM缓存，开启LocalStorage存储
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.loadUrl(url);
    }

}
