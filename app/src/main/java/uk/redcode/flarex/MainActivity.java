package uk.redcode.flarex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.OnBackPressedCallback;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uk.redcode.flarex.activity.LoginActivity;
import uk.redcode.flarex.fragment.FragmentCloudflarePost;
import uk.redcode.flarex.fragment.FragmentCloudflareStatus;
import uk.redcode.flarex.fragment.FragmentCommunity;
import uk.redcode.flarex.fragment.FragmentFirewall;
import uk.redcode.flarex.fragment.FragmentTopic;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.NotificationManager;
import uk.redcode.flarex.object.User;
import uk.redcode.flarex.params.AppParamTheme;
import uk.redcode.flarex.ui.AccountManager;
import uk.redcode.flarex.ui.Alert;
import uk.redcode.flarex.ui.LayoutManager;
import uk.redcode.flarex.ui.ViewManager;
import uk.redcode.flarex.ui.ZoneManager;
import uk.redcode.flarex.work.WorkerManager;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    private TextView toolbarTitle;
    public ImageView toolbarIcon;
    private ProgressBar progress;
    private LinearLayout alertContainer;
    public BottomNavigationView bottomNav;

    public ZoneManager zoneManager;
    public ViewManager viewManager;
    public AccountManager accountManager;
    public boolean unlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define theme
        int theme = AppParameter.getInt(this, AppParameter.THEME, 0);
        AppCompatDelegate.setDefaultNightMode(AppParamTheme.getNightMode(theme));
        setTheme(AppParamTheme.getTheme(theme));
        setContentView(R.layout.activity_main);

        NotificationManager.verifyChannel(this);
        start();

        WorkerManager.init(this);
    }

    private void start() {
        if (!User.isConnected(this)) {
            Logger.info("User not connected, show LoginActivity");
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish(); // Finish MainActivity so user can't go back to empty dashboard
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarIcon = findViewById(R.id.toolbar_icon);
        progress = findViewById(R.id.progress);
        alertContainer = findViewById(R.id.alert_container);
        bottomNav = findViewById(R.id.bottom_nav);
        
        if (toolbar == null || toolbarTitle == null || toolbarIcon == null || 
            progress == null || alertContainer == null || bottomNav == null) {
            Logger.error("Failed to initialize UI elements: progress=" + (progress == null));
            return;
        }
        
        setSupportActionBar(toolbar);
        setLoading(false);

        zoneManager = new ZoneManager(this);
        accountManager = new AccountManager(this);
        viewManager = new ViewManager(this, zoneManager);

        setupBackPressedHandler();
        // runView() is called in onResume()
    }

    private void setupBackPressedHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewManager != null) {
                    viewManager.onBackPressed(MainActivity.this);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void runView() {
        // reset
        if (viewManager == null || zoneManager == null) return;
        
        viewManager.resetAllView();

        // build bottom nav
        buildBottomNav();

        // load home view
        String viewHome = zoneManager.selected == null ? ViewManager.VIEW_ZONE_SELECTOR : getHomeView();

        // check for required view
        if (shouldDoSpecificView(viewHome)) return;

        // check if biometric is enable
        if (AppParameter.getBoolean(this, AppParameter.ENABLE_FINGERPRINT, false) && !unlocked) viewHome = ViewManager.VIEW_LOCK;

        // run home
        viewManager.setView(viewHome, null);
        if (ViewManager.VIEW_ZONE_SELECTOR.equals(viewHome)) setLoading(true);
    }

    private String getHomeView() {
        if (bottomNav == null || bottomNav.getMenu().size() == 0) {
            return ViewManager.VIEW_SETTINGS;
        }
        
        MenuItem firstItem = bottomNav.getMenu().getItem(0);
        if (firstItem == null) return ViewManager.VIEW_SETTINGS;
        
        int home = firstItem.getItemId();
        if (home == R.id.nav_dashboard) return ViewManager.VIEW_DASHBOARD;
        if (home == R.id.nav_dns) return ViewManager.VIEW_DNS;
        if (home == R.id.nav_apps) return ViewManager.VIEW_APPS;
        //if (home == R.id.nav_community) return ViewManager.VIEW_COMMUNITY;
        if (home == R.id.nav_settings) return ViewManager.VIEW_SETTINGS;

        showAlert(new Alert(Alert.ERROR, "Internal Error loading home view"));
        return ViewManager.VIEW_SETTINGS;
    }

    private void buildBottomNav() {
        if (bottomNav == null) return;
        
        try {
            bottomNav.getMenu().clear();
            Menu menu = bottomNav.getMenu();

            if (LayoutManager.hasDashboard()) menu.add(Menu.NONE, R.id.nav_dashboard, Menu.NONE, getString(R.string.dashboard)).setIcon(R.drawable.ic_dashboard);
            if (LayoutManager.get(LayoutManager.DNS)) menu.add(Menu.NONE, R.id.nav_dns, Menu.NONE, getString(R.string.dns)).setIcon(R.drawable.ic_dns);

            menu.add(Menu.NONE, R.id.nav_apps, Menu.NONE, getString(R.string.apps)).setIcon(R.drawable.ic_apps);
            // Community hidden for now
            // menu.add(Menu.NONE, R.id.nav_community, Menu.NONE, getString(R.string.community)).setIcon(R.drawable.ic_community);
            menu.add(Menu.NONE, R.id.nav_settings, Menu.NONE, getString(R.string.settings)).setIcon(R.drawable.ic_settings);
        } catch (Exception e) {
            Logger.error("Error configuring menu: " + e.getMessage());
        }
    }

    private boolean shouldDoSpecificView(String homeView) {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return false;

        String view = extras.getString("view");
        if (view == null || view.isEmpty()) return false;

        if (view.equals(ViewManager.VIEW_CLOUDFLARE_POST)) {
            Logger.info("Load Cloudflare post: "+extras.getString("post-url"));
            viewManager.actualView = homeView;
            viewManager.setView(ViewManager.VIEW_CLOUDFLARE_POST, extras.getString("post-url"));
            return true;
        }

        return false;
    }

    private void onToolbarIconClick() {
        if (viewManager == null || viewManager.actualFragment == null) return;
        
        if (viewManager.actualView.equals(ViewManager.VIEW_FIREWALL)) {
            if (viewManager.actualFragment instanceof FragmentFirewall) {
                ((FragmentFirewall) viewManager.actualFragment).onBack();
            }
            return;
        }
        
        if (!viewManager.actualFragment.enableBackView) return;

        String lastView = viewManager.actualFragment.lastView;
        if (lastView != null) {
            viewManager.setView(lastView, null);
        }
    }

    public void setLoading(boolean loading) { 
        if (progress != null) {
            progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitle(String title) { toolbarTitle.setText(title); }
    public void setTitle(int resId) { toolbarTitle.setText(resId); }

    public void setToolbarIcon(int icon, @Nullable View.OnClickListener listener) {
        if (icon == -1) {
            toolbarIcon.setVisibility(View.GONE);
        } else {
            toolbarIcon.setVisibility(View.VISIBLE);
            toolbarIcon.setImageResource(icon);
            toolbarIcon.setOnClickListener(listener == null ? (view -> onToolbarIconClick()) : listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return viewManager.onCreateOptionsMenu(menu); }

    @Override
    protected void onResume() {
        super.onResume();
        runView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unlocked = false;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (viewManager == null) return false;
        
        if (item.getItemId() == R.id.swap_zone) {
            viewManager.setView(ViewManager.VIEW_ZONE_SELECTOR, null);
            return true;
        } else if (item.getItemId() == R.id.nav_add_dns_record) {
            viewManager.setView(ViewManager.VIEW_ADD_DNS_RECORD, null);
            return true;
        }/* else if (item.getItemId() == R.id.nav_add_firewall_rule) {
            viewManager.setView(ViewManager.VIEW_ADD_FIREWALL, null);
            return true;
        } */else if (item.getItemId() == R.id.nav_cloudflare_status) {
            viewManager.setView(ViewManager.VIEW_CLOUDFLARE_STATUS, null);
            return true;
        } else if (item.getItemId() == R.id.nav_blog) {
            viewManager.setView(ViewManager.VIEW_CLOUDFLARE_BLOG, null);
            return true;
        } else if (item.getItemId() == R.id.actualise_status && viewManager.actualView.equals(ViewManager.VIEW_CLOUDFLARE_STATUS)) {
            if (viewManager.actualFragment instanceof FragmentCloudflareStatus) {
                ((FragmentCloudflareStatus) viewManager.actualFragment).updateList();
            }
            return true;
        } else if (item.getItemId() == R.id.nav_logout) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.show_on_web) {
            if (viewManager.actualView.equals(ViewManager.VIEW_CLOUDFLARE_POST) && 
                viewManager.actualFragment instanceof FragmentCloudflarePost) {
                ((FragmentCloudflarePost) viewManager.actualFragment).showOnWeb();
            }
            if (viewManager.actualView.equals(ViewManager.VIEW_COMMUNITY_TOPIC) && 
                viewManager.actualFragment instanceof FragmentTopic) {
                ((FragmentTopic) viewManager.actualFragment).showOnWeb();
            }
            return true;
        } else if (item.getItemId() == R.id.search_topic) {
            if (viewManager.actualView.equals(ViewManager.VIEW_COMMUNITY) && 
                viewManager.actualFragment instanceof FragmentCommunity) {
                ((FragmentCommunity) viewManager.actualFragment).openSearch();
            }
            return true;
        }
        return false;
    }

    private void logout() {
        setLoading(true);
        User.logout(this);
        if (viewManager != null) viewManager.resetAllView();

        // run login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAlert(Alert alert) {
        alert.setActivity(this);
        alert.show(alertContainer);
    }

    public void unlock() {
        this.unlocked = true;
        this.runView();
        showAlert(new Alert(Alert.SUCCESS, R.string.authenticated));
    }
}