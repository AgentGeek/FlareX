package uk.redcode.flarex.params;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamSendCrash extends Param implements CompoundButton.OnCheckedChangeListener {

    private SwitchMaterial paramSwitch;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.send_crash);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.always_send_the_crash_report);
        paramSwitch = root.findViewById(R.id.param_switch);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        boolean selected = context.getSharedPreferences("CRASH", Context.MODE_PRIVATE).getBoolean("always_send", false);
        paramSwitch.setOnCheckedChangeListener(null);
        paramSwitch.setChecked(selected);
        paramSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        paramSwitch.setOnCheckedChangeListener(null);
        setLoading(true);

        context.getSharedPreferences("CRASH", Context.MODE_PRIVATE).edit().putBoolean("always_send", isChecked).apply();

        paramSwitch.setOnCheckedChangeListener(this);
        setLoading(false);
    }
}
