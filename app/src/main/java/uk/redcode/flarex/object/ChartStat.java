package uk.redcode.flarex.object;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.redcode.flarex.module.TimeRange;

public class ChartStat {

    String timestamp;
    public int bandwidth;
    public int cachedBandwidth;
    public int requests;
    public int cachedRequest;
    public int visitors;

    // security
    public int tlsNone;
    public int tls10;
    public int tls12;
    public int tls13;

    // threats
    public int threats;
    public ArrayList<Threat> threatType;

    // performance
    public ArrayList<ContentType> contentTypes = new ArrayList<>();

    public static ArrayList<ChartStat> parse(JSONArray list) throws JSONException {
        ArrayList<ChartStat> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static ChartStat parse(JSONObject data) throws JSONException {
        ChartStat s = new ChartStat();

        s.timestamp = data.getJSONObject("dimensions").getString("timeslot");
        s.bandwidth = data.getJSONObject("sum").getInt("bytes");
        s.cachedBandwidth = data.getJSONObject("sum").getInt("cachedBytes");
        s.requests = data.getJSONObject("sum").getInt("requests");
        s.cachedRequest = data.getJSONObject("sum").getInt("cachedRequests");
        s.visitors = data.getJSONObject("uniq").getInt("uniques");

        s.contentTypes = parseContentTypes(data.getJSONObject("sum").getJSONArray("contentTypeMap"));
        s = parseTLS(s, data.getJSONObject("sum").getJSONArray("clientSSLMap"));
        s = parseThreats(s, data.getJSONObject("sum"));

        return s;
    }

    private static ChartStat parseThreats(ChartStat s, JSONObject data) throws JSONException {

        s.threatType = new ArrayList<>();
        s.threats = data.getInt("threats");

        JSONArray list = data.getJSONArray("threatPathingMap");
        for (int i = 0; i < list.length(); i++) {
            JSONObject actual = list.getJSONObject(i);

            Threat t = new Threat();
            t.key = actual.getString("key");
            t.count = actual.getInt("requests");
            s.threatType.add(t);
        }

        return s;
    }

    private static ArrayList<ContentType> parseContentTypes(JSONArray list) throws JSONException {
        ArrayList<ContentType> done = new ArrayList<>();

        for (int i = 0; i < list.length(); i++) {
            done.add(ContentType.parse(list.getJSONObject(i)));
        }
        return done;
    }

    private static ChartStat parseTLS(ChartStat s, JSONArray list) throws JSONException {
        for (int i = 0; i < list.length(); i++) {
            JSONObject actual = list.getJSONObject(i);
            String key = actual.getString("key");
            int value = actual.getInt("requests");

            if (key.equals("none")) s.tlsNone += value;
            if (key.equals("TLSv1")) s.tls10 += value;
            if (key.equals("TLSv1.2")) s.tls12 += value;
            if (key.equals("TLSv1.3")) s.tls13 += value;
        }
        return s;
    }

    @SuppressLint("DefaultLocale")
    public String getDateLabel(int range) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        //this is zero time so we need to add that TZ indicator for
        if ( timestamp.endsWith( "Z" ) ) {
            timestamp = timestamp.substring( 0, timestamp.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = timestamp.substring( 0, timestamp.length() - inset );
            String s1 = timestamp.substring( timestamp.length() - inset);

            timestamp = s0 + "GMT" + s1;
        }

        try {
            if (range != TimeRange.TIME_24HOURS) {
                return String.format("%s/%s", timestamp.split("-")[2], timestamp.split("-")[1]);
            }
            Date date = df.parse(timestamp);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String label = getDayLabel(cal);
            return String.format("%s %d:%d", label, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        }  catch (ParseException e) {
            Logger.error(e);
            return "Date: Error";
        }

    }

    @SuppressLint("DefaultLocale")
    private String getDayLabel(Calendar date) {
        Calendar now = Calendar.getInstance();

        // today & yesterday
        if (date.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            && date.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {

            if (date.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                return "Today";
            } else if (date.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)-1) {
                return "Yesterday";
            }

        }

        return String.format("%d/%d", date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH));
    }
}