package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.User;
import uk.redcode.flarex.adapter.DNSRecordAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Notification;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentNotifications extends FragmentCC {

    private RecyclerView recycler;
    private LinearLayout emptyContainer;
    private DNSRecordAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableBackView = true;
        this.lastView = ViewManager.VIEW_SETTINGS;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        recycler = root.findViewById(R.id.recycler);
        emptyContainer = root.findViewById(R.id.no_notifications_container);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateList();
    }

    private void updateList() {
        setLoading(true);

        String accountId = User.getAccountId(getContext());
        if (accountId.isEmpty()) {
            CFApi.getAccountId(getMain(), new CFApi.StringListener() {
                @Override
                public void onResult(String result) {
                    updateList();
                    setLoading(false);
                }

                @Override
                public void onError(Exception e) {
                    setLoading(false);
                }
            });
            return;
        }

        CFApi.getNotifications(getMain(), new CFApi.NotificationListener() {
            @Override
            public void onResult(ArrayList<Notification> notifications) {
                displayList(notifications);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private void displayList(ArrayList<Notification> notifications) {
        if (!isVisible()) return;
        if (notifications.size() == 0) {
            showEmpty(true);
            return;
        }

        showEmpty(false);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //adapter = new DNSRecordAdapter(getContext(), records, zone);
        recycler.setAdapter(adapter);
        setLoading(false);
    }

    private void showEmpty(boolean b) {
        emptyContainer.setVisibility(b ? View.VISIBLE : View.GONE);
        recycler.setVisibility(b ? View.GONE : View.VISIBLE);
    }


}
