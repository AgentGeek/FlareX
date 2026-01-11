package uk.redcode.flarex.params;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Zone;

public class ParamCachingLevel extends uk.redcode.flarex.object.Param implements AdapterView.OnItemSelectedListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/cache_level

    // Standard -> aggressive
    // Ignore query string -> simplified
    // No query string -> basic

    private static final String KEY = "cache_level";
    private static final String TAG = "Param-CachingLevel";
    private Spinner spinner;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.cache_level);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.cache_level_description);

        spinner = root.findViewById(R.id.param_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.cache_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(true);
        spinner.setOnItemSelectedListener(null);

        getSetting(KEY, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                try {
                    String value = body.getString("value");
                    spinner.setOnItemSelectedListener(null);
                    spinner.setSelection(getPosition(value));
                    spinner.setOnItemSelectedListener(ParamCachingLevel.this);
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

    private int getPosition(String value) {
        switch (value) {
            case "aggressive":
                return 0;
            case "simplified":
                return 1;
            case "basic":
                return 2;
            default:
                Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show();
                return 0;
        }
    }

    private String getValueByPosition(int position) {
        switch (position) {
            case 0:
                return "aggressive";
            case 1:
                return "simplified";
            case 2:
                return "basic";
            default:
                Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show();
                setError(true);
                return "0";
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // avoid user being able to spam it
        spinner.setOnItemSelectedListener(null);

        String newValue = getValueByPosition(position);
        if (newValue.equals("0")) return;

        setLoading(true);
        setSetting(KEY, newValue, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                spinner.setOnItemSelectedListener(ParamCachingLevel.this);
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
    public void onNothingSelected(AdapterView<?> parent) {}

}
