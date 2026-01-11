package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.tabs.TabLayout;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CountryStat;

public class FragmentWorld extends FragmentCC {

    private CountryStat stat = null;
    // private FragmentMap mapFragment = null; // Map functionality removed
    private FragmentManager manager = null;
    private WorldTableFragment tableFragment = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup)  inflater.inflate(R.layout.fragment_world, container, false);
        manager = requireActivity().getSupportFragmentManager();
        tableFragment = new WorldTableFragment();
        // mapFragment = new FragmentMap(); // Map functionality removed

        ((TabLayout) root.findViewById(R.id.tabs_world)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setLoading(true);
                if (tab.getPosition() == 0) showTable();
                // else if (tab.getPosition() == 1) showMap(); // Map functionality removed
                else Toast.makeText(getContext(), "No Tab position:"+tab.getPosition(), Toast.LENGTH_LONG).show();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        showTable();
    }

    private void showTable() {
        if (!isVisible()) return;
        if (stat == null) {
            Toast.makeText(requireContext(), R.string.no_data, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!tableFragment.hasStat()) tableFragment.setStat(stat);
        manager.beginTransaction().replace(R.id.frame_world, tableFragment).commit();
        setLoading(false);
    }

    // private void showMap() { // Map functionality removed
    //    if (!isVisible()) return;
    //    if (stat == null) {
    //        Toast.makeText(requireContext(), R.string.no_data, Toast.LENGTH_SHORT).show();
    //        return;
    //    }
    //
    //    if (!mapFragment.hasStat()) mapFragment.setStat(stat);
    //    manager.beginTransaction().replace(R.id.frame_world, mapFragment).commit();
    // } // Map functionality removed

    public void setStat(CountryStat stat) {
        this.stat = stat;
    }

}