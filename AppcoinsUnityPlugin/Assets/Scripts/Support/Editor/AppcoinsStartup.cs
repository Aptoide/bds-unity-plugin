using UnityEngine;
using UnityEditor;
using System;
using System.IO;

[InitializeOnLoad]
public class Startup
{
    private static string appcoinsMainTemplate = UnityEngine.Application.dataPath + "/AppcoinsUnity/Plugins/Android/mainTemplate.gradle";
    private static string currentMainTemplate = UnityEngine.Application.dataPath + "/Plugins/Android/mainTemplate.gradle";
    private static string oldMainTemplate =  UnityEngine.Application.dataPath + "/Plugins/Android/oldMainTemplate.gradle";

    public const string DEFAULT_UNITY_PACKAGE_IDENTIFIER = "com.Company.ProductName";

    static Startup()
    {
        CheckMinSdkVersion();
        CheckMainTemplateGradle();
        Debug.Log("Successfully integrated Appcoins Unity plugin!");
    }

    private static void CheckMinSdkVersion()
    {
        //Check if min sdk version is lower than 21. If it is, set it to 21
        if (PlayerSettings.Android.minSdkVersion < AndroidSdkVersions.AndroidApiLevel21)
            PlayerSettings.Android.minSdkVersion = AndroidSdkVersions.AndroidApiLevel21;
    }

    private static void CheckMainTemplateGradle()
    {
        if(File.Exists(currentMainTemplate))
        {
            if(!Startup.AppcoinsMainTemplateAlreadyMerged())
            {
                File.Copy(currentMainTemplate, oldMainTemplate, true);

                Tree<string> tCurrent = Tree<string>.CreateTreeFromFile(currentMainTemplate, FileParser.BUILD_GRADLE);
                Tree<string> tAppcoins = Tree<string>.CreateTreeFromFile(appcoinsMainTemplate, FileParser.BUILD_GRADLE);

                tCurrent.MergeTrees(tAppcoins);
                Tree<string>.CreateFileFromTree(tCurrent, UnityEngine.Application.dataPath + "/Plugins/Android/mainTemplate.gradle" , false, FileParser.BUILD_GRADLE);
            }
        }

        else
        {
            File.Copy(appcoinsMainTemplate, currentMainTemplate, true);
        }
    }

    private static bool AppcoinsMainTemplateAlreadyMerged()
    {
        StreamReader fileReader = new StreamReader(currentMainTemplate);

        string appcoinsComment = "// Appcoins mainTemplate merged";
        string line;
        bool commentExists = false;

        while((line = fileReader.ReadLine()) != null)
        {
            if(line.Equals(appcoinsComment))
            {
                commentExists = true;
                break;
            }
        }

        fileReader.Close();
        return commentExists;
    }
}