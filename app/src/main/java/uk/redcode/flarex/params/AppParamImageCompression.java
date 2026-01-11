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

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Zone;

public class AppParamImageCompression extends uk.redcode.flarex.object.Param implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.image_compression);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.image_compression_desc);

        spinner = root.findViewById(R.id.param_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.image_compression, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(true);
        spinner.setOnItemSelectedListener(null);

        int p = AppParameter.getInt(context, AppParameter.IMAGE_COMPRESSION, 70);
        spinner.setSelection( (p/10) -1);
        spinner.setOnItemSelectedListener(this);
        setLoading(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setLoading(true);
        
        int p = (position+1)*10;
        Log.d("HERE", "onItemSelected: CHANGE TO -> "+p);
        if (p > 100 || p < 10) {
            Toast.makeText(context, "Error: value not in range", Toast.LENGTH_SHORT).show();
            return;
        }

        AppParameter.setInt(context, AppParameter.IMAGE_COMPRESSION, p);
        setLoading(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}
