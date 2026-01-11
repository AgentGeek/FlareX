package uk.redcode.flarex.module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentDashboard;

public class TimeRange extends Module implements View.OnClickListener {

    public static final int TIME_24HOURS = 0;
    public static final int TIME_7DAYS = 1;
    public static final int TIME_30DAYS = 2;

    public final ArrayList<Chip> chips = new ArrayList<>();
    public int selected = 0;

    public TimeRange(FragmentDashboard parent) {
        super(parent);
    }

    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.module_time_range, container, false);

        chips.add(view.findViewById(R.id.time_24hours));
        chips.add(view.findViewById(R.id.time_7days));
        chips.add(view.findViewById(R.id.time_30days));

        chips.get(0).setOnClickListener(this);
        chips.get(1).setOnClickListener(this);
        chips.get(2).setOnClickListener(this);

        setSelected(TIME_24HOURS, false);
        container.addView(view);
    }

    private void setSelected(int x, boolean trigger) {
        selected = x;
        for (int i = 0; i < chips.size(); i++) {
            chips.get(i).setSelected(x == i);
        }
        if (trigger) parent.refreshAnalytic();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.time_24hours) setSelected(TIME_24HOURS, true);
        else if (v.getId() == R.id.time_7days) setSelected(TIME_7DAYS, true);
        else if (v.getId() == R.id.time_30days) setSelected(TIME_30DAYS, true);
    }

    public long getRange() {
        return getRange(selected);
    }

    static public long getRange(int selected) {
        if (selected == TIME_7DAYS) {
            return (7 * 24 * 60 * 60 * 1000);
        } else if (selected == TIME_30DAYS) {
            return (30L * 24 * 60 * 60 * 1000);
        } else {
            return (24 * 60 * 60 * 1000);
        }
    }
}
