package com.aptoide.appcoinsunity;

/**
 * Created by nunomonteiro on 13/08/2018.
 * Aptoide
 */

import android.util.Log;

import com.aptoide.appcoinsunity.util.IabHelper;
import com.aptoide.appcoinsunity.util.Purchase;

import java.util.LinkedList;
import java.util.List;

public class Application extends android.app.Application {

    static IabHelper iabHelper;
    static boolean debugFlag=false;
    static boolean POAFlag=false;
    static boolean IABFlag=false;
    static Application application;

    private Purchase _latestPurchase;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;

        //Needs to happen before anything else
        //This will fetch the debug flag value that all other calls depend on
        setupStoreEnvironment();

        Log.d("AppcoinsUnityPlugin", "Aplication began.");
    }

    private final String developerAddress = "0xd133fab7abc4711fd328c4aefbf04256a79b1b40";

    public String getDeveloperAddress() {
        return developerAddress;
    }

    public void setupStoreEnvironment() {
        final boolean debugValue = true; //getResources().getBoolean(R.bool.APPCOINS_ENABLE_DEBUG);
        Log.d("AppcoinsUnityPlugin", "Debug should be " + debugValue);

        debugFlag = debugValue;
    }

    public void setLatestPurchase(Purchase p) {
        _latestPurchase = p;
    }

    public Purchase getLatestPurchase() {
         return _latestPurchase;
    }
}
