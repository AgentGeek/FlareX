package uk.redcode.flarex.module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Zone;
import uk.redcode.flarex.ui.Alert;
import uk.redcode.flarex.ui.LayoutManager;

public class DevMode extends Module implements CompoundButton.OnCheckedChangeListener {

    public static final String KEY = "development_mode";

    private ProgressBar progress;
    private ImageView errorImage;
    private SwitchMaterial devMode;

    public DevMode(FragmentDashboard parent) {
        super(parent);
    }

    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        if (!verifyLayout(false)) return;
        View view = inflater.inflate(R.layout.module_dev_mode, container, false);

        this.progress = view.findViewById(R.id.dev_mode_loading);
        this.devMode = view.findViewById(R.id.dev_mode_switch);
        this.errorImage = view.findViewById(R.id.dev_mode_error);

        container.addView(view);
    }

    public boolean verifyLayout(boolean edit) {
        return LayoutManager.get(edit ? LayoutManager.ZONE_CONFIG_EDIT : LayoutManager.ZONE_CONFIG);
    }

    public void refresh(FragmentDashboard parent) {
        if (!verifyLayout(false)) return;
        this.parent = parent;
        this.refresh();
    }

    @Override
    public void refresh() {
        progress.setVisibility(View.VISIBLE);

        CFApi.getSetting(parent.getMain(), parent.zone.zoneId, KEY, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                try {
                    devMode.setEnabled(verifyLayout(true));
                    devMode.setOnCheckedChangeListener(null);
                    boolean enable = Parser.parseBoolean(body.getString("value"));
                    devMode.setChecked(enable);
                    devMode.setOnCheckedChangeListener(DevMode.this);
                    errorImage.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(parent.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    errorImage.setVisibility(View.INVISIBLE);
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                progress.setVisibility(View.INVISIBLE);
                devMode.setEnabled(false);
                errorImage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        devMode.setOnCheckedChangeListener(null);
        progress.setVisibility(View.VISIBLE);

        CFApi.setSetting(parent.getMain(), parent.zone, KEY, Parser.convertBoolean(isChecked), new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                devMode.setChecked(isChecked);
                devMode.setOnCheckedChangeListener(DevMode.this);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                progress.setVisibility(View.INVISIBLE);
                parent.alert(new Alert(Alert.ERROR, "Error changing dev mode"));
            }
        }, "value", Zone.PLAN_FREE);
    }
}
