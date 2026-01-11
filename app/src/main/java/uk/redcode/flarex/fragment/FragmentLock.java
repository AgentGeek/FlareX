package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;

public class FragmentLock extends FragmentCC {

    private BiometricPrompt prompter;
    private BiometricPrompt.PromptInfo promptInfo;
    private TextView label;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.app_name))
                .setSubtitle(getString(R.string.fingerprint_prompt_description))
                .setNegativeButtonText(getString(R.string.cancel))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        prompter = new BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                label.setText(getLabel(errorCode, errString.toString()));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                ((MainActivity) requireActivity()).unlock();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                label.setText(R.string.authentication_failed);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lock, container, false);

        label = root.findViewById(R.id.lock_error);
        root.findViewById(R.id.lock_icon).setOnClickListener(view -> prompt());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        prompt();
    }

    private void prompt() {
        label.setText("");
        prompter.authenticate(promptInfo);
    }

    private String getLabel(int code, String err) {
        switch (code) {
            case 10:
            case 13:
                return getString(R.string.authentication_canceled);
            default:
                Log.d("FragmentLock", String.format("Unknown error code: %d | %s", code, err));
                return String.format(getString(R.string.fingerprint_internal_error), err);
        }
    }
}
