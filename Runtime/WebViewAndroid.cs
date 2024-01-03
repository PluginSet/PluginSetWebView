using System;
using PluginSet.Core;
using UnityEngine;
using Logger = PluginSet.Core.Logger;

#if UNITY_ANDROID
namespace PluginSet.WebView
{
    internal static class WebViewAndroid
    {
        private static Logger Logger = LoggerManager.GetLogger("WebViewAndroid");
        
        private static AndroidJavaClass _webviewHelper;

        private static AndroidJavaClass WebViewHelper
        {
            get
            {
                if (_webviewHelper == null)
                    _webviewHelper = new AndroidJavaClass("com.pluginset.webview.WebViewHelper");

                return _webviewHelper;
            }
        }

        internal static void OpenUrl(string url)
        {
            WebViewHelper.CallStatic("openUrl", AndroidHelper.CurrentActivity, url, null);
        }

        internal static void PreloadWebView(string url, Action success, Action fail)
        {
            WebViewHelper.CallStatic("preloadWebView", AndroidHelper.CurrentActivity, url, new PluginSetCallback(
                delegate(string json)
                {
                    Logger.Debug("preload webview success : " + json);
                    if (success != null)
                        MainThread.Run(success);
                }, delegate(int code, string message)
                {
                    Logger.Debug($"preload webview fail: {message}({code})");
                    DestroyPreloadedWebView();
                    if (fail != null)
                        MainThread.Run(fail);
                }), null);
        }

        internal static void ShowPreloadedWebView(Action success, Action fail)
        {
            WebViewHelper.CallStatic("openPreloadWebView", AndroidHelper.CurrentActivity, new PluginSetCallback(
                delegate(string json)
                {
                    Logger.Debug("show preload webview success : " + json);
                    if (success != null)
                        MainThread.Run(success);
                }, delegate(int code, string message)
                {
                    Logger.Debug($"show preload webview fail: {message}({code})");
                    DestroyPreloadedWebView();
                    if (fail != null)
                        MainThread.Run(fail);
                }));
        }

        internal static void DestroyPreloadedWebView()
        {
            WebViewHelper.CallStatic("destroyPreloadWebView", AndroidHelper.CurrentActivity);
        }
    }
}
#endif