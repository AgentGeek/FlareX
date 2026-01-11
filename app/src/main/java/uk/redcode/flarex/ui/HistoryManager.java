package uk.redcode.flarex.ui;

import android.widget.Toast;

import java.util.ArrayList;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Logger;

public class HistoryManager {

    final ArrayList<History> histories = new ArrayList<>();
    private final ViewManager viewManager;
    private boolean nextBackExit = false;

    private static final String TAG = "HistoryManager";
    public static final int MAX = 7;

    public HistoryManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void push(History history) {
        Logger.info(TAG, "Push last view: "+history.view);
        if (histories.size() == 0) {
            histories.add(history);
            nextBackExit = false;
            return;
        }

        // check if view is equal
        if (histories.get(histories.size()-1).view.equals(history.view)) {
            Logger.info(TAG, "Pushing the same view, not pushed");
            return;
        }

        if (histories.size() > MAX) histories.remove(0);

        histories.add(history);
        nextBackExit = false;
    }

    public void back(MainActivity activity) {
        if (histories.size() == 0) {
            if (!nextBackExit) {
                Toast.makeText(activity, R.string.back_for_exit, Toast.LENGTH_SHORT).show();
                nextBackExit = true;
            } else {
                Logger.info(TAG, "Leave activity from double back");
                activity.finish();
            }
            return;
        }

        History last = histories.get(histories.size()-1);
        viewManager.setView(last.view, last.data, false);
        histories.remove(histories.size()-1);
        nextBackExit = false;
    }

    public void print() {
        StringBuilder str = new StringBuilder();
        str.append("[ ");
        for (History h : histories) {
            str.append(h.view).append(", ");
        }
        str.append("]");
    }

    public static class History {

        final String view;
        Object data = null;

        public History(String view) {
            this.view = view;
        }

        public History(String view, Object data) {
            this.view = view;
            this.data = data;
        }
    }

}
