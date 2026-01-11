package uk.redcode.flarex.params;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.redcode.flarex.R;
import uk.redcode.flarex.activity.LogsActivity;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamLogs extends Param {

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_logs, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.application_logs);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.application_logs_description);
        root.setOnClickListener(view -> showLogs());

        parent.addView(root);
    }

    private void showLogs() {
        Intent intent = new Intent(context, LogsActivity.class);
        context.startActivity(intent);
    }
}
