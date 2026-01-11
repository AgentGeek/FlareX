package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Parser;

public class FragmentIPS extends FragmentCC {

    private ChipGroup ipv4;
    private ChipGroup ipv6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ips, container, false);
        ipv4 = root.findViewById(R.id.ip_v4_group);
        ipv6 = root.findViewById(R.id.ip_v6_group);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
    }

    private void update() {
        setLoading(true);
        CFApi.getCloudflareIPs(requireContext(), new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                ArrayList<String> list_v4 = Parser.parseStringList(body.getJSONObject("result").getJSONArray("ipv4_cidrs"));
                ArrayList<String> list_v6 = Parser.parseStringList(body.getJSONObject("result").getJSONArray("ipv6_cidrs"));
                buildChips(ipv4, list_v4);
                buildChips(ipv6, list_v6);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void buildChips(ChipGroup container, ArrayList<String> list) {
        container.removeAllViews();
        for (String ip : list) {
            Chip chip = new Chip(requireContext());
            chip.setText(ip);
            container.addView(chip);
        }
    }

}
