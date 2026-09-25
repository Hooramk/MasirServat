#if UNITY_EDITOR
using System;
using UnityEditor;
using UnityEditor.Build.Reporting;

namespace MasirServat.Editor
{
    public static class AndroidBuild
    {
        public static void Build()
        {
            PlayerSettings.productName = "Masir Servat";
            PlayerSettings.companyName = "Hooram Gostar Maham";
            PlayerSettings.bundleVersion = "1.0.0-alpha";
            PlayerSettings.Android.bundleVersionCode = 1;
            PlayerSettings.defaultInterfaceOrientation = UIOrientation.LandscapeLeft;
            PlayerSettings.SetApplicationIdentifier(BuildTargetGroup.Android, "ir.masirservat.game");

            EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTargetGroup.Android, BuildTarget.Android);
            EditorUserBuildSettings.androidBuildSystem = AndroidBuildSystem.Gradle;

            var options = new BuildPlayerOptions
            {
                scenes = new[] { "Assets/Scenes/Main.unity" },
                locationPathName = "Builds/Android/MasirServat-Unity.apk",
                target = BuildTarget.Android,
                options = BuildOptions.None
            };

            BuildReport report = BuildPipeline.BuildPlayer(options);
            if (report.summary.result != BuildResult.Succeeded)
                throw new Exception("Unity Android build failed: " + report.summary.result);

            UnityEngine.Debug.Log(
                "Unity Android build succeeded: " +
                report.summary.totalSize + " bytes, " +
                report.summary.totalTime
            );
        }
    }
}
#endif
