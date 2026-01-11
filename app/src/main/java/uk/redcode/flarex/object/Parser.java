package uk.redcode.flarex.object;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Pattern;

import uk.redcode.flarex.network.CFCommunity;

public class Parser {

    public static final int BYTE = 0;
    public static final int KB = 1;
    public static final int MB = 2;
    public static final int GB = 3;

    public static final String REGEXP_EMAIL = "^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final Pattern MATCHER_EMAIL = Pattern.compile(REGEXP_EMAIL);
    public static final String REGEXP_DOMAIN = "^(?:[_a-z0-9](?:[_a-z0-9-]{0,61}[a-z0-9])?\\.)+(?:[a-z](?:[a-z0-9-]{0,61}[a-z0-9])?)?$";
    public static final Pattern MATCHER_DOMAIN = Pattern.compile(REGEXP_DOMAIN);
    public static final String REGEXP_TOKEN = "^[a-zA-Z0-9_\\-]+$";
    public static final Pattern MATCHER_TOKEN = Pattern.compile(REGEXP_TOKEN);
    public static final String REGEXP_IP4 = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
    public static final Pattern MATCHER_IP4 = Pattern.compile(REGEXP_IP4);
    public static final String REGEXP_IP6 = "^((([0-9A-Fa-f]{1,4}:){1,6}:)|(([0-9A-Fa-f]{1,4}:){7}))([0-9A-Fa-f]{1,4})$";
    public static final Pattern MATCHER_IP6 = Pattern.compile(REGEXP_IP6);


    public static ArrayList<String> parseStringList(JSONArray list) throws JSONException {
        ArrayList<String> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(list.getString(i));
        }
        return done;
    }

    public static JSONArray toStringList(ArrayList<String> list) {
        JSONArray array = new JSONArray();
        for (String s : list) {
            array.put(s);
        }
        return array;
    }

    public static ArrayList<Integer> parseIntList(JSONArray list) throws JSONException {
        ArrayList<Integer> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(list.getInt(i));
        }
        return done;
    }

    public static double parseByte(double size, int asked) {
        double size_kb = size /1024;
        double size_mb = size_kb / 1024;
        double size_gb = size_mb / 1024 ;

        switch (asked) {
            case KB: return Math.round(size_kb * 100.0) / 100.0;
            case MB: return Math.round(size_mb * 100.0) / 100.0;
            case GB: return Math.round(size_gb * 100.0) / 100.0;
            case BYTE:
            default: return size;
        }
    }

    public static String getByteLabel(int type) {
        switch (type) {
            case KB:
                return "Kb";
            case MB:
                return "Mb";
            case GB:
                return "Gb";
            case BYTE:
            default:
                return "Byte";
        }
    }

    public static int findNiceByte(double size) {
        size /= 1024;
        if (size < 1000) return KB;
        size /= 1024;
        if (size < 1000) return MB;
        size /= 1024;
        if (size < 1000) return GB;
        return BYTE;
    }

    public static boolean parseBoolean(String value) {
        return value.equals("on");
    }

    public static String convertBoolean(boolean value) {
        return value ? "on" : "off";
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        TimeZone zone = TimeZone.getTimeZone( "ZULU" );
        format.setTimeZone(zone);
        String output = format.format(date);

        int inset0 = 9;
        int inset1 = 6;

        String s0 = output.substring( 0, output.length() - inset0 );
        String s1 = output.substring( output.length() - inset1);

        //result = result.replaceAll( "UTC", "+00:00" );
        return s0 + s1;
    }

    @SuppressLint("SimpleDateFormat")
    public static Calendar parseDate(String date) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Instant instant = Instant.parse(date);
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            return GregorianCalendar.from(zdt);
        }


        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        // this is zero time so we need to add that TZ indicator for
        if ( date.endsWith( "Z" ) ) {
            date = date.substring( 0, date.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = date.substring( 0, date.length() - inset );
            String s1 = date.substring( date.length() - inset);

            date = s0 + "GMT" + s1;
        }

        try {
            Date parsed = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsed);
            return calendar;
        } catch (ParseException e) {
            Logger.error(e);
            return Calendar.getInstance();
        }
    }

    public static ArrayList<String> parseUserAvatar(JSONArray posters, JSONArray users) throws JSONException {
        ArrayList<String> done = new ArrayList<>();
        for (int i = 0; i < posters.length(); i++) {
            int userId = posters.getJSONObject(i).getInt("user_id");

            for (int j = 0; j < users.length(); j++) {
                if (users.getJSONObject(j).getInt("id") == userId) {
                    String avatar = users.getJSONObject(j).getString("avatar_template");
                    avatar = avatar.replace("{size}", "45");
                    if (avatar.startsWith("/")) avatar = CFCommunity.BASE_URL + avatar;
                    done.add(avatar);
                    break;
                }
            }
        }
        return done;
    }

    public static HashMap<String, String> jsonToHashMap(JSONObject json) throws JSONException {
        HashMap<String, String> map = new HashMap<>();

        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = json.get(key).toString();
            map.put(key, value);
        }
        return map;
    }

    public static boolean isEmail(String value) {
        return MATCHER_EMAIL.matcher(value).matches();
    }

    public static boolean isValidToken(String value) { return MATCHER_TOKEN.matcher(value).matches(); }

    public static boolean isIPv4(String value) { return MATCHER_IP4.matcher(value).matches(); }

    public static boolean isIPv6(String value) { return MATCHER_IP6.matcher(value).matches(); }

    public static boolean isDomain(String value) { return MATCHER_DOMAIN.matcher(value).matches(); }
}