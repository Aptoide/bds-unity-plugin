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
    static Application application;
    static String developerAddress = "";

    private Purchase _latestPurchase;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;

        Log.d("AppcoinsUnityPlugin", "Aplication began.");
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
