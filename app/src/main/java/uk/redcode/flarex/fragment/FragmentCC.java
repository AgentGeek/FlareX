package uk.redcode.flarex.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CFAccount;
import uk.redcode.flarex.object.Zone;
import uk.redcode.flarex.ui.AccountManager;
import uk.redcode.flarex.ui.Alert;
import uk.redcode.flarex.ui.ZoneManager;

public class FragmentCC extends Fragment {

    public ViewGroup root = null;
    public Zone zone = null;
    public CFAccount account = null;

    public boolean enableBackView = false;
    public String lastView = "";

    public void setLoading(boolean loading) {
        if (!isVisible()) return;
        ((MainActivity) requireActivity()).setLoading(loading);
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public void setTitle(String title) { ((MainActivity) requireActivity()).setTitle(title); }

    public void setTitle(int title) { ((MainActivity) requireActivity()).setTitle(title); }

    public void changeZone(Zone zone) {
        getZoneManager().setZone(zone);
        this.zone = zone;
    }

    public void changeAccount(CFAccount account) {
        getAccountManager().setAccount(account);
        this.account = account;
    }

    public void setView(String view, Object data) {
        ((MainActivity) requireActivity()).viewManager.setView(view, data);
    }

    public void drawError(int img, int str) {
        drawError(img, getString(str));
    }

    public void drawError(int img, String str) {
        if (root == null) return;

        View error = getLayoutInflater().inflate(R.layout.fragment_error, root, false);

        ((ImageView) error.findViewById(R.id.error_img)).setImageResource(img);
        ((TextView) error.findViewById(R.id.error_text)).setText(str);

        root.removeAllViews();
        root.addView(error);
        setLoading(false);
    }

    public MainActivity getMain() {
        return ((MainActivity) requireActivity());
    }

    public AccountManager getAccountManager() { return ((MainActivity) requireActivity()).accountManager; }

    public String getAccountId() { return getAccountManager().selected == null ? "" : getAccountManager().selected.id; }

    public ZoneManager getZoneManager() { return ((MainActivity) requireActivity()).zoneManager; }

    public void alert(Alert alert) {
        getMain().showAlert(alert);
    }

}
