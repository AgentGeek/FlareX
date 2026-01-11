package uk.redcode.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import uk.redcode.flarex.R;
import uk.redcode.flarex.work.DailyStatsWorker;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamDailyStats extends Param implements CompoundButton.OnCheckedChangeListener {

    private SwitchMaterial paramSwitch;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.daily_stats);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.daily_stats_description);
        paramSwitch = root.findViewById(R.id.param_switch);

        parent.addView(root);
        setBeta(true);
    }

    @Override
    public void refresh() {
        boolean selected = DailyStatsWorker.isEnable(context, zone.zoneId);
        paramSwitch.setOnCheckedChangeListener(null);
        paramSwitch.setChecked(selected);
        paramSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        paramSwitch.setOnCheckedChangeListener(null);
        setLoading(true);
        DailyStatsWorker.setEnable(context, zone.zoneId, isChecked);
        DailyStatsWorker.saveName(context, zone);
        paramSwitch.setOnCheckedChangeListener(this);
        setLoading(false);
    }
}
