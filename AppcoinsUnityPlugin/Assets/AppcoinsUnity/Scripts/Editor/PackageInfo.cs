using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PackageInfo : MonoBehaviour {

    public static string GetPackageName()
    {
        return "BDS_AppCoins_Unity_Package_2018";
    }

    public static bool ShouldCopyToMainRepo() {
        return false;
    }
}
