using PluginSet.Core.Editor;
using UnityEditor;

namespace PluginSet.Webview.Editor
{
    [BuildTools]
    public static class BuildWebViewTools
    {
        // [AndroidProjectModify]
        // public static void OnAndroidProjectModify(BuildProcessorContext context, AndroidProjectManager projectManager)
        // {
        //     var buildParams = context.BuildChannels.Get<BuildWebViewParams>();
        //     if (!buildParams.Enable)
        //         return;
        //
        //     var doc = projectManager.LauncherManifest;
        // }
    }
}