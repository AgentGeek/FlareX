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
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Zone;

public class ParamSSLRecommender extends Param implements CompoundButton.OnCheckedChangeListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/ssl_recommender
    private static final String KEY = "ssl_recommender";
    private static final String TAG = "Param-SSL/TLS-Recommender";
    private SwitchMaterial paramSwitch;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.ssl_tls_recommender);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.ssl_tls_recommender_description);
        paramSwitch = root.findViewById(R.id.param_switch);
        setBeta(true);

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
                    boolean enable = Parser.parseBoolean(body.getString("enabled"));
                    paramSwitch.setChecked(enable);
                    paramSwitch.setOnCheckedChangeListener(ParamSSLRecommender.this);
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

        setSetting(KEY, isChecked, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                // arrived here it should be good;
                paramSwitch.setChecked(isChecked);
                paramSwitch.setOnCheckedChangeListener(ParamSSLRecommender.this);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setError(true);
                setLoading(false);
            }
        }, "enabled");
    }
}
