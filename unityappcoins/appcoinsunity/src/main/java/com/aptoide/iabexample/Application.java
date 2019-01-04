package com.aptoide.iabexample;

/**
 * Created by nunomonteiro on 13/08/2018.
 * Aptoide
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.aptoide.iabexample.util.IabHelper;
import com.aptoide.iabexample.util.Purchase;
import com.asf.appcoins.sdk.ads.AppCoinsAds;
import com.asf.appcoins.sdk.ads.AppCoinsAdsBuilder;
import com.unity3d.player.UnityPlayer;
import java.io.*;

public class Application extends android.app.Application {

    static IabHelper iabHelper;
    static Application application;
    static String PACKAGE_NAME = "";
    static String developerAddress = "";
    private static AppCoinsAds adsSdk;

    private Purchase _latestPurchase;
    private boolean useTestNet;
    private String appcoinsPrefabName;
    private boolean hasConnection;


    @Override
    public void onCreate() {
        super.onCreate();
        application=this;

        appcoinsPrefabName = getResources().getString(R.string.APPCOINS_PREFAB);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        //Needs to happen before anything else
        //This will fetch the debug flag value that all other calls depend on

        //Check if there's a connection to a network
        hasConnection = checkIfConnectionActive();
        if(hasConnection){
        //If we're connected to a network check if we have access to the internet
        hasConnection = checkIfThereIsInternetConnection();
        }

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
            Log.d("Unity","Successfully set up the AdsSDK");




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

    /*
     * Checks if there is a Connection active.
     * @returns true if exists a connection to a network and false if not.
     */
    boolean checkIfConnectionActive(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /*
     * Checks if there is connection to the internet by executing the command "ping -c 1 -t 10 google.com"
     * Creates a process and executes a ping to check if there is internet conectivity
     * @returns true if the response of the ping is 200 and returns false if its diferent than 200.
     *
     */
    boolean checkIfThereIsInternetConnection(){

        Process p;

        try {
            p = Runtime.getRuntime().exec("ping -c 1 google.com");
            String lineRead;
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));


            while ((lineRead = stdInput.readLine()) != null) {
                if(lineRead.contains("Unknown")){
                    return false;
                }
            }

        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public boolean hasConnectionInternet(){
        return hasConnection;
    }
}
