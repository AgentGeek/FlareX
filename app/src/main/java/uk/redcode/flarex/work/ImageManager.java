package uk.redcode.flarex.work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Logger;

public class ImageManager {

    private static final String TAG = "ImageManager";
    private static Executor executor = null;

    public static void start(Context context) {
        try {
            executor = Executors.newFixedThreadPool(6);

            String path = context.getExternalMediaDirs()[0].getPath() + "/img/";
            File folder = new File(path);
            if (!folder.isDirectory() || !folder.exists()) {
                Logger.info(TAG, String.format("%s, not found creating ...", folder.getAbsolutePath()));
                folder.mkdir();
            } else {
                Logger.info(TAG, String.format("%s, found", folder.getAbsolutePath()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e);
        }
    }

    private static void execute(Builder builder) {
        // running on background

        // try load bitmap from cache
        builder.bitmap = loadFromCache(builder);

        // download image if null
        if (builder.bitmap == null) {
            Logger.info(TAG, String.format("%s not found in cache, downloading ...", builder.id));
            builder.bitmap = downloadImage(builder.source);
            saveImage(builder, builder.bitmap);
        }

        // set bitmap to image
        builder.activity.runOnUiThread(() -> builder.imageView.setImageBitmap(builder.bitmap));
    }

    /*
        Load from cache
     */

    private static Bitmap loadFromCache(Builder builder) {
        try {
            File file = new File(builder.activity.getExternalMediaDirs()[0].getPath() + "/img/" + builder.id);
            if (!file.exists()) return null;
            return BitmapFactory.decodeFile(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        Download from internet
     */

    private static Bitmap downloadImage(String source) {
        try {
            java.net.URL url = new java.net.URL(source);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveImage(Builder builder, Bitmap bitmap) {
        try {
            int quality = AppParameter.getInt(builder.activity, AppParameter.IMAGE_COMPRESSION, 70);
            File file = new File(builder.activity.getExternalMediaDirs()[0].getPath() + "/img/" + builder.id);
            Logger.info(String.format("save image: %s", file.getPath()));

            if (!file.exists()) file.createNewFile();
            FileOutputStream out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Builder
     */

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder implements Runnable {

        public String id = "";
        public String source = "";
        public ImageView imageView = null;
        public Bitmap bitmap = null;
        public final Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder load(String url) {
            this.source = url;
            this.id = getId();
            Logger.info(TAG, String.format("From id: %s -> %s", source, id));
            return this;
        }

        public Builder into(ImageView view) {
            this.imageView = view;
            return this;
        }

        private String getId() {
            String id = source.replace("://", "");
            id = id.replace("https", "");
            id = id.replace("http", "");
            id = id.replace("/", "_");
            id = id.replace("?", "__");
            return id;
        }

        public void run() {
            executor.execute(() -> ImageManager.execute(this));
        }
    }

}
