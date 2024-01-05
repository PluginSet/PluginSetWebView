using System;
using System.Collections;
using PluginSet.Core;
using UnityEngine;

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

        public static void PreloadWebView(string url, Action success = null, Action fail = null, float timeout = -1)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            WebViewAndroid.PreloadWebView(url, delegate
            {
                StopPreloadTimeout();
                success?.Invoke();
            }, delegate
            {
                StopPreloadTimeout();
                fail?.Invoke();
            });

            if (timeout > 0)
            {
                StartPreloadTimeout(timeout, delegate
                {
                    StopPreloadTimeout();
                    DestroyPreloadedWebView();
                    fail?.Invoke();
                });
            }
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

        private static Coroutine _preloadTimeout;

        private static void StartPreloadTimeout(float timeout, Action fail)
        {
            StopPreloadTimeout();
            _preloadTimeout = CoroutineHelper.Instance.StartCoroutine(PreloadTimeoutInternal(timeout, fail));
        }

        private static void StopPreloadTimeout()
        {
            if (_preloadTimeout != null)
            {
                CoroutineHelper.Instance.StopCoroutine(_preloadTimeout);
                _preloadTimeout = null;
            }
        }
        
        private static IEnumerator PreloadTimeoutInternal(float timeout, Action fail)
        {
            yield return new WaitForSeconds(timeout);
            fail?.Invoke();
        }
    }
}
