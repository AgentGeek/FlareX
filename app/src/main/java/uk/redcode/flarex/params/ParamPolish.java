package uk.redcode.flarex.params;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Zone;

public class ParamPolish extends uk.redcode.flarex.object.Param implements CompoundButton.OnCheckedChangeListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/polish
    private static final String KEY = "polish";
    private static final String TAG = "Param-Polish";
    private SwitchMaterial paramSwitch;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        requiredPlan = Zone.PLAN_PRO;
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.polish);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.polish_description);
        paramSwitch = root.findViewById(R.id.param_switch);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(true);

        getSetting(KEY, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onResult: "+body.toString());
                try {
                    paramSwitch.setOnCheckedChangeListener(null);
                    boolean enable = Parser.parseBoolean(body.getString("value"));
                    paramSwitch.setChecked(enable);
                    paramSwitch.setOnCheckedChangeListener(ParamPolish.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setError(true);
                }
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setError(true);
                setLoading(false);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        paramSwitch.setOnCheckedChangeListener(null);
        setLoading(true);

        setSetting(KEY, Parser.convertBoolean(isChecked), new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                // arrived here it should be good;
                paramSwitch.setChecked(isChecked);
                paramSwitch.setOnCheckedChangeListener(ParamPolish.this);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                reverse(paramSwitch, ParamPolish.this);
                setError(true);
                setLoading(false);
            }
        });
    }

}
