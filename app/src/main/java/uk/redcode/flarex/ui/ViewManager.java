package uk.redcode.flarex.ui;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationBarView;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentAccountSelector;
import uk.redcode.flarex.fragment.FragmentAddDNS;
import uk.redcode.flarex.fragment.FragmentApps;
import uk.redcode.flarex.fragment.FragmentCC;
import uk.redcode.flarex.fragment.FragmentCloudflareBlog;
import uk.redcode.flarex.fragment.FragmentCloudflarePost;
import uk.redcode.flarex.fragment.FragmentCloudflareStatus;
import uk.redcode.flarex.fragment.FragmentCommunity;
import uk.redcode.flarex.fragment.FragmentDNS;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.fragment.FragmentFirewall;
import uk.redcode.flarex.fragment.FragmentHistory;
import uk.redcode.flarex.fragment.FragmentIPS;
import uk.redcode.flarex.fragment.FragmentIntelligence;
import uk.redcode.flarex.fragment.FragmentLock;
import uk.redcode.flarex.fragment.FragmentNotifications;
import uk.redcode.flarex.fragment.FragmentSettings;
import uk.redcode.flarex.fragment.FragmentTopic;
import uk.redcode.flarex.fragment.FragmentWhois;
import uk.redcode.flarex.fragment.FragmentZoneSelector;
import uk.redcode.flarex.object.CFPost;
import uk.redcode.flarex.object.CountryStat;
import uk.redcode.flarex.object.DNSRecord;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.Topic;

public class ViewManager implements NavigationBarView.OnItemSelectedListener {

    private static final String TAG = "ViewManager";
    public static final String VIEW_LOCK = "lock";
    public static final String VIEW_DASHBOARD = "dashboard";
    public static final String VIEW_SETTINGS = "settings";
    public static final String VIEW_APPS = "apps";
    public static final String VIEW_WHOIS = "whois";
    public static final String VIEW_HISTORY = "history";
    public static final String VIEW_IPS = "ips";
    public static final String VIEW_INTELLIGENCE = "intelligence";
    public static final String VIEW_DNS = "dns";
    public static final String VIEW_ADD_DNS_RECORD = "add-dns-record";
    public static final String VIEW_FIREWALL = "firewall";
    public static final String VIEW_COMMUNITY = "community";
    public static final String VIEW_COMMUNITY_TOPIC = "community-topic";
    public static final String VIEW_ZONE_SELECTOR = "zone-selector";
    public static final String VIEW_ACCOUNT_SELECTOR = "account-selector";
    public static final String VIEW_CLOUDFLARE_STATUS = "cloudflare-status";
    public static final String VIEW_CLOUDFLARE_BLOG = "cloudflare-blog";
    public static final String VIEW_CLOUDFLARE_POST = "cloudflare-post";
    public static final String VIEW_NOTIFICATION = "notifications";
    public static final String VIEW_WORLD = "world";

    private final MainActivity activity;
    private final ZoneManager zoneManager;
    private final HistoryManager historyManager;
    private final Chip chipBeta;
    public String actualView = "";
    private final Object actualData = null;
    public FragmentCC actualFragment = null;

    private final FragmentLock fragmentLock = new FragmentLock();
    private final FragmentApps fragmentApps = new FragmentApps();
    private final FragmentWhois fragmentWhois = new FragmentWhois();
    private final FragmentHistory fragmentHistory = new FragmentHistory();
    private final FragmentIPS fragmentIPS = new FragmentIPS();
    private final FragmentIntelligence fragmentIntel = new FragmentIntelligence();
    private FragmentDNS fragmentDNS = new FragmentDNS();
    private FragmentAddDNS fragmentAddDNS = new FragmentAddDNS();
    // private FragmentWorld fragmentWorld = new FragmentWorld(); // Removed FragmentWorld
    private FragmentFirewall fragmentFirewall = new FragmentFirewall();
    private FragmentCommunity fragmentCommunity = new FragmentCommunity();
    private FragmentTopic fragmentTopic = new FragmentTopic();
    private FragmentSettings fragmentSettings = new FragmentSettings();
    private FragmentDashboard fragmentDashboard = new FragmentDashboard();
    private FragmentZoneSelector zoneSelector = new FragmentZoneSelector();
    private final FragmentAccountSelector accountSelector = new FragmentAccountSelector();
    private FragmentCloudflareStatus cloudflareStatus = new FragmentCloudflareStatus();
    private FragmentCloudflareBlog cloudflareBlog = new FragmentCloudflareBlog();
    private FragmentCloudflarePost cloudflarePost = new FragmentCloudflarePost();
    private FragmentNotifications fragmentNotifications = new FragmentNotifications();

    public ViewManager(MainActivity activity, ZoneManager zoneManager) {
        this.activity = activity;
        this.activity.bottomNav.setOnItemSelectedListener(this);
        this.activity.bottomNav.setOnItemReselectedListener(this::onNavigationItemSelected);
        this.chipBeta = activity.findViewById(R.id.toolbar_chip_beta);
        this.zoneManager = zoneManager;
        this.historyManager = new HistoryManager(this);
    }

    public void setView(String view, @Nullable Object data) {
        setView(view, data, true);
    }

    public void setView(String view, @Nullable Object data, boolean addToHistory) {
        Logger.info(TAG, "Change view: "+view);
        zoneManager.updateLabel();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentCC fragment = getFragment(view, data);

        fragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment, view)
                .addToBackStack(view)
                .commit();

        if (!actualView.isEmpty() && addToHistory) historyManager.push(new HistoryManager.History(actualView, actualData));

        this.actualView = view;
        this.actualFragment = fragment;
        activity.invalidateOptionsMenu();
    }

    private FragmentCC getFragment(String view, @Nullable Object data) {
        activity.bottomNav.setVisibility(View.VISIBLE);
        activity.toolbar.setVisibility(View.VISIBLE);
        chipBeta.setVisibility(View.GONE);

        switch (view) {

            case VIEW_LOCK:
                activity.bottomNav.setVisibility(View.GONE);
                activity.toolbar.setVisibility(View.GONE);
                return fragmentLock;

            case VIEW_DASHBOARD:
                fragmentDashboard.setZone(zoneManager.selected);
                setBottomNavSelection(R.id.nav_dashboard);
                return fragmentDashboard;

            // case VIEW_WORLD: // Removed VIEW_WORLD case
            //    activity.setToolbarIcon(R.drawable.ic_arrow_left, view1 -> setView(VIEW_DASHBOARD, null));
            //    if (data != null) fragmentWorld.setStat((CountryStat) data);
            //    return fragmentWorld;

            case VIEW_DNS:
                fragmentDNS.setZone(zoneManager.selected);
                setBottomNavSelection(R.id.nav_dns);
                return fragmentDNS;

            case VIEW_ADD_DNS_RECORD:
                fragmentAddDNS.setZone(zoneManager.selected);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                fragmentAddDNS.record = (DNSRecord) data;
                return fragmentAddDNS;

            case VIEW_APPS:
                setBottomNavSelection(R.id.nav_apps);
                return fragmentApps;

            case VIEW_WHOIS:
                activity.setTitle(R.string.whois);
                return fragmentWhois;

            case VIEW_HISTORY:
                activity.setTitle(R.string.history);
                return fragmentHistory;

            case VIEW_FIREWALL:
                fragmentFirewall.setZone(zoneManager.selected);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                return fragmentFirewall;

            case VIEW_IPS:
                activity.setTitle(R.string.cloudflare_ips);
                return fragmentIPS;

            case VIEW_INTELLIGENCE:
                activity.setTitle(R.string.intelligence);
                return fragmentIntel;

            case VIEW_COMMUNITY:
                setBottomNavSelection(R.id.nav_community);
                chipBeta.setVisibility(View.VISIBLE);
                activity.setTitle(R.string.community);
                activity.setToolbarIcon(-1, null);
                return fragmentCommunity;

            case VIEW_COMMUNITY_TOPIC:
                fragmentTopic.topic = (Topic) data;
                if (data != null) activity.setTitle(((Topic) data).title);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                return fragmentTopic;

            case VIEW_SETTINGS:
                fragmentSettings.setZone(zoneManager.selected);
                fragmentSettings.actualView = FragmentSettings.VIEW_MENU;
                setBottomNavSelection(R.id.nav_settings);
                return fragmentSettings;

            case VIEW_NOTIFICATION:
                //fragmentNotifications.setZone(zoneManager.selected);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                return fragmentNotifications;

            case VIEW_ZONE_SELECTOR:
                activity.bottomNav.setVisibility(View.GONE);
                activity.setToolbarIcon(-1, null);
                activity.setTitle(R.string.select_zone);
                return zoneSelector;

            case VIEW_ACCOUNT_SELECTOR:
                activity.bottomNav.setVisibility(View.GONE);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                activity.setTitle(R.string.select_account);
                return accountSelector;

            case VIEW_CLOUDFLARE_STATUS:
                activity.bottomNav.setVisibility(View.GONE);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                activity.setTitle(R.string.cloudflare_status);
                cloudflareStatus.lastView = actualView;
                return cloudflareStatus;

            case VIEW_CLOUDFLARE_BLOG:
                activity.bottomNav.setVisibility(View.GONE);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                activity.setTitle(R.string.cloudflare_blog);
                if (!actualView.equals(VIEW_CLOUDFLARE_POST)) cloudflareBlog.lastView = actualView;
                return cloudflareBlog;

            case VIEW_CLOUDFLARE_POST:
                activity.bottomNav.setVisibility(View.GONE);
                activity.setToolbarIcon(R.drawable.ic_arrow_left, null);
                cloudflarePost.lastView = actualView;
                if (data instanceof CFPost) {
                    cloudflarePost.setPost((CFPost) data);
                } else if (data instanceof String) {
                    cloudflarePost.setPost(null);
                    cloudflarePost.setPostToLoad((String) data);
                }
                return  cloudflarePost;

            default:
                return new FragmentCC();
        }
    }

    private void setBottomNavSelection(int id) {
        activity.bottomNav.setOnItemSelectedListener(null);
        activity.bottomNav.setOnItemReselectedListener(null);
        activity.bottomNav.setSelectedItemId(id);
        activity.bottomNav.setOnItemSelectedListener(this);
        activity.bottomNav.setOnItemReselectedListener(this::onNavigationItemSelected);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (zoneManager.selected == null) {
            activity.showAlert(new Alert(Alert.INFO, R.string.select_zone_first));
            return false;
        } else if (item.getItemId() == R.id.nav_dashboard) {
            setView(VIEW_DASHBOARD, null);
            return true;
        } else if (item.getItemId() == R.id.nav_dns) {
            setView(VIEW_DNS, null);
            return true;
        } else if (item.getItemId() == R.id.nav_community) {
            setView(VIEW_COMMUNITY, null);
            return true;
        } else if (item.getItemId() == R.id.nav_apps) {
            setView(VIEW_APPS, null);
            return true;
        } else if (item.getItemId() == R.id.nav_settings) {
            setView(VIEW_SETTINGS, null);
            return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();

        switch (actualView) {
            case VIEW_DASHBOARD:
                inflater.inflate(R.menu.dashboard, menu);
                return true;
            case VIEW_ZONE_SELECTOR:
            case VIEW_SETTINGS:
                inflater.inflate(R.menu.settings, menu);
                return true;
            case VIEW_DNS:
                inflater.inflate(R.menu.dns, menu);
                return true;
            case VIEW_CLOUDFLARE_STATUS:
                inflater.inflate(R.menu.cf_status, menu);
                return true;
            case VIEW_CLOUDFLARE_POST:
            case VIEW_COMMUNITY_TOPIC:
                inflater.inflate(R.menu.cf_post, menu);
                return true;
            case VIEW_COMMUNITY:
                inflater.inflate(R.menu.community, menu);
                return true;
            case VIEW_CLOUDFLARE_BLOG:
                return false;
            default:
                inflater.inflate(R.menu.swap_zone, menu);
                return true;
        }
    }

    public void resetAllView() {
        fragmentDNS = new FragmentDNS();
        fragmentAddDNS = new FragmentAddDNS();
        // fragmentWorld = new FragmentWorld(); // Removed FragmentWorld
        fragmentFirewall = new FragmentFirewall();
        fragmentCommunity = new FragmentCommunity();
        fragmentTopic = new FragmentTopic();
        fragmentSettings = new FragmentSettings();
        fragmentDashboard = new FragmentDashboard();
        zoneSelector = new FragmentZoneSelector();
        cloudflareStatus = new FragmentCloudflareStatus();
        cloudflareBlog = new FragmentCloudflareBlog();
        cloudflarePost = new FragmentCloudflarePost();
        fragmentNotifications = new FragmentNotifications();
    }

    public void onBackPressed(MainActivity activity) {
        historyManager.back(activity);
    }
}