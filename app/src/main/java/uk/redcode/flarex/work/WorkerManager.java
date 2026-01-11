package uk.redcode.flarex.work;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Logger;

public class WorkerManager {

    private static final String TAG = "WorkerManager";
    private static final String BLOG = "BLOG";
    private static final String STATS = "STATS";

    public static void init(Context context) {
        Logger.info(TAG, "Starting ...");
        WorkManager manager = WorkManager.getInstance(context);

        // blog work
        if (AppParameter.getBoolean(context, AppParameter.BLOG_NOTIFICATION, false)) {
            PeriodicWorkRequest blogWork = new PeriodicWorkRequest.Builder(BlogNotificationWorker.class, 1, TimeUnit.HOURS).build();
            manager.enqueueUniquePeriodicWork(BLOG, ExistingPeriodicWorkPolicy.KEEP, blogWork);
        } else {
            manager.cancelUniqueWork(BLOG);
        }

        // Daily Stat
        PeriodicWorkRequest statsWork = new PeriodicWorkRequest.Builder(DailyStatsWorker.class, 1, TimeUnit.HOURS).build();
        manager.enqueueUniquePeriodicWork(STATS, ExistingPeriodicWorkPolicy.KEEP, statsWork);
    }

}
