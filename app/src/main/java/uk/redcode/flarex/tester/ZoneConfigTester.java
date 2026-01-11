package uk.redcode.flarex.tester;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.module.DevMode;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Zone;
import uk.redcode.flarex.ui.LayoutManager;

public class ZoneConfigTester extends Tester {

    static private final String key = LayoutManager.ZONE_CONFIG;
    static private final String keyEdit = LayoutManager.ZONE_CONFIG_EDIT;

    public ZoneConfigTester(Context context) {
        super(context);
        this.name = "Settings";
        this.permission = "Zone.Zone Settings";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);
        setLoading(true);

        CFApi.getSetting(context, zone, DevMode.KEY, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                try {
                    boolean value = Parser.parseBoolean(body.getString("value"));
                    tryEdit(zone, listener, value);
                } catch (JSONException e) {
                    ZoneConfigTester.this.icon = ERROR;
                    ZoneConfigTester.this.result = "Error reading config";
                    setLayout(key, keyEdit, false);
                    setLoading(false);
                    listener.onFinish(zone);
                }
            }

            @Override
            public void onError(Exception e) {
                ZoneConfigTester.this.icon = ERROR;
                ZoneConfigTester.this.result = "No read permission";
                setLayout(key, keyEdit, false);
                setLoading(false);
                listener.onFinish(zone);
            }
        });
    }

    private void tryEdit(String zone, TestListener listener, boolean value) {
        Zone z = new Zone();
        z.zoneId = zone;
        z.planId = Zone.PLAN_FREE;

        CFApi.setSetting(context, z, DevMode.KEY, Parser.convertBoolean(!value), new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                reverseChange(z, listener, value);
            }

            @Override
            public void onError(Exception e) {
                ZoneConfigTester.this.icon = WARNING;
                ZoneConfigTester.this.result = "Can read config, no edit permission";
                setLayout(keyEdit, false);
                setLoading(false);
                listener.onFinish(zone);
            }
        }, "value", Zone.PLAN_FREE);
    }

    private void reverseChange(Zone zone, TestListener listener, boolean value) {
        CFApi.setSetting(context, zone, DevMode.KEY, Parser.convertBoolean(value), new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                ZoneConfigTester.this.icon = SUCCESS;
                ZoneConfigTester.this.result = "Can read and edit config";
                setLoading(false);
                listener.onFinish(zone.zoneId);
            }

            @Override
            public void onError(Exception e) {
                ZoneConfigTester.this.icon = ERROR;
                ZoneConfigTester.this.result = "Can read config, error reversing value";
                setLayout(keyEdit, false);
                setLoading(false);
                listener.onFinish(zone.zoneId);
            }
        }, "value", Zone.PLAN_FREE);
    }

}
