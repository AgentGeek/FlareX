package uk.redcode.flarex.object;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.ui.LayoutManager;

public class User {

    private static String EMAIL = "";
    private static String KEY = "";
    private static String ACCOUNT_ID = "";
    private static int MODE = -1;

    private static final String SP_NAME = "USER";
    private static final String TAG = "User";

    public static boolean isConnected(Context context) {
        SharedPreferences sharedP = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String apikey = sharedP.getString("apikey", "");
        if (sharedP.getInt("mode", -1) == CFApi.TYPE_MASTER_KEY) {
            String email = sharedP.getString("email", "");
            return !email.isEmpty() && !apikey.isEmpty();
        } else {
            return !apikey.isEmpty();
        }
    }

    public static String getEmail(Context context) {
        if (!EMAIL.isEmpty()) return EMAIL;
        String email = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("email", "");
        if (!email.isEmpty()) EMAIL = email;
        return email;
    }

    public static String getKey(Context context) {
        if (!KEY.isEmpty()) return KEY;
        String key = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("apikey", "");
        if (!key.isEmpty()) KEY = key;
        return key;
    }

    public static int getMode(Context context) {
        if (MODE != -1) return MODE;
        int mode = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt("mode", -1);
        if (mode != -1) MODE = mode;
        return mode;
    }

    public static String getAccountId(Context context) {
        if (!ACCOUNT_ID.isEmpty()) return ACCOUNT_ID;
        String id = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("accountId", "");
        if (!id.isEmpty()) ACCOUNT_ID = id;
        return id;
    }

    public static void logout(Context context) {
        Logger.info(TAG, "Logout ...");
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("apikey", "");
        editor.putString("accountId", "");
        editor.apply();
        LayoutManager.reset(context);
    }

    public static void setAvatar(Context context, boolean male) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("male", male);
        editor.apply();
    }

    public static boolean getAvatar(Context context) {
        SharedPreferences sharedP = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sharedP.getBoolean("male", true);
    }

    public static String parseAccountId(JSONObject body, Context context) throws JSONException {
        if (!body.getBoolean("success")) throw new JSONException("Error parsing account ID");
        if (body.getJSONArray("result").length() == 0) throw new JSONException("Error parsing account ID");

        JSONObject account = body.getJSONArray("result").getJSONObject(0);
        String id = account.getJSONObject("account").getString("id");

        // save
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString("accountId", id).apply();
        ACCOUNT_ID = id;

        return id;
    }
}
