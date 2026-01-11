package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.AuditLogAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.AuditLog;

public class FragmentHistory extends FragmentCC {

    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler, container, false);
        recycler = (RecyclerView) root;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
    }

    private void update() {
        setLoading(true);
        CFApi.getAuditLogs(requireContext(), getAccountId(), new CFApi.AuditLogListener() {
            @Override
            public void onResult(ArrayList<AuditLog> logs) {
                drawLogs(logs);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void drawLogs(ArrayList<AuditLog> logs) {
        AuditLogAdapter adapter = new AuditLogAdapter(requireContext(), logs);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);
    }
}
