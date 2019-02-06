package com.aptoide.iabexample;

import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.aptoide.iabexample.util.IabHelper;
import com.aptoide.iabexample.util.IabResult;
import com.aptoide.iabexample.util.Inventory;
import com.aptoide.iabexample.util.Purchase;
import com.aptoide.iabexample.util.WalletUtils;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;

import java.util.List;

/**
 * Created by nunomonteiro on 13/08/2018.
 * Modified by Aptoide
 */

public class UnityAppcoins  {

    String appcoinsPrefabName = UnityPlayer.currentActivity.getResources().getString(R.string.APPCOINS_PREFAB);
    static final int RC_REQUEST = 10001;

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener =
            new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                    mInventory = inventory;
                    Log.d(TAG, "Query inventory finished.");

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // Is it a failure?
                    if (result.isFailure()) {
                        String error = "Failed to query inventory: " + result;
                        complain(error);

                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFail",error);
                        return;
                    }

                    /*
                     * Check for items we own. Notice that for each purchase, we check
                     * the developer payload to see if it's correct! See
                     * verifyDeveloperPayload().
                     */

                    _ownedSkus =  inventory.getAllOwnedSkus();
                    Log.d(TAG, "Checking " + _ownedSkus.size() + " owned items.");

                    _currentInventoryValidationTests = 0;
                    _totalInventoryValidationTests = _ownedSkus.size();

                    if (_currentInventoryValidationTests == _totalInventoryValidationTests) {
                        Log.d(TAG, "Initial inventory query finished.");
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeSuccess","");
                    } else {
                        Purchase purchase = inventory.getPurchase(_ownedSkus.get(_currentInventoryValidationTests));
                        verifyDeveloperPayload(purchase,mValidationFinishedListener);
                    }
                }
            };

    // Called when inventory item validation is complete
    IabHelper.OnPayloadValidationFinishedListener mValidationFinishedListener =
            new IabHelper.OnPayloadValidationFinishedListener() {
                public void onValidationFinished(boolean success, Purchase purchase) {
                    _currentInventoryValidationTests++;
                    Log.d(TAG, "Validation finished with result: " + (success ? "PASSED" : "FAILED") );

                    if (!success) {
                        String errorMsg = "Existing sku that did NOT pass payload validation: " + purchase.getSku();
                        Log.d(TAG, errorMsg);
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFailed",errorMsg);
                    } else {
                        Log.d(TAG, "Notifying existence of sku " + purchase.getSku() + " to process");

                        Application.application.setLatestPurchase(purchase);
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnProcessPurchase",purchase.getSku());

                        //If all products were validated successfully notify initialization success
                        //else continue validation process
                        if (_currentInventoryValidationTests == _totalInventoryValidationTests) {
                            Log.d(TAG, "Initial inventory query finished.");
                            UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeSuccess","");
                        } else {
                            Purchase newPurchase = mInventory.getPurchase(_ownedSkus.get(_currentInventoryValidationTests));
                            verifyDeveloperPayload(newPurchase ,mValidationFinishedListener);
                        }
                    }

                }
            };


    // Called when setup is complete
    IabHelper.OnIabSetupFinishedListener mSetupFinishedListener =
            new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        //Oh noes, there was a problem.

                        //Verifys if the user is connected to a network.If not returns the error NetworkNotAvailable
                        if(!Application.application.hasConnectionInternet())
                        {
                            String error = "No Network Available.";
                            UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFail",error);
                            return;
                        }
                        String error = "Problem setting up in-app billing: ";
                        complain(error + result);

                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFail",error + result);

                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // Important: Dynamically register for broadcast messages about updated purchases.
                    // We register the receiver here instead of as a <receiver> in the Manifest
                    // because we always call getPurchases() at startup, so therefore we can ignore
                    // any broadcasts sent while the app isn't running.
                    // Note: registering this listener in an Activity is a bad idea, but is done here
                    // because this is a SAMPLE. Regardless, the receiver must be registered after
                    // IabHelper is setup, but before first call to getPurchases().
                    // Verify the action for the broadcast receiver is empty meaning the feature is not
                    // supported and there is no reason to create a listener.
                    //if (!IabBroadcastReceiver.ACTION.isEmpty()) {
                    //  mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                    //  IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    //  registerReceiver(mBroadcastReceiver, broadcastFilter);
                    //}
                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        String error = "Error querying inventory. Another async operation in progress.";
                        complain(error);
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFail",error);
                    } finally {

                    }
                }

            };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

                    // if we were disposed of in the meantime, quit.
                    if (mHelper == null) return;

                    if (result.isSuccess()) {
                        // successfully consumed, so we apply the effects of the item in our
                        // game world's logic, which in our case means filling the gas tank a bit
                        Log.d(TAG, "Consumption successful. Provisioning.");
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseSuccess",purchase.getSku());
                    } else {
                        String error = "Error while consuming: " + result;
                        complain(error);
                        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseFailure",error);
                    }

                    Log.d(TAG, "End consumption flow.");
                }
            };

    public static final String TAG = "UnityAppcoinsBDS";
    // (arbitrary) request code for the purchase flow
    private static int REQUEST_CODE = 1337;
    private static  String developerBDSPublicKey = "CONSTRUCT_YOUR_BDS_KEY_AND_PLACE_IT_HERE";
    private static  boolean _useMainNet = true;
    private static  boolean _useAds = false;
    private static  boolean shouldLog = false;
    public static UnityAppcoins instance;
    private IabHelper mHelper;
    private Inventory mInventory;
    private boolean _isPayloadValid = true;
    private Purchase _toBeValidatedPurchase;
    //The number of validation tests to be done before ending inventory query
    private int _totalInventoryValidationTests;
    //The current number of validation tests already done for inventory quert
    private int _currentInventoryValidationTests;
    private List<String> _ownedSkus;


    public static void start()
    {
        instance = new UnityAppcoins();
        instance.CreateIABHelper();
    }

    //DEPRECATED. Now this value comes from Application
//    public static void setDeveloperAddress(String address) {
//        Application.setDeveloperAddress(address);
//    }

    //DEPRECATED. Now this value comes from Application
//    public static void setDeveloperBDSPublicKey(String key) {
//        developerBDSPublicKey = key;
//    }

    public static void setLogging(boolean val) {
        shouldLog = val;
    }

    public static void setUseMainNet(boolean val) {
        Log.d("Unity","Setting use main net: " + val);
        _useMainNet = val;
    }

    public static void setUseAdsSDK(boolean val) {
        Log.d("Unity","Setting use ads: " + val);
        _useAds = val;
    }

    public static boolean hasWalletInstalled() {
        boolean hasWallet = WalletUtils.hasWalletInstalled(UnityPlayer.currentActivity);
        return hasWallet;
    }

    public static boolean hasSpecificWalletInstalled() {
        boolean hasWallet = WalletUtils.hasSpecificWalletInstalled(UnityPlayer.currentActivity, "com.appcoins.wallet");
        return hasWallet;
    }

    public static void promptWalletInstall() {
        Log.d("Unity","Prompting to install wallet");
        WalletUtils.showWalletInstallDialog(UnityPlayer.currentActivity,
                UnityPlayer.currentActivity.getString(R.string.install_wallet_from_ads));
    }

    public static String getPackageName() { return  Application.PACKAGE_NAME; }

    public void CreateIABHelper() {

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from your Aptoide's back office). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */

        String base64EncodedPublicKey = Application.developerPublicKey;

        Log.d("Unity","CreateIABHelper:: base64encodedkey " + base64EncodedPublicKey);

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR") || base64EncodedPublicKey.equals("")) {
            UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnInitializeFail","Developer Public Key not supplied!");
            return;
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(UnityPlayer.currentActivity, base64EncodedPublicKey, _useMainNet);

        Application.iabHelper = mHelper;

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(shouldLog);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(
                mSetupFinishedListener
        );
    }

    public void makePurchase(String skuID) {
        makePurchase(skuID,"");
    }

    public void makePurchase(String skuID, String payload) {

        Log.d("Unity", "[PrepareBuy] Calling makePurchase with skuid" + skuID + " and payload " + payload);

        Intent shareIntent = new Intent();
        shareIntent.putExtra(PurchaseActivity.SKUID_TAG,skuID);
        shareIntent.putExtra(PurchaseActivity.PAYLOAD_TAG,payload);
        shareIntent.setClass(UnityPlayer.currentActivity,PurchaseActivity.class);
        UnityPlayer.currentActivity.startActivityForResult(shareIntent, REQUEST_CODE);
    }

    public void consumePurchase(String skuID) {
        try {
            Purchase p = Application.application.getLatestPurchase();
            if (!p.getSku().equals(skuID)) {
                String errorMsg = skuID + " doesn't match the skuID of latest purchase " + p.getSku();
                complain(errorMsg);
                UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseFailure",errorMsg);
            }
            mHelper.consumeAsync(p,mConsumeFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            String errorMsg = "Error consuming " + skuID + ". Another async operation in progress.";
            complain(errorMsg);
            UnityPlayer.UnitySendMessage(appcoinsPrefabName,"OnPurchaseFailure",errorMsg);
        }
    }

    public String getAPPCPriceStringForSKU(String skuID) throws RemoteException, JSONException {
        return mHelper.getAPPCPriceStringForSKU(skuID);
    }

    public String getFiatPriceStringForSKU(String skuID) throws RemoteException, JSONException {
        return mHelper.getFiatPriceStringForSKU(skuID);
    }

    public String getFiatCurrencyCodeForSKU(String skuID) throws RemoteException, JSONException {
        return mHelper.getFiatCurrencyCodeForSKU(skuID);
    }


    public boolean OwnsProduct(String skuID) {
        boolean hasPurchase = false;
        if (mInventory != null) {
            hasPurchase = mInventory.hasPurchase(skuID);
        }

        return hasPurchase;
    }

    void complain(String message) {
        Log.e(TAG, "**** Unity BDS error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
//        AlertDialog.Builder bld = new AlertDialog.Builder(this);
//        bld.setMessage(message);
//        bld.setNeutralButton("OK", null);
//        Log.d(TAG, "Showing alert dialog: " + message);
//        bld.create()
//                .show();
    }

    /** Verifies the developer payload of a purchase. */
    void verifyDeveloperPayload(Purchase p, IabHelper.OnPayloadValidationFinishedListener validationListener) {
        _toBeValidatedPurchase = p;
        mValidationFinishedListener = validationListener;

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

        //Reset value
        _isPayloadValid = false;

        //UnitySendMessage can't return values so we call it and on Unity's side we call a function in java to change the value
        UnityPlayer.UnitySendMessage(appcoinsPrefabName,"AskForPayloadValidation",payload);
    }

    public void setPayloadValidationStatus(boolean status) {
        Log.d(TAG, "setPayloadValidationStatus: " + status);
        _isPayloadValid = status;

        mValidationFinishedListener.onValidationFinished(status,_toBeValidatedPurchase);
    }

    public boolean UseMainNet() {
        return _useMainNet;
    }

}
