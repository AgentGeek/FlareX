package uk.redcode.flarex.params;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamTheme extends Param implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    Activity activity = null;

    public AppParamTheme setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_select, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.app_theme);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.app_theme_description);

        spinner = root.findViewById(R.id.param_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.available_theme, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(true);

        int value = AppParameter.getInt(context, AppParameter.THEME, 0);
        spinner.setOnItemSelectedListener(null);
        spinner.setSelection(value);
        spinner.setOnItemSelectedListener(this);
        setLoading(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("HERE", "onItemSelected: pos: "+position);
        Log.d("HERE", "onItemSelected: SharedPref: "+AppParameter.getInt(context, AppParameter.THEME, 0));


        if (position == AppParameter.getInt(context, AppParameter.THEME, 0)) return;

        // avoid user being able to spam it
        spinner.setOnItemSelectedListener(null);
        setLoading(true);
        AppParameter.setInt(context, AppParameter.THEME, position);

        //updateTheme();
        //context.setTheme(getTheme(position));*

        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);

        spinner.setOnItemSelectedListener(this);
        setLoading(false);
    }

    static public int getTheme(int position) {
        switch (position) {
            case 1: return R.style.Theme_FlareX_Amoled;
            case 0: default: return R.style.Theme_FlareX;
        }
    }

    public static int getNightMode(int position) {
        switch (position) {
            case 2: return AppCompatDelegate.MODE_NIGHT_NO;
            case 1: case 0: default: return AppCompatDelegate.MODE_NIGHT_YES;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}