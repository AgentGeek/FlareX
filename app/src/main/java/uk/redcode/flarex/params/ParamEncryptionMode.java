package uk.redcode.flarex.params;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class ParamEncryptionMode extends Param implements CompoundButton.OnCheckedChangeListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/ssl
    private static final String KEY = "ssl";
    private static final String TAG = "Param-SSL";

    public static final String OFF = "off";
    public static final String FLEXIBLE = "flexible";
    public static final String FULL = "full";
    public static final String STRICT = "strict";

    private RadioButton off;
    private RadioButton flexible;
    private RadioButton full;
    private RadioButton strict;

    private ImageView flexibleImg;
    private ImageView fullImg;
    private ImageView strictImg;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_ssl, parent, false);
        super.onDraw(root, zone);

        off = root.findViewById(R.id.ssl_off);
        flexible = root.findViewById(R.id.ssl_flexible);
        full = root.findViewById(R.id.ssl_full);
        strict = root.findViewById(R.id.ssl_full_strict);

        flexibleImg = root.findViewById(R.id.ssl_flexible_img);
        fullImg = root.findViewById(R.id.ssl_full_img);
        strictImg = root.findViewById(R.id.ssl_strict_img);

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
                    String state = body.getString("value");
                    setSelected(state);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setError(true);
                }
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                setError(true);
            }
        });
    }


    private void setSelected(String state) {
        unregisterAll();
        switch (state) {
            case OFF:
                off.setChecked(true);
                break;
            case FLEXIBLE:
                flexible.setChecked(true);
                break;
            case FULL:
                full.setChecked(true);
                break;
            case STRICT:
                strict.setChecked(true);
                break;
            default:
                Toast.makeText(context, "Invalid state: "+state, Toast.LENGTH_SHORT).show();
                setError(true);
                break;
        }
        registerAll();
        updateView(state);
    }

    private void updateView(String state) {
        unregisterAllImg();
        switch (state) {
            case OFF:
                return;
            case FLEXIBLE:
                flexibleImg.setVisibility(View.VISIBLE);
                return;
            case FULL:
                flexibleImg.setVisibility(View.VISIBLE);
                fullImg.setVisibility(View.VISIBLE);
                return;
            case STRICT:
                flexibleImg.setVisibility(View.VISIBLE);
                fullImg.setVisibility(View.VISIBLE);
                strictImg.setVisibility(View.VISIBLE);
                return;
            default:
                Toast.makeText(context, "Invalid state: "+state, Toast.LENGTH_SHORT).show();
                setError(true);
        }
    }

    private void unregisterAllImg() {
        flexibleImg.setVisibility(View.INVISIBLE);
        fullImg.setVisibility(View.INVISIBLE);
        strictImg.setVisibility(View.INVISIBLE);
    }


    private void unregisterAll() {
        off.setOnCheckedChangeListener(null);
        off.setChecked(false);
        flexible.setOnCheckedChangeListener(null);
        flexible.setChecked(false);
        full.setOnCheckedChangeListener(null);
        full.setChecked(false);
        strict.setOnCheckedChangeListener(null);
        strict.setChecked(false);
    }

    private void registerAll() {
        off.setOnCheckedChangeListener(this);
        flexible.setOnCheckedChangeListener(this);
        full.setOnCheckedChangeListener(this);
        strict.setOnCheckedChangeListener(this);
    }

    private String getSateByView(int id) {
        if (id == R.id.ssl_off) return OFF;
        else if (id == R.id.ssl_flexible) return FLEXIBLE;
        else if (id == R.id.ssl_full) return FULL;
        else if (id == R.id.ssl_full_strict) return STRICT;
        else {
            setError(true);
            Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT).show();
            return "0";
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        unregisterAll();
        String newState = getSateByView(buttonView.getId());
        if (newState.equals("0")) return;

        Log.d(TAG, "onCheckedChanged: " + newState);
        setLoading(true);
        setSetting(KEY, newState, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                // arrived here it should be good;
                setSelected(newState);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setError(true);
                setLoading(false);
            }
        });
    }

}
