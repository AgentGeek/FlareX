package uk.redcode.flarex.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.CFAccount;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.User;

public class AccountManager {

    private final static String TAG = "AccountManager";
    private final MainActivity activity;
    public CFAccount selected = null;

    private boolean avatarMale = true;

    public AccountManager(MainActivity activity) {
        this.activity = activity;
        this.avatarMale = User.getAvatar(activity);
        load();
    }

    private void load() {
        if (!AppParameter.getBoolean(activity, AppParameter.REMEMBER_ACCOUNT, false)) return;

        // try load account
        CFAccount last = AppParameter.getLastAccount(activity);
        if (last == null) {
            Logger.info("Enable to load remembered account, load null");
            activity.showAlert(new Alert(Alert.ERROR, R.string.unable_load_last_account));
            return;
        }

        // set zone
        selected = last;
    }

    public void buildHeader(LinearLayout container, LayoutInflater inflater) {
        // load layout and fill it
        View view = inflater.inflate(R.layout.row_account_header, container, false);
        ((TextView) view.findViewById(R.id.account_name)).setText(getName());
        ((TextView) view.findViewById(R.id.account_label)).setText(getLabel());

        // build avatar
        ((ImageView) view.findViewById(R.id.user_avatar)).setImageResource(avatarMale ? R.drawable.ic_avatar : R.drawable.ic_avatar_female);
        view.findViewById(R.id.user_avatar).setOnClickListener(v -> {
            avatarMale = !avatarMale;
            ((ImageView) v).setImageResource(avatarMale ? R.drawable.ic_avatar : R.drawable.ic_avatar_female);
            User.setAvatar(activity, avatarMale);
        });

        // listen swap
        view.findViewById(R.id.select_account).setOnClickListener(view1 -> activity.viewManager.setView(ViewManager.VIEW_ACCOUNT_SELECTOR, null));

        // add view
        container.addView(view);
    }

    private String getName() {
        return selected == null ? activity.getString(R.string.no_account_selected) : selected.name.toLowerCase(Locale.ROOT);
    }

    private String getLabel() {
        return selected == null ? "" : selected.type;
    }

    public void setAccount(CFAccount account) {
        Logger.info(TAG, "Set account: "+account.name);
        selected = account;
        AppParameter.setLastAccount(activity, account);
        activity.bottomNav.setSelectedItemId(activity.bottomNav.getMenu().getItem(0).getItemId());
    }

    public boolean isAccountSelected() {
        return selected != null;
    }
}
