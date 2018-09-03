package com.aptoide.appcoinsunity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.aptoide.appcoinsunity.util.IabHelper;
import com.aptoide.appcoinsunity.util.IabResult;
import com.aptoide.appcoinsunity.util.PayloadHelper;
import com.aptoide.appcoinsunity.util.Purchase;
import com.unity3d.player.UnityPlayer;

/**
 * Created by Aptoide on 6/28/2018.
 */
public class PurchaseActivity extends Activity {

    public static PurchaseActivity activity;

    String appcoinsPrefabName;

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener =
            new IabHelper.OnIabPurchaseFinishedListener() {
                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

                    Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

                    // if we were disposed of in the meantime, quit.
                    if (Application.iabHelper == null) return;

                    if (result.isFailure()) {
                        complain("Error purchasing: " + result);
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseFailure",result.getMessage());
                        finish();
                        return;
                    }
                    if (!verifyDeveloperPayload(purchase)) {
                        complain("Error purchasing. Authenticity verification failed.");
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseFailure",result.getMessage());
                        finish();
                        return;
                    }

                    Application.application.setLatestPurchase(purchase);

                    Log.d(TAG, "Purchase successful. Notifying game and waiting consume strategy");
                    UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnProcessPurchase",purchase.getSku());
                }
            };

    public static String SKUID_TAG = "skuid";
    public static String PAYLOAD_TAG = "payload";
    static final int RC_REQUEST = 10001;

    private static String TAG = "PurchaseActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        appcoinsPrefabName = getResources().getString(R.string.APPCOINS_PREFAB);

        // print debug message to logcat
        Log.d(TAG, "Activity began.");
        String skuid = getIntent().getStringExtra(SKUID_TAG);
        String devPayload = getIntent().getStringExtra(PAYLOAD_TAG);
        Log.d(TAG,"the activity is " + this);

        String payload =
                PayloadHelper.buildIntentPayload(Application.application.getDeveloperAddress(),
                        (devPayload.equals("") ? null : devPayload));
        try {
            Application.iabHelper.launchPurchaseFlow(this, skuid, RC_REQUEST, mPurchaseFinishedListener,
                    payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            String errorMsg = "Error launching purchase flow. Another async operation in progress.";
            complain(errorMsg);
            UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseFailure",errorMsg);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG,"onActivityResult with reqCode " + requestCode + " resultCode " + resultCode + " and data " + data);

        // Pass on the activity result to the helper for handling
        if (!Application.iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }

        setResult(resultCode,data);
        finish();
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    void complain(String message) {
        Log.e(TAG, "**** Unity BDS error: " + message);
    }
}