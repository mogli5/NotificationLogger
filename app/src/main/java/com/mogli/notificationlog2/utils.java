package com.mogli.notificationlog2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class utils {
    public static String getAppNameFromPackage(Context context, String packageName, boolean returnNull) {
        final PackageManager pm = context.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        if (returnNull) {
            return ai == null ? null : pm.getApplicationLabel(ai).toString();
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : packageName);
    }

    public static Drawable getAppIconFromPackage(Context context, String packageName) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        Drawable drawable = null;
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            if (ai != null) {
                drawable = pm.getApplicationIcon(ai);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public static String nullToEmptyString(CharSequence charsequence) {
        if (charsequence == null) {
            return "";
        } else {
            return charsequence.toString();
        }
    }

    public static String getTime(Long currentTimeMillis){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date resultdate = new Date(currentTimeMillis);
        String currentTime = df.format(resultdate);
        return currentTime;
    }

    public static String getDate(Long currentTimeMillis){
        DateFormat df = new SimpleDateFormat("d MMM yyyy");
        Date resultdate = new Date(currentTimeMillis);
        String currentDate = df.format(resultdate);
        return currentDate;
    }

}
