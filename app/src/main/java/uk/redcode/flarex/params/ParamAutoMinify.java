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

public class ParamAutoMinify extends uk.redcode.flarex.object.Param implements CompoundButton.OnCheckedChangeListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/minify
    private static final String KEY = "minify";
    private static final String TAG = "Param-Minify";
    private SwitchMaterial paramJavascript;
    private SwitchMaterial paramCSS;
    private SwitchMaterial paramHTML;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_minify, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.auto_minify);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.auto_minify_description);
        paramJavascript = root.findViewById(R.id.param_minify_javascript);
        paramCSS = root.findViewById(R.id.param_minify_css);
        paramHTML = root.findViewById(R.id.param_minify_html);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(true);
        unregisterAll();

        getSetting(KEY, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onResult: "+body.toString());
                try {
                    JSONObject values = body.getJSONObject("value");
                    updateSwitchStatus(values);
                    registerAll();
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

    private void updateSwitchStatus(JSONObject values) throws JSONException {
        paramJavascript.setChecked(Parser.parseBoolean(values.getString("js")));
        paramCSS.setChecked(Parser.parseBoolean(values.getString("css")));
        paramHTML.setChecked(Parser.parseBoolean(values.getString("html")));
    }

    private JSONObject getSwitchValues() throws JSONException {
        JSONObject v = new JSONObject();
        v.put("js", Parser.convertBoolean(paramJavascript.isChecked()));
        v.put("css", Parser.convertBoolean(paramCSS.isChecked()));
        v.put("html", Parser.convertBoolean(paramHTML.isChecked()));
        return v;
    }

    private void unregisterAll() {
        paramJavascript.setOnCheckedChangeListener(null);
        paramCSS.setOnCheckedChangeListener(null);
        paramHTML.setOnCheckedChangeListener(null);
    }

    private void registerAll() {
        paramJavascript.setOnCheckedChangeListener(this);
        paramCSS.setOnCheckedChangeListener(this);
        paramHTML.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        unregisterAll();
        setLoading(true);

        CFApi.JSONListener listener = new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: " + body.toString());
                try {
                    JSONObject values = body.getJSONObject("value");
                    updateSwitchStatus(values);
                    registerAll();
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
        };

        try {
            JSONObject newValues = getSwitchValues();
            setSetting(KEY, newValues, listener);
        } catch (JSONException e) {
            e.printStackTrace();
            setError(true);
            setLoading(false);
        }
    }



}
