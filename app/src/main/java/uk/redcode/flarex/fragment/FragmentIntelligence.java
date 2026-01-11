package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Intelligence;
import uk.redcode.flarex.object.Parser;

public class FragmentIntelligence extends FragmentCC {

    private TextInputLayout inputLayout;
    private TextInputEditText input;
    private IntelView intelView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_intel, container, false);

        input = root.findViewById(R.id.intel_input);
        inputLayout = root.findViewById(R.id.intel_input_layout);
        inputLayout.setEndIconOnClickListener(view -> search());
        intelView = new IntelView(root);
        input.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable editable) {}
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputLayout.isErrorEnabled()) inputLayout.setErrorEnabled(false);
            }
        });

        return root;
    }

    private void search() {
        int type = getInputType();
        if (!verify(type)) return;

        setLoading(true);
        intelView.hide();
        CFApi.getIntelligence(requireContext(), getAccountId(), type, input.getText().toString(), new CFApi.IntelligenceListener() {
            @Override
            public void onResult(Intelligence intel) {
                intelView.bind(intel);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private int getInputType() {
        String value = input.getText().toString();
        if (Parser.isDomain(value)) return Intelligence.DOMAIN;
        if (Parser.isIPv4(value)) return Intelligence.IPV4;
        if (Parser.isIPv6(value)) return Intelligence.IPV6;
        return 0;
    }

    private boolean verify(int type) {
        // cant be empty
        if (input.getText().toString().isEmpty()) {
            inputLayout.setError(getString(R.string.cant_be_empty));
            inputLayout.setErrorEnabled(true);
            return false;
        }

        // check valid type
        if (type == 0) {
            inputLayout.setError(getString(R.string.not_valid_domain_ip));
            inputLayout.setErrorEnabled(true);
            return false;
        }

        inputLayout.setErrorEnabled(false);
        return true;
    }

    private class IntelView {

        private final LinearLayout container;
        private final TextView asked;
        private final ChipGroup risks;
        // ip only
        private final ChipGroup ptrDomains;
        private final ChipGroup reservations;
        private final LinearLayout typeLayout;
        private final LinearLayout countryLayout;
        private final LinearLayout descriptionLayout;
        // domain only
        private final ChipGroup categories;
        private final ChipGroup domainRefs;
        private final LinearLayout appLayout;
        private final LinearLayout riskLayout;
        private final LinearLayout popularityLayout;


        public IntelView(View root) {
            container = root.findViewById(R.id.intel_result);
            asked = root.findViewById(R.id.intel_asked);
            risks = root.findViewById(R.id.intel_risks);
            typeLayout = root.findViewById(R.id.intel_type_layout);
            countryLayout = root.findViewById(R.id.intel_country_layout);
            descriptionLayout = root.findViewById(R.id.intel_description_layout);
            appLayout = root.findViewById(R.id.intel_app_layout);
            ptrDomains = root.findViewById(R.id.intel_ptr_domain);
            categories = root.findViewById(R.id.intel_categories);
            domainRefs = root.findViewById(R.id.intel_domain_refs);
            riskLayout = root.findViewById(R.id.intel_risk_layout);
            reservations = root.findViewById(R.id.intel_reservations);
            popularityLayout = root.findViewById(R.id.intel_popularity_layout);

            container.setVisibility(View.GONE);
        }

        public void hide() {
            container.setVisibility(View.GONE);
        }

        private void resetView(boolean isDomain) {
            // domain
            appLayout.setVisibility(isDomain ? View.VISIBLE : View.GONE);
            riskLayout.setVisibility(isDomain ? View.VISIBLE : View.GONE);
            popularityLayout.setVisibility(isDomain ? View.VISIBLE : View.GONE);
            // ip
            typeLayout.setVisibility(isDomain ? View.GONE : View.VISIBLE);
            countryLayout.setVisibility(isDomain ? View.GONE : View.VISIBLE);
            descriptionLayout.setVisibility(isDomain ? View.GONE : View.VISIBLE);
            // Categories - Domain
            categories.setVisibility(isDomain ? View.VISIBLE : View.GONE);
            container.findViewById(R.id.intel_categories_label).setVisibility(isDomain ? View.VISIBLE : View.GONE);
            // Domain Ref - Domain
            domainRefs.setVisibility(isDomain ? View.VISIBLE : View.GONE);
            container.findViewById(R.id.intel_domain_refs_label).setVisibility(isDomain ? View.VISIBLE : View.GONE);
            // PRT Domain - IP
            ptrDomains.setVisibility(isDomain ? View.GONE : View.VISIBLE);
            container.findViewById(R.id.intel_ptr_domain_label).setVisibility(isDomain ? View.GONE : View.VISIBLE);
            // reservations - IP
            reservations.setVisibility(isDomain ? View.GONE : View.VISIBLE);
            container.findViewById(R.id.intel_reservations_label).setVisibility(isDomain ? View.GONE : View.VISIBLE);
        }

        public void bind(Intelligence intel) {
            resetView(intel.type == Intelligence.DOMAIN);
            if (intel.type == Intelligence.DOMAIN) bindDomain(intel);
            else bindIP(intel);
            container.setVisibility(View.VISIBLE);
        }

        private void bindIP(Intelligence intel) {
            asked.setText(intel.asked);
            ((TextView) typeLayout.findViewById(R.id.intel_type)).setText(intel.ipref.type);
            ((TextView) countryLayout.findViewById(R.id.intel_country)).setText(intel.ipref.country);
            ((TextView) descriptionLayout.findViewById(R.id.intel_description)).setText(intel.ipref.description);
            buildCategory(risks, intel.risks);
            buildStringChip(ptrDomains, intel.ptrDomains);
            buildStringChip(reservations, intel.reservations);
        }

        private void bindDomain(Intelligence intel) {
            asked.setText(intel.asked);
            ((TextView) appLayout.findViewById(R.id.intel_app)).setText(intel.application);
            ((TextView) riskLayout.findViewById(R.id.intel_risk)).setText(intel.riskScore);
            ((TextView) popularityLayout.findViewById(R.id.intel_popularity)).setText(intel.rank);
            buildCategory(risks, intel.risks);
            buildCategory(categories, intel.categories);
            buildStringChip(domainRefs, intel.refs);
        }

        private void buildCategory(ChipGroup container, ArrayList<Intelligence.Category> list) {
            container.removeAllViews();
            for (Intelligence.Category cat : list) {
                Chip chip = new Chip(requireContext());
                chip.setText(cat.name);
                container.addView(chip);
            }
            if (list.size() == 0) container.addView(getEmptyChip());
        }

        private void buildStringChip(ChipGroup container, ArrayList<String> list) {
            container.removeAllViews();
            for (String str : list) {
                Chip chip = new Chip(requireContext());
                chip.setText(str);
                container.addView(chip);
            }
            if (list.size() == 0) container.addView(getEmptyChip());
        }

        private View getEmptyChip() {
            Chip empty = new Chip(requireContext());
            empty.setText("⍉");
            return empty;
        }

    }
}
