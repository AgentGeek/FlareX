package uk.redcode.flarex.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ScrollView;

import uk.redcode.flarex.fragment.FragmentDashboard;

public class WorldMap extends Module {

    public WorldMap(FragmentDashboard parent, ScrollView scrollView) {
        super(parent, scrollView);
    }

    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        // Map functionality removed, nothing to draw.
    }

    public void update(Object stat) {
        // Map functionality removed, nothing to update.
    }

    public static void loadCountryList(Context context) {
        // Map functionality removed, no country list to load.
    }
}