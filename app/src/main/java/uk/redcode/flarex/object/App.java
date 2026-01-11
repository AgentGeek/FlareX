package uk.redcode.flarex.object;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.ui.ViewManager;

public class App {

    public static final int WHOIS = 0;
    public static final int FIREWALL = 1;
    public static final int HISTORY = 2;
    public static final int IPS = 3;
    public static final int INTELLIGENCE = 4;

    public int id;
    public int icon;
    public int label;
    public String view;
    public boolean requireAccount;

    public static ArrayList<App> getList() {
        return new ArrayList<App>() {{
           add(App.from(WHOIS));
           add(App.from(FIREWALL));
           add(App.from(HISTORY));
           add(App.from(IPS));
           add(App.from(INTELLIGENCE));
        }};
    }

    private static App from(int id) {
        App app = new App();
        app.id = id;
        app.icon = App.getIcon(id);
        app.label = App.getLabel(id);
        app.view = App.getView(id);
        app.requireAccount = App.requireAccount(id);
        return app;
    }

    private static boolean requireAccount(int id) {
        switch (id) {
            case WHOIS:
            case HISTORY:
            case INTELLIGENCE:
                return true;
            default: return false;
        }
    }

    private static int getIcon(int id) {
        switch (id) {
            case WHOIS: return R.drawable.ic_whois;
            case FIREWALL: return R.drawable.ic_firewall;
            case HISTORY: return R.drawable.ic_history;
            case IPS: return R.drawable.ic_ips;
            case INTELLIGENCE: return R.drawable.ic_intelligence;
            default: return R.drawable.ic_question;
        }
    }

    private static int getLabel(int id) {
        switch (id) {
            case WHOIS: return R.string.whois;
            case FIREWALL: return R.string.firewall;
            case HISTORY: return R.string.history;
            case IPS: return R.string.ip;
            case INTELLIGENCE: return R.string.intelligence;
            default: return R.string.question;
        }
    }

    private static String getView(int id) {
        switch (id) {
            case WHOIS: return ViewManager.VIEW_WHOIS;
            case FIREWALL: return ViewManager.VIEW_FIREWALL;
            case HISTORY: return ViewManager.VIEW_HISTORY;
            case IPS: return ViewManager.VIEW_IPS;
            case INTELLIGENCE: return ViewManager.VIEW_INTELLIGENCE;
            default: return ViewManager.VIEW_APPS;
        }
    }
}
