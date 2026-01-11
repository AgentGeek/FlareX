package uk.redcode.flarex.tester;

import android.content.Context;

import java.util.ArrayList;

import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Zone;

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
