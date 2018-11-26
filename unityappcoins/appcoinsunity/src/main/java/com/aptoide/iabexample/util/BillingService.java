package com.aptoide.iabexample.util;

import android.os.Bundle;
import android.os.RemoteException;
import java.util.List;

/**
 * Created by marcelobenites on 02/11/16.
 */

public interface BillingService {

  int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException;

  Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws
      RemoteException;

  Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
      String developerPayload) throws RemoteException;

  Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws
      RemoteException;

  int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws
      RemoteException;

  Bundle getBuyIntentToReplaceSkus(int apiVersion, String packageName, List<String> oldSkus, String newSku, String type, String developerPayload) throws
      RemoteException ;
}
