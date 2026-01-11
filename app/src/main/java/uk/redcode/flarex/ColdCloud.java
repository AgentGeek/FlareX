package uk.redcode.flarex;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.ui.LayoutManager;
import uk.redcode.flarex.work.ImageManager;

public class ColdCloud extends Application {

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();
        initCrashHandler();
        Logger.init();
        Logger.info(String.format("Coldcloud %s [%d] Starting ...", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        ImageManager.start(this);
        LayoutManager.init(this);
    }

    private void initCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.d("MainThread", "uncaughtException: APPLICATION CRASH !");
            throwable.printStackTrace();
            System.exit(1);
        });
    }

}
