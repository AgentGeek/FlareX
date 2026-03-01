package in.ankrypt.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.ankrypt.flarex.BuildConfig;
import in.ankrypt.flarex.R;
import in.ankrypt.flarex.object.Param;
import in.ankrypt.flarex.object.Zone;

public class AppParamVersion extends Param {

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_version, parent, false);

        TextView version = root.findViewById(R.id.app_version);
        version.setText(BuildConfig.VERSION_NAME);

        parent.addView(root);
    }

}
