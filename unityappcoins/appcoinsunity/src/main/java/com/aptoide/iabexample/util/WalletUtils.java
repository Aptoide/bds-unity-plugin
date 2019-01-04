package com.aptoide.iabexample.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aptoide.iabexample.R;

import java.util.List;

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

        return hasHandlerAvailable(intent, context);
    }

    public static boolean hasSpecificWalletInstalled(Context context, String walletPackageName) {
        boolean found = true;

        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(walletPackageName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }

        return found;
    }

    public static void checkWalletVersion(Context context) {

        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo("com.appcoins.wallet", 0 );
        String verName = pinfo.versionName;
        int verCode = pinfo.versionCode;
        Log.d("Unity", "BDS Wallet Version:" + verName + verCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showWalletInstallDialog(Context context, String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.wallet_missing)
                .setMessage(message)
                .setPositiveButton(R.string.install, (dialog, which) -> gotoStore(context))
                .setNegativeButton(R.string.skip, (DialogInterface dialog, int which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
