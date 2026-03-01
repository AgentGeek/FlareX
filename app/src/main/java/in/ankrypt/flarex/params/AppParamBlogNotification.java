package in.ankrypt.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import in.ankrypt.flarex.R;
import in.ankrypt.flarex.object.AppParameter;
import in.ankrypt.flarex.object.Param;
import in.ankrypt.flarex.object.Zone;
import in.ankrypt.flarex.work.WorkerManager;

public class AppParamBlogNotification extends Param implements CompoundButton.OnCheckedChangeListener {

    private SwitchMaterial paramSwitch;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.blog_notification);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.blog_notification_description);
        paramSwitch = root.findViewById(R.id.param_switch);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        boolean value = AppParameter.getBoolean(context, AppParameter.BLOG_NOTIFICATION, false);
        paramSwitch.setOnCheckedChangeListener(null);
        paramSwitch.setChecked(value);
        paramSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        paramSwitch.setOnCheckedChangeListener(null);
        setLoading(true);

        AppParameter.setBoolean(context, AppParameter.BLOG_NOTIFICATION, isChecked);
        if (isChecked) AppParameter.setLastZone(context, zone);
        paramSwitch.setOnCheckedChangeListener(this);
        setLoading(false);

        // reload worker
        if (isChecked) WorkerManager.init(context);
    }
}
