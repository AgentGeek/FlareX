package uk.redcode.flarex.params;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Param;
import uk.redcode.flarex.object.Zone;

public class AppParamLocking extends Param implements CompoundButton.OnCheckedChangeListener {

    private SwitchMaterial paramSwitch;
    private BiometricManager manager;
    private BiometricPrompt prompter;
    private BiometricPrompt.PromptInfo promptInfo;
    private TextView description;

    public AppParamLocking setActivity(Activity activity) {
        this.manager = BiometricManager.from(activity);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.app_name))
                .setSubtitle(activity.getString(R.string.fingerprint_confirm))
                .setNegativeButtonText(activity.getString(R.string.cancel))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        prompter = new BiometricPrompt((FragmentActivity) activity, ContextCompat.getMainExecutor(activity), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode != 13 && errorCode != 10) {
                    description.setText(errString);
                }
                paramSwitch.setOnCheckedChangeListener(AppParamLocking.this);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AppParameter.setBoolean(context, AppParameter.ENABLE_FINGERPRINT, true);
                paramSwitch.setChecked(true);
                paramSwitch.setOnCheckedChangeListener(AppParamLocking.this);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        return this;
    }

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_boolean, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.enable_lock);
        description = root.findViewById(R.id.param_description);
        description.setText(R.string.enable_lock_description);
        paramSwitch = root.findViewById(R.id.param_switch);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        paramSwitch.setOnCheckedChangeListener(null);

        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            // fingerprint available
            case BiometricManager.BIOMETRIC_SUCCESS:
                boolean value = AppParameter.getBoolean(context, AppParameter.ENABLE_FINGERPRINT, false);
                paramSwitch.setChecked(value);
                paramSwitch.setEnabled(true);
                paramSwitch.setOnCheckedChangeListener(this);
                return;

            // hardware not available
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                AppParameter.setBoolean(context, AppParameter.ENABLE_FINGERPRINT, false);
                paramSwitch.setEnabled(false);
                description.setText(R.string.fingerprint_not_available);
                return;

            // no fingerprint enrolled
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                AppParameter.setBoolean(context, AppParameter.ENABLE_FINGERPRINT, false);
                paramSwitch.setEnabled(false);
                description.setText(R.string.fingerprint_not_enrolled);
                return;

            // error
            default:
                Log.d("BiometricManager", "Unknown value: "+manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG));
                AppParameter.setBoolean(context, AppParameter.ENABLE_FINGERPRINT, false);
                paramSwitch.setEnabled(false);
                description.setText(R.string.fingerprint_error);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // avoid user being able to spam it
        paramSwitch.setOnCheckedChangeListener(null);

        if (isChecked) {
            // ask confirmation first
            paramSwitch.setChecked(false);
            prompter.authenticate(promptInfo);
            return;
        }

        AppParameter.setBoolean(context, AppParameter.ENABLE_FINGERPRINT, isChecked);
        paramSwitch.setOnCheckedChangeListener(this);
    }
}
