package uk.redcode.flarex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.ZoneAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Zone;

public class FragmentZoneSelector extends FragmentCC {

    private ArrayList<Zone> zones = new ArrayList<>();
    private RecyclerView recycler;

    private boolean refreshing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_zone_selector, container, false);
        recycler = root.findViewById(R.id.recycler);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (!refreshing) updateList();
    }

    private void updateList() {
        refreshing = true;
        setLoading(true);
        CFApi.getZones(getMain(), new CFApi.ZoneListener() {
            @Override
            public void onResult(ArrayList<Zone> zones) {
                FragmentZoneSelector.this.zones = zones;
                displayList();
                refreshing = false;
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                refreshing = false;
                setLoading(false);
            }
        });
    }

    private void displayList() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ZoneAdapter adapter = new ZoneAdapter(getContext(), zones);
        adapter.setListener(zone -> {
            if (zone.status.equals("pending")) {
                Toast.makeText(getContext(), R.string.please_wait_active, Toast.LENGTH_LONG).show();
                return;
            }

            // force reload of dashboard
            requireContext().getSharedPreferences("DASHBOARD", Context.MODE_PRIVATE).edit().putBoolean("force_update", true).apply();
            // set zone
            changeZone(zone);
        });
        recycler.setAdapter(adapter);
        setLoading(false);
    }
}
