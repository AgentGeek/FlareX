package uk.redcode.flarex.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.work.DailyStatsWorker;

public class DailStat {

    public int bandwidth;
    public int cachedBandwidth;
    public int requests;
    public int cachedRequest;
    public int visitors;

    public static DailStat parse(JSONObject data) throws JSONException {
        DailStat s = new DailStat();

        s.bandwidth = data.getJSONObject("sum").getInt("bytes");
        s.cachedBandwidth = data.getJSONObject("sum").getInt("cachedBytes");
        s.requests = data.getJSONObject("sum").getInt("requests");
        s.cachedRequest = data.getJSONObject("sum").getInt("cachedRequests");
        s.visitors = data.getJSONObject("uniq").getInt("uniques");

        return s;
    }

    public static class DailyStatHandler {

        public final String zoneId;
        public DailStat today;
        public DailStat yesterday;


        public DailyStatHandler(String zoneId) {
            this.zoneId = zoneId;
        }

        public void printDiff() {
            Log.d("DailyStat", String.format("%d - %d", yesterday.bandwidth, today.bandwidth));
            Log.d("DailyStat", String.format("%d - %d", yesterday.visitors, today.visitors));
        }

        public NotificationCompat.Builder createNotification(Context context) {
            String zoneName = DailyStatsWorker.getName(context, zoneId);
            String content = getNotificationContent();
            Log.d("DailyStat", "createNotification: "+content);

            return new NotificationCompat.Builder(context, NotificationManager.CHANNEL_ID_DAILY_STAT)
                .setSmallIcon(R.drawable.ic_coldcloud)
                .setContentTitle(zoneName)
                .setContentText(Html.fromHtml(content))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(NotificationManager.GROUP_DAILY_STATS);
        }

        @SuppressLint("DefaultLocale")
        private String getNotificationContent() {
            return getVisitorLabel() + getBandwidthLabel();
        }

        @SuppressLint("DefaultLocale")
        private String getBandwidthLabel() {
            int type = Parser.findNiceByte(yesterday.bandwidth);

            double a = Parser.parseByte(yesterday.bandwidth, type);
            double b = Parser.parseByte(today.bandwidth, type);
            double tmp = (b - a)/((b + a)/2);
            double percent = tmp * 100;

            String yBytes = String.format("%.2f %s", a, Parser.getByteLabel(type));
            String tBytes = String.format("%.2f %s", b, Parser.getByteLabel(type));
            String direction = percent > 0 ? "Up" : "Down";
            return String.format("<b>Bandwidth</b>: %s → %s  <b>|</b>  %.2f%% %s", yBytes, tBytes, percent, direction);
            //Log.d("HERE", "getBandwidthLabel: "+label);
        }

        @SuppressLint("DefaultLocale")
        private String getVisitorLabel() {
            int a = yesterday.visitors;
            int b = today.visitors;

            float tmp = (float) (b - a)/b;
            float percent = tmp*100;

            String direction = percent > 0 ? "Up" : "Down";
            return String.format("<b>Visitor</b>: %d → %d <b>|</b> %.2f%% %s<br>", yesterday.visitors, today.visitors, percent, direction);
            //Log.d("HERE", "getVisitorLabel: "+label);
        }
    }

}
