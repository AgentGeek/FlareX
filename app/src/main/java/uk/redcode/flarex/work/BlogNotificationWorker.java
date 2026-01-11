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

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.CFPost;
import uk.redcode.flarex.object.NotificationManager;

public class BlogNotificationWorker extends Worker {

    private final Context context;
    private final String TAG = "BlogWorker";

    public BlogNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!AppParameter.getBoolean(context, AppParameter.BLOG_NOTIFICATION, false)) {
            Log.d(TAG, "doWork: disabled, exit.");
            return Result.success();
        }

        run();
        return Result.success();
    }

    private void run() {
        CFApi.getBlogPosts(context, 1, new CFApi.HTMLListener() {
            @Override
            public void onResult(String html) {
                Log.d(TAG, "run: blog get");
                ArrayList<CFPost> posts = CFPost.parseMultiple(html);
                Log.d(TAG, "run: post parsed");
                sendNotification(compare(posts));
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "run: Error fetching blog data");
            }
        });
    }

    private ArrayList<CFPost> compare(ArrayList<CFPost> posts) {
        String lastUrl = AppParameter.getLastPost(context);
        ArrayList<CFPost> toNotify = new ArrayList<>();

        for (CFPost post : posts) {
            if (post.url.equals(lastUrl)) break;
            toNotify.add(post);
        }

        Log.d(TAG, "compare: " + toNotify.size());
        return toNotify;
    }

    private void sendNotification(ArrayList<CFPost> posts) {
        // check permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "sendNotification: notification permission not granted, leaving");
            return;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = 2000;

        for (CFPost post : posts) {
            Log.d(TAG, "sendNotification: " + post.title);
            NotificationCompat.Builder builder = post.createNotification(context);
            notificationManager.notify(notificationId, builder.build());
            notificationId++;
        }

        if (posts.size() > 1) {
            @SuppressLint("DefaultLocale") NotificationCompat.Builder summary = new NotificationCompat.Builder(context, NotificationManager.CHANNEL_ID_BLOG_POST)
                    .setContentTitle(String.format("%d New Blog Post", posts.size()))
                    .setContentText("")
                    .setSmallIcon(R.drawable.ic_coldcloud)
                    .setStyle(new NotificationCompat.InboxStyle().setSummaryText("Blog Posts"))
                    .setGroup(NotificationManager.GROUP_BLOG_POST)
                    .setGroupSummary(true);
            notificationManager.notify(0, summary.build());
        }

        // set the new last post
        if (posts.size() >= 1) AppParameter.setLastPost(context, posts.get(0).url);
    }
}
