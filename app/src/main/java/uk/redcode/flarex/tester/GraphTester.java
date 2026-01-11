package uk.redcode.flarex.tester;

import android.content.Context;

import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.module.TimeRange;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.ui.LayoutManager;

public class GraphTester extends Tester {

    private static final String key = LayoutManager.ANALYTICS;

    public GraphTester(Context context) {
        super(context);
        this.name = "GraphQL";
        this.permission = "Zone.Analytics";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);
        setLoading(true);

        try {
            JSONObject data = FragmentDashboard.getDashboardData(
                    context,
                    R.raw.graphql_dashboard,
                    TimeRange.getRange(TimeRange.TIME_24HOURS),
                    zone,
                    TimeRange.TIME_24HOURS
            );

            CFApi.graphql(context, data, new CFApi.JSONListener() {
                @Override
                public void onResult(JSONObject body) {
                    GraphTester.this.icon = SUCCESS;
                    GraphTester.this.result = "Analytics can be read";
                    setLoading(false);
                    listener.onFinish(zone);
                }

                @Override
                public void onError(Exception e) {
                    GraphTester.this.icon = ERROR;
                    GraphTester.this.result = "No analytics permission";
                    setLayout(key, false);
                    setLoading(false);
                    listener.onFinish(zone);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            this.result = "Error parsing GraphQL data";
            this.icon = ERROR;
            setLoading(false);
            listener.onFinish(zone);
            setLayout(key, false);
        }
    }

}
