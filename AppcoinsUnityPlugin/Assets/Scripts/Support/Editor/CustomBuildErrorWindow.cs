using UnityEditor;
using UnityEngine;

public class CustomBuildErrorWindow : EditorWindow
{
    public static CustomBuildWindow instance;
    public Vector2 scrollViewVector = Vector2.zero;
    private static bool[] _errors = null;
    private static string[] _errorsTitles = {
        "Export Unity Project: ",
        "(Gradle) Build Exported Project: ",
        "(ADB) Install .apk to device: ",
        "(ADB) Run .apk in the device: "
    };

    //Create the custom Editor Window
    public static void CreateCustomBuildErrorWindow(bool[] errors)
    {
        CustomBuildWindow.instance = (CustomBuildWindow)EditorWindow.GetWindowWithRect(
            typeof(CustomBuildWindow),
            new Rect(0, 0, 600, 500),
            true,
            "Custom Build Errors"
        );

        instance.minSize = new Vector2(600, 500);
        instance.autoRepaintOnSceneChange = true;
        instance.Show();

        _errors = errors;
    }

    public void OnInspectorUpdate()
    {
        // This will only get called 10 times per second.
        Repaint();
    }

    void OnGUI()
    {
        int height = 10;
        int i = 0;

        while (_errors[i] == true && i < _errors.Length)
        {
            GUI.Label(new Rect(5, height, 590, 20), _errorsTitles[i]);
            height += 10;
        }

        if (CustomBuild.gradlePath != "" && GUI.Button(new Rect(530, 470, 60, 20), "Confirm"))
        {
            this.Close();
        }
    } 
}