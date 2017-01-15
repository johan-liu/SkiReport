package com.app.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by MaHanYong on 4/17/14.
 */
public class AppData {
    public AppData()
    {

    }

    public static boolean GetUserLoginState(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getBoolean("skireport.registerstate", false);
    }

    public static void SetUserLoginState(Context ctx, boolean bstate)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("skireport.registerstate", bstate);
        editor.commit();
    }

    public static String GetUserName(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getString("skireport.username", "");
    }

    public static void SetUserName(Context ctx, String username)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("skireport.username", username);
        editor.commit();
    }

    public static String GetUserCountry(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getString("skireport.usercountry", "");
    }

    public static void SetUserCountry(Context ctx, String usercountry)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("skireport.usercountry", usercountry);
        editor.commit();
    }

    public static boolean GetPushNotificationState(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getBoolean("skireport.pushnotification", true);
    }

    public static void SetPushNotificationState(Context ctx, boolean bstate)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("skireport.pushnotification", bstate);
        editor.commit();
    }

    public static boolean GetTemperState(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getBoolean("skireport.temperature", true);
    }

    public static void SetTemperState(Context ctx, boolean bstate)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("skireport.temperature", bstate);
        editor.commit();
    }

    public static int GetUserId(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getInt("skireport.userid", -1);
    }

    public static void SetUserId(Context ctx, int nUserId)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("skireport.userid", nUserId);
        editor.commit();
    }

    public static int GetLastUpdatedForPush(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getInt("skireport.lastupdatedforpush", -1);
    }

    public static void SetLastUpdatedForPush(Context ctx, int lastupdatedid)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("skireport.lastupdatedforpush", lastupdatedid);
        editor.commit();
    }

    public static int GetNotifID(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        return prefs.getInt("skireport.notifyid", -1);
    }

    public static void SetNotifID(Context ctx, int notifyId)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Global.config_appkey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("skireport.notifyid", notifyId);
        editor.commit();
    }

    public static String GetUserPhoneEmail(Context ctx)
    {
        String emailAddr = "";
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                emailAddr = account.name;
                break;
            }
        }

        return emailAddr;
    }

    public static String GetUserLocation(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return "";
    }

    public static String GetDeviceUdid(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return android_id;
    }
}
