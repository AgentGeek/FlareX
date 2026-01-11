package uk.redcode.flarex.work;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.DailStat;
import uk.redcode.flarex.object.NotificationManager;
import uk.redcode.flarex.object.Zone;

public class DailyStatsWorker extends Worker {

    private static final String KEY = "APP_DAILY_STATS";
    private static final String KEY_NAME = "ZONE_NAME";
    private static final String TAG = "DailyStats";

    private final Context context;

    public DailyStatsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    private interface Listener {
        void onFinish(DailStat.DailyStatHandler handler);
        void onError(DailStat.DailyStatHandler handler);
    }

    /*
        Enabling
     */

    public static boolean isEnable(Context context, String zoneId) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getBoolean(zoneId, false);
    }

    public static void setEnable(Context context, String zoneId, boolean isChecked) {
        context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit().putBoolean(zoneId, isChecked).apply();
    }

    /*
        Name
     */

    public static void saveName(Context context, Zone zone) {
        context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE).edit().putString(zone.zoneId, zone.name).apply();
    }

    public static String getName(Context context, String zoneId) {
        return context.getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE).getString(zoneId, "Zone Name");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: START WORK");
        if (!isTime()) return Result.success();
        run();
        return Result.success();
    }

    private boolean isTime() {
        Calendar cal = Calendar.getInstance();
        Log.d(TAG, "isTime: "+cal.get(Calendar.HOUR_OF_DAY));
        if (cal.get(Calendar.HOUR_OF_DAY) >= 22 && cal.get(Calendar.HOUR_OF_DAY) < 23) {
            Log.d(TAG, "isTime: yes: "+cal.get(Calendar.HOUR_OF_DAY));
            return true;
        }

        Log.d(TAG, "isTime: no: "+cal.get(Calendar.HOUR_OF_DAY));
        return false;
    }

    /*
        Magic
     */

    public void run() {
        Log.d(TAG, "run: run daily stats");

        ArrayList<String> zonesId = new ArrayList<>();

        Map<String, ?> list = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : list.entrySet()) {
            Log.d(TAG, "run: "+entry.getKey());
            Log.d(TAG, "run: "+entry.getValue());
            Log.d(TAG, "run: -----------------");

            try {
                boolean enable = (boolean) entry.getValue();
                if (enable) zonesId.add(entry.getKey());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run: invalid boolean: "+entry.getKey());
            }
        }

        getValue(zonesId, 0, new ArrayList<>());
    }

    private void getValue(ArrayList<String> zonesId, final int pos, ArrayList<DailStat.DailyStatHandler> result) {
        if (pos >= zonesId.size()) {
            makeNotifications(result);
            return;
        }

        DailStat.DailyStatHandler handler = new DailStat.DailyStatHandler(zonesId.get(pos));

        contactGraphQL(handler, new Listener() {
            @Override
            public void onFinish(DailStat.DailyStatHandler handler) {
                result.add(handler);
                getValue(zonesId, pos+1, result);
            }

            @Override
            public void onError(DailStat.DailyStatHandler handler) {
                Log.d(TAG, "onError: "+handler.zoneId);
            }
        }, true);
    }

    @SuppressLint("DefaultLocale")
    private void makeNotifications(ArrayList<DailStat.DailyStatHandler> result) {
        // check permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "sendNotification: notification permission not granted, leaving");
            return;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = 1000;

        for (DailStat.DailyStatHandler handler : result) {
            Log.d(TAG, "makeNotifications: generate: "+notificationId);
            NotificationCompat.Builder builder = handler.createNotification(context);
            notificationManager.notify(notificationId, builder.build());
            notificationId++;
        }

        if (result.size() > 1) {
            NotificationCompat.Builder summary = new NotificationCompat.Builder(context, NotificationManager.CHANNEL_ID_DAILY_STAT)
                .setContentTitle("Your zone daily stats")
                .setContentText(String.format("%d zones", result.size()))
                .setSmallIcon(R.drawable.ic_coldcloud)
                .setStyle(new NotificationCompat.InboxStyle().setSummaryText("Daily Stats"))
                .setGroup(NotificationManager.GROUP_DAILY_STATS)
                .setGroupSummary(true);
            notificationManager.notify(0, summary.build());
        }

    }

    private void contactGraphQL(DailStat.DailyStatHandler handler, Listener listener, boolean today) {
        try {
            JSONObject data = getDashboardData(handler.zoneId, today);

            CFApi.graphql(context, data, new CFApi.JSONListener() {
                @Override
                public void onResult(JSONObject body) {
                    try {
                        JSONObject values = body.getJSONObject("data").getJSONObject("viewer").getJSONArray("zones").getJSONObject(0).getJSONArray("zones").getJSONObject(0);

                        if (today) {
                            handler.today = DailStat.parse(values);
                            contactGraphQL(handler, listener, false);
                        } else {
                            handler.yesterday = DailStat.parse(values);
                            //handler.printDiff();
                            listener.onFinish(handler);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "getValue: error fetching data: "+handler.zoneId);
                        listener.onError(handler);
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "getValue: error fetching data: "+handler.zoneId);
                    listener.onError(handler);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getValue: error fetching data: "+handler.zoneId);
            listener.onError(handler);
        }
    }

    private JSONObject getDashboardData(String zoneId, boolean today) throws Exception {
        InputStream is = context.getResources().openRawResource(R.raw.graphql_daily_stats);
        byte[] b = new byte[is.available()];
        is.read(b);

        String query = new String(b);

        int diff = today ? (1000 * 60 * 60 * 24) : (1000 * 60 * 60 * 24 * 2);
        Date until = new Date();
        Date since = new Date(until.getTime() - diff);

        JSONObject variables = new JSONObject();
        variables.put("zoneTag", zoneId);
        variables.put("until", getJSONDateForGraphQL(until));
        variables.put("since", getJSONDateForGraphQL(since));

        JSONObject data = new JSONObject();
        data.put("variables", variables);
        data.put("operationName", "GetZoneAnalytics");
        data.put("query", query);

        //Log.d(TAG, "toSend: "+data.toString(4));

        return data;
    }

    private String getJSONDateForGraphQL(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
