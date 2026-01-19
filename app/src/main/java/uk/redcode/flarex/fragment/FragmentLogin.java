package uk.redcode.flarex.fragment;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.activity.LoginActivity;
import uk.redcode.flarex.dialog.DialogHelper;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.Parser;

public class FragmentLogin extends Fragment {

    private TextInputEditText emailInput;
    private TextInputEditText keyInput;
    private TextInputLayout emailLayout;
    private TextInputLayout keyLayout;
    private ChipGroup connectionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = root.findViewById(R.id.email);
        emailLayout = root.findViewById(R.id.email_layout);
        keyInput = root.findViewById(R.id.apikey);
        keyLayout = root.findViewById(R.id.apikey_layout);
        keyLayout.setEndIconOnClickListener(v -> pastKey());
        connectionMode = root.findViewById(R.id.connection_mode);

        connectionMode.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.get(0) == R.id.login_master_key) {
                emailLayout.setVisibility(View.VISIBLE);
                keyLayout.setHint(R.string.apikey);
            } else if (checkedIds.get(0) == R.id.login_token) {
                emailLayout.setVisibility(View.GONE);
                keyLayout.setHint(R.string.api_token);
            }
        });

        root.findViewById(R.id.login_btn).setOnClickListener(v -> testAndLogin());
        root.findViewById(R.id.help_btn).setOnClickListener(view -> {
            DialogHelper helper = new DialogHelper();
            helper.show(requireActivity().getSupportFragmentManager(), DialogHelper.TAG);
        });

        return root;
    }

    private void setLoading(boolean b) {
        Activity activity = requireActivity();
        if (activity instanceof LoginActivity) ((LoginActivity) activity).setLoading(b);
    }

    private void runTokenTest() {
        Activity activity = requireActivity();
        if (activity instanceof LoginActivity) ((LoginActivity) activity).runTokenTest();
        else Toast.makeText(requireContext(), "Fatal Error: Parent is not LoginActivity", Toast.LENGTH_LONG).show();
    }

    private void pastKey() {
        try {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
            if (clipboard.getPrimaryClip() == null) return;
            CharSequence key = clipboard.getPrimaryClip().getItemAt(0).getText();
            keyInput.setText(key);
        } catch (Exception e) {
            Logger.error(e);
            String message = e.getLocalizedMessage();
            if (message == null || message.isEmpty()) message = "Unknown Error";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void testAndLogin() {
        if (!verify()) return;
        setLoading(true);

        if (connectionMode.getCheckedChipId() == R.id.login_token) {
            testAndLoginToken();
            return;
        }

        String email = emailInput.getText().toString();
        String apikey = keyInput.getText().toString();

        CFApi.testAndSave(requireContext(), email, apikey, new CFApi.TestListener() {
            @Override
            public void onResult(boolean connected) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                showError(e);
            }
        });
    }

    private void testAndLoginToken() {
        String token = keyInput.getText().toString();

        CFApi.verifyToken(requireContext(), token, new CFApi.TestListener() {
            @Override
            public void onResult(boolean connected) {
                runTokenTest();
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                showError(e);
            }
        });
    }

    private void showError(Exception error) {
        Logger.error(error);
        String message = error.getMessage();
        if (message == null || message.isEmpty()) message = "Unknown Error";
        String msg = message.startsWith("400") ? getString(R.string.invalid_credential) : message;
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
    }

    private boolean verify() {
        boolean exit = true;

        if (keyInput.getText().toString().isEmpty()) {
            keyLayout.setError(getString(R.string.cant_be_empty));
            keyLayout.setErrorEnabled(true);
            exit = false;
        } else if (!Parser.isValidToken(keyInput.getText().toString())) {
            keyLayout.setError(getString(R.string.not_valid_token));
            keyLayout.setErrorEnabled(true);
            exit = false;
        } else {
            keyLayout.setErrorEnabled(false);
        }

        // if token mode don't check email
        if (connectionMode.getCheckedChipId() == R.id.login_token) return exit;

        if (emailInput.getText().toString().isEmpty()) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError(getString(R.string.cant_be_empty));
            exit = false;
        } else if (!Parser.isEmail(emailInput.getText().toString())) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError(getString(R.string.not_valid_email));
            exit = false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return exit;
    }

}
