using PluginSet.Core.Editor;
using UnityEngine;

namespace PluginSet.Webview.Editor
{
    [BuildChannelsParams("Webview", "Webview设置")]
    public class BuildWebViewParams : ScriptableObject
    {
        [Tooltip("是否使用Webview")]
        public bool Enable;

        [Tooltip("使用默认UI")]
        public bool UseDefaultUI = true;
    }
}
