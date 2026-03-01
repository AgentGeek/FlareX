package in.ankrypt.flarex.ui;

import android.view.View;

import androidx.annotation.Nullable;

import in.ankrypt.flarex.MainActivity;
import in.ankrypt.flarex.R;
import in.ankrypt.flarex.object.AppParameter;
import in.ankrypt.flarex.object.Logger;
import in.ankrypt.flarex.object.Zone;

public class ZoneManager {

    private final static String TAG = "ZoneManager";
    private final MainActivity activity;
    public Zone selected = null;

    public View.OnClickListener onShootListener = null;

    public ZoneManager(MainActivity activity) {
        this.activity = activity;
        load();

        activity.toolbarIcon.setOnClickListener(v -> {
            if (onShootListener != null) onShootListener.onClick(v);
            onShootListener = null;
        });

        updateLabel();
    }

    private void load() {
        if (!AppParameter.getBoolean(activity, AppParameter.REMEMBER_ZONE, false)) return;

        // try load zone
        Zone last = AppParameter.getLastZone(activity);
        if (last == null) {
            Logger.info("Enable to load remembered Zone, load null");
            activity.showAlert(new Alert(Alert.ERROR, R.string.unable_load_last_zone));
            return;
        }

        // set zone
        selected = last;
    }

    public void updateLabel() {
        if (selected == null) {
            activity.getSupportActionBar().setTitle(R.string.no_zone_selected);
            activity.getSupportActionBar().setIcon(null);
            return;
        }

        activity.setTitle(selected.name);
        activity.setToolbarIcon(selected.getStatusIcon(), null);
    }

    public void setZone(Zone zone) {
        Logger.info(TAG, "Set Zone: "+zone.zoneId);
        selected = zone;
        updateLabel();
        AppParameter.setLastZone(activity, zone);
        activity.bottomNav.setSelectedItemId(activity.bottomNav.getMenu().getItem(0).getItemId());
    }

    public void setIcon(int iconId, @Nullable View.OnClickListener listener) {
        activity.setToolbarIcon(iconId, null);
        this.onShootListener = listener;
    }

}
