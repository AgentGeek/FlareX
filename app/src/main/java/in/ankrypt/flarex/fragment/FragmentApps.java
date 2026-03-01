package in.ankrypt.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import in.ankrypt.flarex.MainActivity;
import in.ankrypt.flarex.R;
import in.ankrypt.flarex.adapter.AppAdapter;
import in.ankrypt.flarex.object.App;
import in.ankrypt.flarex.ui.Alert;
import in.ankrypt.flarex.ui.ViewManager;

public class FragmentApps extends FragmentCC implements AppAdapter.AppListener {

    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler, container, false);
        recycler = (RecyclerView) root;
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppAdapter adapter = new AppAdapter(requireContext(), App.getList());
        adapter.setListener(this);
        recycler.setAdapter(adapter);
        setLoading(false);
    }

    @Override
    public void onAppSelected(App app) {
        if (app.requireAccount) {
            if (!getAccountManager().isAccountSelected()) {
                alert(new Alert(Alert.INFO, R.string.account_select_require));
                return;
            }
        }

        setView(app.view, null);
        ((MainActivity) requireActivity()).setToolbarIcon(R.drawable.ic_arrow_left, null);
        ((MainActivity) requireActivity()).viewManager.actualFragment.enableBackView = true;
        ((MainActivity) requireActivity()).viewManager.actualFragment.lastView = ViewManager.VIEW_APPS;
    }
}
