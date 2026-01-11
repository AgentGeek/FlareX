package uk.redcode.flarex.object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;

public class Param {

    public ProgressBar progress = null;
    public ImageView errorIcon = null;
    public Chip beta = null;

    public String requiredPlan = Zone.PLAN_FREE;

    public Context context;
    public Zone zone = null;
    public CFAccount account = null;

    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone, CFAccount account) {
        this.account = account;
        onDraw(inflater, parent, zone);
    }

    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        this.zone = zone;
        this.context = parent.getContext();
    }

    public void onDraw(View view, Zone zone) {
        this.zone = zone;
        this.context = view.getContext();
        this.progress = view.findViewById(R.id.param_progress);
        this.progress.setVisibility(View.INVISIBLE);
        this.errorIcon = view.findViewById(R.id.param_error);
        this.errorIcon.setVisibility(View.INVISIBLE);
        this.beta = view.findViewById(R.id.param_beta);
    }

    public void refresh() {
        // async
    }

    public void setLoading(boolean loading) {
        if (progress == null) return;
        progress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    public void setError(boolean show) {
        if (errorIcon == null) return;
        errorIcon.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        setLoading(false);
    }

    public void setBeta(boolean show) {
        if (beta == null) return;
        beta.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void getSetting(String key, CFApi.JSONListener listener) {
        CFApi.getSetting(context, zone.zoneId, key, listener);
    }

    public void setSetting(String key, String value, CFApi.JSONListener listener) {
        CFApi.setSetting(context, zone, key, value, listener, "value", requiredPlan);
    }
    public void setSetting(String key, int value, CFApi.JSONListener listener) {
        CFApi.setSetting(context, zone, key, value, listener, "value", requiredPlan);
    }
    public void setSetting(String key, JSONObject value, CFApi.JSONListener listener) {
        CFApi.setSetting(context, zone, key, value, listener, "value", requiredPlan);
    }
    public void setSetting(String key, String value, CFApi.JSONListener listener, String dataKey) {
        CFApi.setSetting(context, zone, key, value, listener, dataKey, requiredPlan);
    }
    public void setSetting(String key, boolean value, CFApi.JSONListener listener, String dataKey) {
        CFApi.setSetting(context, zone, key, value, listener, dataKey, requiredPlan);
    }

    public void reverse(SwitchMaterial sw, SwitchMaterial.OnCheckedChangeListener listener) {
        sw.setOnCheckedChangeListener(null);
        sw.setChecked(!sw.isChecked());
        sw.setOnCheckedChangeListener(listener);
    }
}
