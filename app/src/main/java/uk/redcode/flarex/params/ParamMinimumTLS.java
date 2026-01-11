package uk.redcode.flarex.params;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class ParamMinimumTLS extends Param implements AdapterView.OnItemSelectedListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/min_tls_version
    private static final String KEY = "min_tls_version";
    private static final String TAG = "Param-MinimumTLS";
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.minimum_tls);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.minimum_tls_description);

        spinner = root.findViewById(R.id.param_spinner);
        adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.tls_version, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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
                    String value = body.getString("value");
                    int pos = adapter.getPosition(value);
                    spinner.setOnItemSelectedListener(null);
                    spinner.setSelection(pos);
                    spinner.setOnItemSelectedListener(ParamMinimumTLS.this);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // avoid user being able to spam it
        spinner.setOnItemSelectedListener(null);
        setLoading(true);

        setSetting(KEY, spinner.getSelectedItem().toString(), new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                spinner.setSelection(position);
                spinner.setOnItemSelectedListener(ParamMinimumTLS.this);
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
