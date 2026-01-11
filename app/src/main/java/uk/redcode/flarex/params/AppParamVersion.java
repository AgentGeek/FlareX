package uk.redcode.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.redcode.flarex.BuildConfig;
import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamVersion extends Param {

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_version, parent, false);

        TextView version = root.findViewById(R.id.app_version);
        version.setText(BuildConfig.VERSION_NAME);

        parent.addView(root);
    }

}
