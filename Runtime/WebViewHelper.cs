using System;

namespace PluginSet.WebView
{
    public static class WebViewHelper
    {
        public static void OpenUrl(string url)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            WebViewAndroid.OpenUrl(url);
#else
#endif
        }

        public static void PreloadWebView(string url, Action success = null, Action fail = null)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            WebViewAndroid.PreloadWebView(url, success, fail);
#else
            fail?.Invoke();
#endif
            
        }

        public static void ShowPreloadedWebView(Action success = null, Action fail = null)
        {
            
#if UNITY_ANDROID && !UNITY_EDITOR
            WebViewAndroid.ShowPreloadedWebView(success, fail);
#else
            fail?.Invoke();
#endif
        }

        public static void DestroyPreloadedWebView()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            WebViewAndroid.DestroyPreloadedWebView();
#else
#endif
        }
    }
}
