package in.ankrypt.flarex.tester;

import android.content.Context;

import java.util.ArrayList;

import in.ankrypt.flarex.adapter.TokenTestAdapter;
import in.ankrypt.flarex.network.CFApi;
import in.ankrypt.flarex.object.Zone;

public class ZoneTester extends Tester {

    public ZoneTester(Context context) {
        super(context);
        this.name = "Zone";
        this.permission = "";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);
        setLoading(true);

        CFApi.getZones(context, new CFApi.ZoneListener() {
            @Override
            public void onResult(ArrayList<Zone> zones) {
                String zoneId = "";
                if (zones.size() <= 0) {
                    ZoneTester.this.icon = WARNING;
                    ZoneTester.this.result = "No zone found";
                } else {
                    ZoneTester.this.icon = SUCCESS;
                    ZoneTester.this.result = "At least one zone was seen";
                    zoneId = zones.get(0).zoneId;
                }
                setLoading(false);
                listener.onFinish(zoneId);
            }

            @Override
            public void onError(Exception e) {
                ZoneTester.this.icon = ERROR;
                ZoneTester.this.result = "Error loading your zone";
                setLoading(false);
                listener.onFinish("");
            }
        });
    }
}
