package uk.redcode.flarex.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class AppParameter {

    private static final String TAG = "AppParameter";
    private static final String KEY = "APP_PARAM";

    public static final String THEME = "theme";
    public static final String SYNC_CHART = "sync_chart";
    public static final String REMEMBER_ZONE = "remember";
    public static final String REMEMBER_ACCOUNT = "remember_account";
    public static final String LAST_ZONE = "last_zone";
    public static final String LAST_POST = "last_post";
    public static final String LAST_ACCOUNT = "last_account";
    public static final String IMAGE_COMPRESSION = "image_compression";
    public static final String BLOG_NOTIFICATION = "blog_notification";
    public static final String ENABLE_FINGERPRINT = "enable_fingerprint";

    // BOOLEAN
    public static void setBoolean(Context context, String key, boolean value) {
        Logger.info(TAG, String.format("Update boolean: %s -> %b", key, value));
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static boolean getBoolean(Context context, String key, boolean def) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getBoolean(key, def);
    }

    // INTEGER
    @SuppressLint("DefaultLocale")
    public static void setInt(Context context, String key, int value) {
        Logger.info(TAG, String.format("Update int: %s -> %d", key, value));
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static int getInt(Context context, String key, int def) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getInt(key, def);
    }

    // String
    public static void setString(Context context, String key, String value) {
        Logger.info(TAG, String.format("Update string: %s -> %s", key, value));
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getString(Context context, String key, String def) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(key, def);
    }

    /*
        Blog
     */

    public static String getLastPost(Context context) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(LAST_POST, "");
    }

    public static void setLastPost(Context context, String url) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(LAST_POST, url);
        editor.apply();
    }

    /*
        Security
     */

    public static void saveLastZoneSecurity(Context context, Zone zone, String status) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(String.format("last-zone-security-%s", zone.zoneId), status);
        editor.apply();
    }

    public static String getLastZoneSecurity(Context context, Zone zone) {
        SharedPreferences sharedP = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return sharedP.getString(String.format("last-zone-security-%s", zone.zoneId), "null");
    }

    /*
        Zone
     */

    @Nullable
    public static Zone getLastZone(Context context) {
        try {
            String zone = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(LAST_ZONE, "");
            if (zone.isEmpty()) return null;
            return Zone.parse(new JSONObject(zone));
        } catch (JSONException e) {
            Logger.error(e);
            return null;
        }
    }

    public static void setLastZone(Context context, Zone zone) {
        Logger.info(TAG, String.format("Update Last zone: %s", zone.zoneId));
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(LAST_ZONE, zone.raw);
        editor.apply();
    }

    /*
        Account
     */

    @Nullable
    public static CFAccount getLastAccount(Context context) {
        try {
            String account = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(LAST_ACCOUNT, "");
            if (account.isEmpty()) return null;
            return CFAccount.parse(new JSONObject(account));
        } catch (JSONException e) {
            Logger.error(e);
            return null;
        }
    }

    public static void setLastAccount(Context context, CFAccount account) {
        Logger.info(TAG, String.format("Update Last account: %s", account.name));
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(LAST_ACCOUNT, account.raw);
        editor.apply();
    }

}
