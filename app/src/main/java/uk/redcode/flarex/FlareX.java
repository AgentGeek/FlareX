package uk.redcode.flarex;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.ui.LayoutManager;
import uk.redcode.flarex.work.ImageManager;

public class FlareX extends Application {

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init();
        Logger.info(String.format("FlareX Starting ..."));
        ImageManager.start(this);
        LayoutManager.init(this);
    }
}
