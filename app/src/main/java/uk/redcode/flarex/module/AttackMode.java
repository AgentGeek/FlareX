package uk.redcode.flarex.module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Zone;
import uk.redcode.flarex.ui.Alert;
import uk.redcode.flarex.ui.LayoutManager;

public class AttackMode extends Module implements CompoundButton.OnCheckedChangeListener {

    private static final String KEY = "security_level";

    private String actualStatus = "medium";
    private ProgressBar progress;
    private ImageView errorImage;
    private SwitchMaterial attackMode;

    public AttackMode(FragmentDashboard parent) {
        super(parent);
    }

    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        if (!verifyLayout(false)) return;
        View view = inflater.inflate(R.layout.module_attack_mode, container, false);

        this.progress = view.findViewById(R.id.attack_mode_loading);
        this.attackMode = view.findViewById(R.id.attack_mode_switch);
        this.errorImage = view.findViewById(R.id.attack_mode_error);

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
                    attackMode.setEnabled(verifyLayout(true));
                    attackMode.setOnCheckedChangeListener(null);

                    actualStatus = body.getString("value");
                    // save last security status
                    if (parent.isAdded()) {
                        if (!actualStatus.equals("under_attack")) AppParameter.saveLastZoneSecurity(parent.requireContext(), parent.zone, actualStatus);
                    }

                    boolean enable = body.getString("value").equals("under_attack");
                    attackMode.setChecked(enable);
                    attackMode.setOnCheckedChangeListener(AttackMode.this);
                    errorImage.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    parent.alert(new Alert(Alert.ERROR, e.getMessage()));
                    errorImage.setVisibility(View.VISIBLE);
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                progress.setVisibility(View.INVISIBLE);
                attackMode.setEnabled(false);
                errorImage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        // avoid user being able to spam it
        attackMode.setOnCheckedChangeListener(null);
        progress.setVisibility(View.VISIBLE);

        String savedStatus = AppParameter.getLastZoneSecurity(parent.requireContext(), parent.zone);
        if (savedStatus.equals("null")) savedStatus = "medium";
        String newStatus = actualStatus.equals("under_attack") ? savedStatus : "under_attack";


        CFApi.setSetting(parent.getMain(), parent.zone, KEY, newStatus, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                if (!newStatus.equals("under_attack")) AppParameter.saveLastZoneSecurity(parent.requireContext(), parent.zone, newStatus);
                actualStatus = newStatus;

                attackMode.setChecked(isChecked);
                attackMode.setOnCheckedChangeListener(AttackMode.this);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                parent.alert(new Alert(Alert.ERROR, "Error changing under attack mode"));
                progress.setVisibility(View.INVISIBLE);
            }
        }, "value", Zone.PLAN_FREE);
    }
}
