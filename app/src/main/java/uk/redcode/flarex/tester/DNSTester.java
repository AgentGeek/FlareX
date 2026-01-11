package uk.redcode.flarex.tester;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.DNSRecord;
import uk.redcode.flarex.ui.LayoutManager;

public class DNSTester extends Tester {

    static private final String key = LayoutManager.DNS;
    static private final String keyEdit = LayoutManager.DNS_EDIT;

    public DNSTester(Context context) {
        super(context);
        this.name = "DNS";
        this.permission = "Zone.DNS";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);
        setLoading(true);

        CFApi.getDNSRecords(context, zone, new CFApi.DNSListener() {
            @Override
            public void onResult(ArrayList<DNSRecord> records) {
                DNSTester.this.icon = WARNING;
                DNSTester.this.result = "Can read dns records";
                setLayout(key, true); // Found bug: key should be set to true
                setLayout(keyEdit, false);
                setLoading(false);
                listener.onFinish(zone);
                if (records.size() <= 0) return;
                tryEdit(zone, listener);
            }

            @Override
            public void onError(Exception e) {
                DNSTester.this.icon = ERROR;
                DNSTester.this.result = "Error reading dns records";
                setLayout(key, false);
                setLayout(keyEdit, false);
                setLoading(false);
                listener.onFinish(zone);
            }
        });
    }

    private void tryEdit(String zone, TestListener listener) {
        try {
            CFApi.addDNSRecords(context, zone, getData(), new CFApi.JSONListener() {
                @Override
                public void onResult(JSONObject body) {
                    try {
                        removeRecord(zone, listener, body.getJSONObject("result").getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        DNSTester.this.icon = ERROR;
                        DNSTester.this.result = "Can read dns records, error removing test record";
                        setLayout(keyEdit, false);
                        setLoading(false);
                        listener.onFinish(zone);
                    }
                }

                @Override
                public void onError(Exception e) {
                    DNSTester.this.icon = WARNING;
                    DNSTester.this.result = "Can read dns records, no edit permission";
                    setLayout(keyEdit, false);
                    setLoading(false);
                    listener.onFinish(zone);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            this.icon = WARNING;
            this.result = "Can read dns records";
            setLayout(keyEdit, false);
            setLoading(false);
            listener.onFinish(zone);
        }

    }

    private void removeRecord(String zone, TestListener listener, String recordId) {
        DNSRecord record = new DNSRecord();
        record.recordId = recordId;

        CFApi.deleteDNSRecord(context, zone, record, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                DNSTester.this.icon = SUCCESS;
                DNSTester.this.result = "Can read and edit dns records";
                setLayout(keyEdit, true); // Mark as editable
                setLoading(false);
                listener.onFinish(zone);
            }

            @Override
            public void onError(Exception e) {
                DNSTester.this.icon = ERROR;
                DNSTester.this.result = "Can read dns records, error removing test record";
                setLoading(false);
                listener.onFinish(zone);
            }
        });
    }

    private JSONObject getData() throws JSONException {
        JSONObject record = new JSONObject();
        record.put("type", "CNAME");
        record.put("content", "example.com");
        record.put("name", "__flarex__");
        record.put("ttl", 1);
        record.put("proxied", true);
        return record;
    }

}