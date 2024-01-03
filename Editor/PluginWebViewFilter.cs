using PluginSet.Core.Editor;
using UnityEditor;
using UnityEngine;

namespace PluginSet.Webview.Editor
{
    [InitializeOnLoad]
    public class PluginWebViewFilter : MonoBehaviour
    {
        static PluginWebViewFilter()
        {
            var filter = PluginFilter.IsBuildParamsEnable<BuildWebViewParams>();
            PluginFilter.RegisterFilter("com.pluginset.webview/Plugins/iOS", filter);
            PluginFilter.RegisterFilter("com.pluginset.webview/Plugins/Android", FilterAndroid);
        }

        private static bool FilterAndroid(string path, BuildProcessorContext context)
        {
            var buildParams = context.BuildChannels.Get<BuildWebViewParams>();
            if (!buildParams.Enable)
                return true;

            if (path.Contains("webview_default_ui"))
                return !buildParams.UseDefaultUI;

            return false;
        }
    }
        
}
