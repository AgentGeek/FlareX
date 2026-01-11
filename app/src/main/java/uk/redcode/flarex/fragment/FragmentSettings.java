package uk.redcode.flarex.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.activity.LoginActivity;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.User;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Parameter;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentSettings extends FragmentCC  {

    public static final int VIEW_MENU = 0;
    public static final int VIEW_CATEGORY = 1;

    private LinearLayout container;
    public int actualView = VIEW_MENU;
    private int actualCategory = -1;
    private ArrayList<Integer> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        this.container = root.findViewById(R.id.settings_container);

        categories = Parameter.getCategories();
        buildView();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setLoading(false);
        buildMenu();
    }

    private void buildView() {
        if (actualView == VIEW_MENU) buildMenu();
        else if (actualView == VIEW_CATEGORY) buildCategory();
    }

    private void buildMenu() {
        // redraw zone icon
        ((MainActivity) requireActivity()).zoneManager.updateLabel();

        container.removeAllViews();
        getAccountManager().buildHeader(container, getLayoutInflater());
        for (int cat : categories) {
            View view = getLayoutInflater().inflate(R.layout.row_setting_category, container, false);
            ((TextView) view.findViewById(R.id.category_name)).setText(Parameter.getName(cat));
            ((ImageView) view.findViewById(R.id.category_icon)).setImageResource(Parameter.getIcon(cat));

            view.setOnClickListener(v -> {
                if (cat == Parameter.NOTIFICATIONS) {
                    setView(ViewManager.VIEW_NOTIFICATION, null);
                    return;
                }

                actualCategory = cat;
                actualView = VIEW_CATEGORY;
                buildView();
            });

            container.addView(view);
        }
        buildTokenRefresh();
        buildSwapZone();
    }

    private void buildSwapZone() {
        View view = getLayoutInflater().inflate(R.layout.row_setting_category, container, false);
        ((TextView) view.findViewById(R.id.category_name)).setText(getString(R.string.swap_zone));
        ((ImageView) view.findViewById(R.id.category_icon)).setImageResource(R.drawable.ic_swap);

        view.setOnClickListener(v ->  ((MainActivity) requireActivity()).viewManager.setView(ViewManager.VIEW_ZONE_SELECTOR, null));

        container.addView(view);
    }

    private void buildTokenRefresh() {
        if (User.getMode(requireContext()) != CFApi.TYPE_TOKEN) return;
        View view = getLayoutInflater().inflate(R.layout.row_setting_category, container, false);
        ((TextView) view.findViewById(R.id.category_name)).setText(getString(R.string.refresh_token));
        ((ImageView) view.findViewById(R.id.category_icon)).setImageResource(R.drawable.ic_key);

        view.setOnClickListener(v ->  {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.putExtra("ACTION", "REFRESH_TOKEN");
            intent.putExtra("TOKEN", User.getKey(requireContext()));
            requireActivity().startActivity(intent);
        });

        container.addView(view);
    }

    private void buildCategory() {
        // set back icon
        ((MainActivity) requireActivity()).setToolbarIcon(R.drawable.ic_arrow_left, v -> {
            actualView = VIEW_MENU;
            buildView();
        });

        container.removeAllViews();
        ArrayList<Param> params = Parameter.getParams(actualCategory, getMain());

        for (Param p : params) {
            p.onDraw(getLayoutInflater(), container, zone, getAccountManager().selected);
            p.refresh();
        }
    }
}
