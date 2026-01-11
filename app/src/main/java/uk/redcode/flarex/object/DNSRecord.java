package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;

public class DNSRecord {

    public String recordId;
    public String name;
    public String type;
    public String content;

    public boolean proxiable;
    public boolean proxied;
    public boolean locked;

    public int ttl;

    public static ArrayList<DNSRecord> parse(JSONArray list) throws JSONException {
        ArrayList<DNSRecord> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static DNSRecord parse(JSONObject data) throws JSONException {
        DNSRecord r = new DNSRecord();

        r.recordId = data.getString("id");
        r.name = data.getString("name");
        r.type = data.getString("type");
        r.content = data.getString("content");

        r.proxiable = data.optBoolean("proxiable", false);
        r.proxied = data.optBoolean("proxied", false);
        r.locked = data.optBoolean("locked", false);

        r.ttl = data.getInt("ttl");
        return r;
    }

    public String getTTL() {
        return ttl == 1 ? "Auto" : String.valueOf(ttl);
    }

    public int getProxiedImg() {
        return proxied ? R.drawable.ic_proxied : R.drawable.ic_no_proxy;
    }
}