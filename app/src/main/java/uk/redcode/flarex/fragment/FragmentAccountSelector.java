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
import uk.redcode.flarex.adapter.AccountAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.CFAccount;

public class FragmentAccountSelector extends FragmentCC {

    private ArrayList<CFAccount> accounts = new ArrayList<>();
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

        CFApi.getAccounts(getMain(), new CFApi.AccountListener() {
            @Override
            public void onResult(ArrayList<CFAccount> accounts) {
                FragmentAccountSelector.this.accounts = accounts;
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

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        AccountAdapter adapter = new AccountAdapter(requireContext(), accounts);
        adapter.setListener(new AccountAdapter.AccountListener() {
            @Override
            public void onAccountSelected(CFAccount account) {
                changeAccount(account);
            }
        });
        recycler.setAdapter(adapter);
        setLoading(false);
    }
}
