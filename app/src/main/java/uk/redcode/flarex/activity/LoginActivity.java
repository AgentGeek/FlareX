package uk.redcode.flarex.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentLogin;
import uk.redcode.flarex.fragment.FragmentTokenTest;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.params.AppParamTheme;
import uk.redcode.flarex.ui.LayoutManager;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progress;
    private Toolbar toolbar;

    private FragmentLogin fragmentLogin;
    private FragmentTokenTest fragmentTokenTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        // define theme
        int theme = AppParameter.getInt(this, AppParameter.THEME, 0);
        AppCompatDelegate.setDefaultNightMode(AppParamTheme.getNightMode(theme));
        setTheme(AppParamTheme.getTheme(theme));
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> fragmentTokenTest.stop());

        progress = findViewById(R.id.progress);
        fragmentLogin = new FragmentLogin();
        fragmentTokenTest = new FragmentTokenTest();

        showRequiredAction();
        setLoading(false);
    }

    private void showRequiredAction() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            showLogin();
            return;
        }

        String action = extras.getString("ACTION");
        if (action.equals("REFRESH_TOKEN")) {
            String token = extras.getString("TOKEN");
            if (token.isEmpty()) {
                Toast.makeText(this, R.string.token_empty, Toast.LENGTH_SHORT).show();
                showLogin();
                return;
            }
            CFApi.saveCredential(this, "Token User", token, CFApi.TYPE_TOKEN);
            runTokenTest();
        } else {
            showLogin();
        }
    }

    public void setLoading(boolean b) {
        progress.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    public void showLogin() {
        toolbar.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, fragmentLogin, "login").commit();
    }

    public void runTokenTest() {
        LayoutManager.reset(this);
        setLoading(false);
        toolbar.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, fragmentTokenTest, "tokenTester").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return fragmentTokenTest.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.validate_token) {
            return fragmentTokenTest.validateToken();
        }
        return super.onOptionsItemSelected(item);
    }
}