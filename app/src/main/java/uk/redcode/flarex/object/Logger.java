package uk.redcode.flarex.object;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import uk.redcode.flarex.BuildConfig;

public class Logger {

    public static final ArrayList<String> logs = new ArrayList<>();
    private static long timeStart = 0;

    private static final String TAG = "LOGGER";

    private static final String INFO = "[INFO]";
    private static final String WARNING = "[WARNING]";
    private static final String ERROR = "[ERROR]";
    private static final String NETWORK = "[NETWORK]";

    public static void init() {
        timeStart = Calendar.getInstance().getTimeInMillis();
    }

    @SuppressLint("DefaultLocale")
    private static String getTime() {
        return String.format("[%d ms]", Calendar.getInstance().getTimeInMillis() - timeStart);
    }

    /*
        INFO
     */

    public static void info(String s) {
        String log = String.format("%s %s %s", INFO, getTime(), s);
        logs.add(log);
        if (BuildConfig.DEBUG) Log.d(TAG, log);
    }

    public static void info(String tag, String s) {
        info(String.format("{%s} %s", tag, s));
    }

    /*
        Network
     */

    public static void network(String s) {
        String log = String.format("%s %s %s", NETWORK, getTime(), s);
        logs.add(log);
        if (BuildConfig.DEBUG) Log.d(TAG, log);
    }

    public static void network(String tag, String s) {
        network(String.format("{%s} %s", tag, s));
    }

    /*
        WARNING
     */

    public static void warning(String s) {
        String log = String.format("%s %s %s", WARNING, getTime(), s);
        logs.add(log);
        if (BuildConfig.DEBUG) Log.w(TAG, log);
    }

    public static void warning(String tag, String s) {
        warning(String.format("{%s} %s", tag, s));
    }

    /*
        Error
     */

    public static void error(Exception e) {
        String log = String.format("%s %s %s %s", ERROR, getTime(), e.getMessage(), e);
        logs.add(log);
        if (BuildConfig.DEBUG) e.printStackTrace();
    }
}