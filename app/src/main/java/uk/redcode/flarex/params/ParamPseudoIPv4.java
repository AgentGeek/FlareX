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

public class ParamPseudoIPv4 extends uk.redcode.flarex.object.Param implements AdapterView.OnItemSelectedListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/settings/pseudo_ipv4
    private static final String KEY = "pseudo_ipv4";
    private static final String TAG = "Param-PseudoIPv4";
    private Spinner spinner;

    private String actualValue = "";

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);
        requiredPlan = Zone.PLAN_PRO;

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.pseudo_ipv4);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.pseudo_ipv4_description);

        spinner = root.findViewById(R.id.param_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.pseudo_ipv4, android.R.layout.simple_spinner_item);
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
                Log.d(TAG, "onResult: "+body.toString());
                try {
                    actualValue = body.getString("value");
                    spinner.setSelection(getPosition(actualValue));
                    spinner.setOnItemSelectedListener(ParamPseudoIPv4.this);
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

    private int getPosition(String value) {
        switch (value) {
            case "off":
                return 0;
            case "add_header":
                return 1;
            case "overwrite_header":
                return 2;
            default:
                Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show();
                return 0;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // verify required plan
        if (!zone.hasRequiredPlan(requiredPlan, context)) {
            spinner.setOnItemSelectedListener(null);
            spinner.setSelection(getPosition(actualValue));
            spinner.setOnItemSelectedListener(this);
            return;
        }

        // avoid user being able to spam it
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}
