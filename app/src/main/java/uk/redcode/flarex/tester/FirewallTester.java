package uk.redcode.flarex.tester;

import android.content.Context;

import java.util.ArrayList;

import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.FirewallRule;
import uk.redcode.flarex.ui.LayoutManager;

public class FirewallTester extends Tester {

    private static final String key = LayoutManager.FIREWALL;
    private static final String keyEdit = LayoutManager.FIREWALL_EDIT;

    public FirewallTester(Context context) {
        super(context);
        this.name = "Firewall";
        this.permission = "Zone.Zone - Zone.Firewall Services";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);
        setLoading(true);

        CFApi.getFirewallRules(context, zone, new CFApi.RuleListener() {
            @Override
            public void onResult(ArrayList<FirewallRule> rules) {
                FirewallTester.this.icon = SUCCESS;
                FirewallTester.this.result = "Can read firewall rules";
                setLoading(false);
                listener.onFinish(zone);
            }

            @Override
            public void onError(Exception e) {
                FirewallTester.this.icon = WARNING;
                FirewallTester.this.result = "Can't read firewall rules";
                setLayout(key, keyEdit, false);
                setLoading(false);
                listener.onFinish(zone);
            }
        });
    }

}
