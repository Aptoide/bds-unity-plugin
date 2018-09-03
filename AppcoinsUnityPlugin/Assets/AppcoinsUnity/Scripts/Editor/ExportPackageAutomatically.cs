using UnityEditor;
using UnityEngine;
using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;


public class ExportPackageAutomatically : ScriptableObject
{

    [MenuItem("Export Package/Unity")]
    public static void ExportFromUnity()
    {
        // Just the file name with extension
        List<string> filesToRemove = new List<string> {
            "ExportPackageAutomatically.cs",
            "PackageInfo.cs",
            "ProcessCompleted.out",
            "BashCommand.sh",
            "ProcessError.out",
            "AttemptMergeTemplate.gradle",
            "MergedTemplate.gradle",
            "oldMainTemplate.gradle",
            "Assets/Plugins/Android/mainTemplate.gradle"
        };

        // Complete path to folder to filter out (starting from Assets)
        List<string> foldersToRemove = new List<string> {
            "Assets/Products"
            ,"Assets/AppcoinsUnity/Scripts/Editor/Tests"
            ,"Assets/StompyRobot"
            ,"Assets/Scenes"
        };

        int pathToRemove = (Path.GetDirectoryName(Application.dataPath) + "/").Length;

        string[] allFiles = Directory.GetFiles(Application.dataPath + "/", "*.*", SearchOption.AllDirectories);
        List<string> filesToExport = new List<string>(allFiles);

        //Filter unwanted folders
        for (int i = filesToExport.Count - 1; i >= 0; i--)
        {
            string itemName = filesToExport[i].Substring(pathToRemove);
            filesToExport[i] = itemName;

            //Debug.Log("Checking item " + itemName);

            foreach(string folder in foldersToRemove) {
                if (itemName.Contains(folder)) {
                    RemoveItemFromList(filesToExport, i);
                    break;
                }
            }           
        }

        //Filter unwanted files
        for(int i = filesToExport.Count - 1; i >= 0; i--)
        {
            //Debug.Log("Checking item " + filesToExport[i]);

            //Delete files starting with .
            if(Path.GetFileName(filesToExport[i]).Substring(0, 1) == ".")
            {
                RemoveItemFromList(filesToExport, i);
            }

            //Delete metas
            else if(Path.GetExtension(filesToExport[i]) == ".meta")
            {
                RemoveItemFromList(filesToExport, i);
            }

            //Delete blacklisted files
            else 
            {
                foreach(string blackFile in filesToRemove)
                {
                    if(filesToExport[i].Contains(blackFile))
                    {
                        RemoveItemFromList(filesToExport,i);
                        break;
                    }
                }
            }
        }

        string packagePath = Application.dataPath +
                                        "/../../" + 
                                        PackageInfo.GetPackageName() + ".unitypackage";


        ExportPackageOptions options = ExportPackageOptions.Recurse;
        AssetDatabase.ExportPackage(filesToExport.ToArray(), packagePath, options);

        UnityEngine.Debug.Log("Export done successfully");
    }

    private static void RemoveItemFromList(List<string> list, int itemIndex) {
        //UnityEngine.Debug.Log("Deleting " + list[itemIndex]);
        list.RemoveAt(itemIndex);
    }
}