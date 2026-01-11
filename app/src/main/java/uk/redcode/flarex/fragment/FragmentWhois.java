package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Whois;

public class FragmentWhois extends FragmentCC {

    private LinearLayout container;
    private TextInputLayout domainLayout;
    private TextInputEditText domainInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_whois, container, false);

        this.container = root.findViewById(R.id.whois_result);
        domainInput = root.findViewById(R.id.domain_input);
        domainLayout = root.findViewById(R.id.domain_layout);

        this.container.setVisibility(View.GONE);
        domainLayout.setEndIconOnClickListener(view -> lookup());
        domainInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (domainLayout.isErrorEnabled()) domainLayout.setErrorEnabled(false);
            }
        });

        return root;
    }

    private void lookup() {
        if (!verify()) return;

        setLoading(true);
        container.setVisibility(View.GONE);
        CFApi.whois(requireContext(), getAccountId(), domainInput.getText().toString(), new CFApi.WhoisListener() {
            @Override
            public void onResult(Whois whois) {
                bindResult(whois);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    private boolean verify() {
        if (domainInput.getText() == null) {
            Toast.makeText(requireContext(), R.string.error_get_input_text, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (domainInput.getText().toString().isEmpty()) {
            domainLayout.setError(getString(R.string.cant_be_empty));
            domainLayout.setErrorEnabled(true);
            return false;
        }

        if (!Parser.isDomain(domainInput.getText().toString())) {
            domainLayout.setError(getString(R.string.not_valid_domain));
            domainLayout.setErrorEnabled(true);
            return false;
        }

        domainLayout.setErrorEnabled(false);
        return true;
    }

    private void bindResult(Whois whois) {
        if (notFound(whois)) return;

        ((TextView) container.findViewById(R.id.whois_domain)).setText(whois.domain);
        ((TextView) container.findViewById(R.id.whois_country)).setText(whois.country);
        ((TextView) container.findViewById(R.id.whois_date)).setText(whois.creationDate);
        ((TextView) container.findViewById(R.id.whois_update_date)).setText(whois.updateDate);
        ((TextView) container.findViewById(R.id.whois_email)).setText(whois.email);
        ((TextView) container.findViewById(R.id.whois_org)).setText(whois.organisation);
        ((TextView) container.findViewById(R.id.whois_registrant)).setText(whois.registrant);
        ((TextView) container.findViewById(R.id.whois_registrar)).setText(whois.registrar);

        ChipGroup group = container.findViewById(R.id.whois_nameservers);
        group.removeAllViews();
        for (String server : whois.nameservers) {
            Chip chip = new Chip(requireContext());
            chip.setText(server);
            group.addView(chip);
        }

        container.setVisibility(View.VISIBLE);
    }

    private boolean notFound(Whois whois) {
        if (!whois.notFound) return false;

        domainLayout.setError(getString(R.string.domain_not_register));
        domainLayout.setErrorEnabled(true);
        return true;
    }

}
