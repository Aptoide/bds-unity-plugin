package com.aptoide.iabexample.util;

import android.os.IBinder;

/**
 * Created by marcelobenites on 02/11/16.
 */

public class BillingServiceFactory {

  public static BillingService create(final IBinder service) {
    return new AppCoinsBillingService(service);
  }
}
