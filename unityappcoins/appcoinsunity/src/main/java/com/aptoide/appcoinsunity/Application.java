package com.aptoide.appcoinsunity;

/**
 * Created by nunomonteiro on 13/08/2018.
 * Aptoide
 */

import android.content.pm.PackageManager;
import android.util.Log;

import com.aptoide.appcoinsunity.util.IabHelper;
import com.aptoide.appcoinsunity.util.Purchase;
import com.asf.appcoins.sdk.ads.AppCoinsAds;
import com.asf.appcoins.sdk.ads.AppCoinsAdsBuilder;
import com.unity3d.player.UnityPlayer;

import java.util.LinkedList;
import java.util.List;

public class Application extends android.app.Application {

    static IabHelper iabHelper;
    static Application application;
    static String developerAddress = "";
    private static AppCoinsAds adsSdk;

    private Purchase _latestPurchase;
    private boolean useTestNet;
    private String appcoinsPrefabName;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;

        appcoinsPrefabName = getResources().getString(R.string.APPCOINS_PREFAB);

        //Needs to happen before anything else
        //This will fetch the debug flag value that all other calls depend on
        setupStoreEnvironment();

        setupAdsSDK();
        Log.d("AppcoinsUnityPlugin", "Aplication began.");
    }

    public void setupStoreEnvironment() {
        final boolean debugValue = getResources().getBoolean(R.bool.APPCOINS_ENABLE_DEBUG);
        Log.d("AppcoinsUnityPlugin", "Debug should be " + debugValue);

        useTestNet = debugValue;
    }

    public void setupAdsSDK() {
        final boolean poaValue = getResources().getBoolean(R.bool.APPCOINS_ENABLE_POA);
        Log.d("AppcoinsUnityPlugin", "POA should be " + poaValue);


        if(poaValue == true) {

            Log.d("AppcoinsUnityPlugin", "POA sdk initialized");
            adsSdk = new AppCoinsAdsBuilder().withDebug(useTestNet)
                    .createAdvertisementSdk(application);
            try {
                adsSdk.init(Application.application);
            } catch (PackageManager.NameNotFoundException e) {
                UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFail","Failed to initialize AppcoinsAds SDK " + e.toString());
                e.printStackTrace();
            }
            Log.d("UnityAppCoins","Successfully set up the AdsSDK");




        }

    }



    public static void setDeveloperAddress(String theDeveloperAddress) {
        developerAddress = theDeveloperAddress;
    }

    public String getDeveloperAddress() {
        return developerAddress;
    }

    public void setLatestPurchase(Purchase p) {
        _latestPurchase = p;
    }

    public Purchase getLatestPurchase() {
         return _latestPurchase;
    }
}
