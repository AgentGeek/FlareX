package uk.redcode.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamRememberZone extends Param implements CompoundButton.OnCheckedChangeListener {

    private SwitchMaterial paramSwitch;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.remember_zone);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.remember_zone_description);
        paramSwitch = root.findViewById(R.id.param_switch);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        boolean value = AppParameter.getBoolean(context, AppParameter.REMEMBER_ZONE, false);
        paramSwitch.setOnCheckedChangeListener(null);
        paramSwitch.setChecked(value);
        paramSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        paramSwitch.setOnCheckedChangeListener(null);
        setLoading(true);

        AppParameter.setBoolean(context, AppParameter.REMEMBER_ZONE, isChecked);
        if (isChecked) AppParameter.setLastZone(context, zone);
        paramSwitch.setOnCheckedChangeListener(this);
        setLoading(false);
    }
}
