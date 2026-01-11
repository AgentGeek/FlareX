package uk.redcode.flarex.ui;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import uk.redcode.flarex.object.Logger;

public class LayoutManager {

    public static final String ANALYTICS = "analytics";
    public static final String DNS = "dns";
    public static final String DNS_EDIT = "dns.edit";
    public static final String FIREWALL = "firewall";
    public static final String FIREWALL_EDIT = "firewall.edit";
    public static final String ZONE_CONFIG = "zone.config";
    public static final String ZONE_CONFIG_EDIT = "zone.config.edit";
    public static final String CERTIFICATES = "certificates";
    public static final String NOTIFICATIONS = "notifications";

    private static final String TAG = "Layout";
    private static HashMap<String, Boolean> map;

    public static void init(Context context) {
        SharedPreferences sharedP = context.getSharedPreferences("LAYOUT", Context.MODE_PRIVATE);
        map = new HashMap<>();

        // init hash map
        map.put(ANALYTICS, sharedP.getBoolean(ANALYTICS, true));
        map.put(DNS, sharedP.getBoolean(DNS, true));
        map.put(DNS_EDIT, sharedP.getBoolean(DNS_EDIT, true));
        map.put(FIREWALL, sharedP.getBoolean(FIREWALL, true));
        map.put(FIREWALL_EDIT, sharedP.getBoolean(FIREWALL_EDIT, true));
        map.put(ZONE_CONFIG, sharedP.getBoolean(ZONE_CONFIG, true));
        map.put(ZONE_CONFIG_EDIT, sharedP.getBoolean(ZONE_CONFIG_EDIT, true));
        map.put(CERTIFICATES, sharedP.getBoolean(CERTIFICATES, true));
    }

    public static void reset(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("LAYOUT", Context.MODE_PRIVATE).edit();
        for (String key : map.keySet()) {
            editor.putBoolean(key, true);
        }
        editor.apply();
    }

    public static boolean get(String key) {
        return map.containsKey(key) ? map.get(key) : false;
    }

    public static void set(String key, boolean value) {
        map.put(key, value);
    }

    public static boolean hasDashboard() {
        if (get(ANALYTICS)) return true;
        return get(ZONE_CONFIG);
    }

    public static boolean hasLayout() {
        log();
        for (String key : map.keySet()) {
            if (map.get(key)) return true;
        }
        return false;
    }

    public static void log() {
        for (String key : map.keySet()) {
            Logger.info(TAG, String.format("%s -> %b", key, map.get(key)));
        }
    }

    public static void save(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("LAYOUT", Context.MODE_PRIVATE).edit();
        for (String key : map.keySet()) {
            editor.putBoolean(key, map.get(key));
        }
        editor.apply();
    }
}
