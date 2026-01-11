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

public class ParamCacheTTL extends uk.redcode.flarex.object.Param implements AdapterView.OnItemSelectedListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/browser_cache_ttl
    private static final String KEY = "browser_cache_ttl";
    private static final String TAG = "Param-CacheTTL";
    private Spinner spinner;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.browser_cache_ttl);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.browser_cache_ttl_description);

        spinner = root.findViewById(R.id.param_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.browser_cache_ttl, android.R.layout.simple_spinner_item);
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
                    int value = body.getInt("value");
                    spinner.setSelection(getPosition(value));
                    spinner.setOnItemSelectedListener(ParamCacheTTL.this);
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

    private int getPosition(int value) {
        switch (value) {
            case 0: return 0; // respect header
            case 30: return 1; // 30s
            case 60: return 2; // 1m
            case 120: return 3; // 2m
            case 300: return 4; // 5m
            case 1200: return 5; // 20m
            case 1800: return 6; // 30m
            case 3600: return 7; // 1h
            case 7200: return 8; // 2h
            case 10800: return 9; // 3h
            case 14400: return 10; // 4h
            case 18000: return 11; // 5h
            case 28800: return 12; // 8h
            case 43200: return 13; // 12h
            case 57600: return 14; // 16h
            case 72000: return 15; // 20h
            case 86400: return 16; // 1d
            case 172800: return 17; // 2d
            case 259200: return 18; // 3d
            case 345600: return 19; // 4d
            case 432000: return 20; // 5d
            case 691200: return 21; // 8d
            case 1382400: return 22; // 16d
            case 2073600: return 23; // 24d
            case 2678400: return 24; // 31d
            case 5356800: return 25; // 2 Months
            case 16070400: return 26; // 6 Months
            case 31536000: return 27; // 1 y
            default:
                Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show();
                return 0;
        }
    }

    private int getValueByPosition(int position) {
        switch (position) {
            case 0: return 0; // respect header
            case 1: return 30; // 30s
            case 2: return 60; // 1m
            case 3: return 120; // 2m
            case 4: return 300; // 5m
            case 5: return 1200; // 20m
            case 6: return 1800; // 30m
            case 7: return 3600; // 1h
            case 8: return 7200; // 2h
            case 9: return 10800; // 3h
            case 10: return 14400; // 4h
            case 11: return 18000; // 5h
            case 12: return 28800; // 8h
            case 13: return 43200; // 12h
            case 14: return 57600; // 16h
            case 15: return 72000; // 20h
            case 16: return 86400; // 1d
            case 17: return 172800; // 2d
            case 18: return 259200; // 3d
            case 19: return 345600; // 4d
            case 20: return 432000; // 5d
            case 21: return 691200; // 8d
            case 22: return 1382400; // 16d
            case 23: return 2073600; // 24d
            case 24: return 2678400; // 31d
            case 25: return 5356800; // 2 Months
            case 26: return 16070400; // 6 Months
            case 27: return 31536000; // 1 y
            default:
                Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show();
                return -1;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // avoid user being able to spam it
        spinner.setOnItemSelectedListener(null);

        int newValue = getValueByPosition(position);
        if (newValue == -1) return;

        setLoading(true);
        setSetting(KEY, newValue, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                spinner.setOnItemSelectedListener(ParamCacheTTL.this);
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
