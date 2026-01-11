package uk.redcode.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Zone;

public class ParamMaximumUpload extends uk.redcode.flarex.object.Param implements AdapterView.OnItemSelectedListener {

    // GET ??
    private static final String KEY = "";
    private static final String TAG = "Param-MaximumUpload";

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.maximum_upload);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.maximum_upload_description);

        Spinner spinner = root.findViewById(R.id.param_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.maximum_upload, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(false);
        setError(true);

        /*getSetting(KEY, (success, body) -> {
            setLoading(false);
            if (!success) {
                setError(true);
                return;
            }

            Log.d(TAG, "onResult: "+body.toString());
            setError(true);
        });*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // avoid user being able to spam it
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}
