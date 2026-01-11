package uk.redcode.flarex.object;

import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;

import uk.redcode.flarex.R;

public class NotificationManager {

    public static final String CHANNEL_ID_DAILY_STAT = "channel_daily_stat";
    public static final String CHANNEL_ID_BLOG_POST = "channel_blog_post";

    public static final String GROUP_DAILY_STATS = "uk.redcode.flarex.DAILY_STAT";
    public static final String GROUP_BLOG_POST = "uk.redcode.flarex.BLOG_POST";

    public static void verifyChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        verifyDailyStat(context);
        verifyBlogPost(context);
    }

    private static void verifyDailyStat(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        // Daily Stat
        CharSequence name = context.getString(R.string.channel_daily_stat);
        String description = context.getString(R.string.channel_daily_stat_description);
        int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DAILY_STAT, name, importance);
        channel.setDescription(description);

        android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private static void verifyBlogPost(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        // Daily Stat
        CharSequence name = context.getString(R.string.channel_blog_post);
        String description = context.getString(R.string.channel_blog_post_description);
        int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_BLOG_POST, name, importance);
        channel.setDescription(description);

        android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
