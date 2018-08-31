package com.aptoide.appcoinsunity.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aptoide.appcoinsunity.R;
import com.aptoide.appcoinsunity.UnityAppcoins;

import java.util.List;

import io.reactivex.Single;

public class WalletUtils {

    public static boolean hasHandlerAvailable(Intent intent, Context context) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return !infos.isEmpty();
    }

    public static boolean hasWalletInstalled(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("ethereum:");
        intent.setData(uri);

        boolean val = hasHandlerAvailable(intent, context);

        Log.d(UnityAppcoins.TAG,"Wallet " + (val ? "IS" : "ISN'T") + " installed" );

        return val;
    }

    public static Single<Boolean> promptToInstallWallet(Activity activity, String message) {
        return showWalletInstallDialog(activity, message).doOnSuccess(aBoolean -> {
            if (aBoolean) {
                gotoStore(activity);
            }
        });
    }
    private static Single<Boolean> showWalletInstallDialog(Context context, String message) {
        return Single.create(emitter -> {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.wallet_missing)
                    .setMessage(message)
                    .setPositiveButton(R.string.install, (dialog, which) -> emitter.onSuccess(true))
                    .setNegativeButton(R.string.skip, (dialog, which) -> emitter.onSuccess(false))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }
    @NonNull
    private static void gotoStore(Context activity) {
        String appPackageName = "com.appcoins.wallet";
        try {
            activity.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

}
