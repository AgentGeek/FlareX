package uk.redcode.flarex.fragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.DNSRecordAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.DNSRecord;
import uk.redcode.flarex.ui.LayoutManager;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentDNS extends FragmentCC {

    private RecyclerView recycler;
    private DNSRecordAdapter adapter;
    private ArrayList<DNSRecord> records = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dns, container, false);

        recycler = root.findViewById(R.id.recycler);
        registerItemTouchHelper();

        return root;
    }

    private void registerItemTouchHelper() {
        if (!LayoutManager.get(LayoutManager.DNS_EDIT)) return;

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Log.d("DNS_SWIPE", "onSwiped: direction " + direction);
                Log.d("DNS_SWIPE", "onSwiped: position " + position);
                if (direction == ItemTouchHelper.LEFT) {
                    deleteDNSRecord(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    editDNSRecord(position);
                }
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((DNSRecordAdapter.ViewHolder) viewHolder).foreground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                updateBackground(viewHolder, dX);
                final View foregroundView = ((DNSRecordAdapter.ViewHolder) viewHolder).foreground;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((DNSRecordAdapter.ViewHolder) viewHolder).foreground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                updateBackground(viewHolder, dX);
                final View foregroundView = ((DNSRecordAdapter.ViewHolder) viewHolder).foreground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                return super.convertToAbsoluteDirection(flags, layoutDirection);
            }

            private void updateBackground(RecyclerView.ViewHolder viewHolder, float dX) {
                ((DNSRecordAdapter.ViewHolder) viewHolder).backgroundRight.setVisibility(dX < 0 ? View.VISIBLE : View.GONE);
                ((DNSRecordAdapter.ViewHolder) viewHolder).backgroundLeft.setVisibility(dX > 0 ? View.VISIBLE : View.GONE);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler);
    }

    private void editDNSRecord(int position) {
        DNSRecord record = records.get(position);
        setView(ViewManager.VIEW_ADD_DNS_RECORD, record);
    }

    private void deleteDNSRecord(int position) {
        setLoading(true);
        DNSRecord record = records.get(position);

        CFApi.deleteDNSRecord(getMain(), zone.zoneId, record, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                setLoading(false);
                adapter.remove(position);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                adapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateList();
    }

    private void updateList() {
        setLoading(true);
        CFApi.getDNSRecords(getMain(), zone.zoneId, new CFApi.DNSListener() {
            @Override
            public void onResult(ArrayList<DNSRecord> records) {
                FragmentDNS.this.records = records;
                displayList();
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void displayList() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DNSRecordAdapter(getContext(), records, zone);
        recycler.setAdapter(adapter);
        setLoading(false);
    }


}
