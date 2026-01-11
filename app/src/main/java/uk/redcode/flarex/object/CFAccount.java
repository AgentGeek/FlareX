package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CFAccount {

    public String id;
    public String name;
    public String type;
    public String creation;
    public Quota quota;
    public String raw;

    public static ArrayList<CFAccount> parse(JSONArray list) throws JSONException {
        ArrayList<CFAccount> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static CFAccount parse(JSONObject data) throws JSONException {
        CFAccount account = new CFAccount();

        account.id = data.getString("id");
        account.name = data.getString("name");
        account.type = data.getString("type");
        account.creation = data.getString("created_on");
        account.quota = Quota.parse(data.getJSONObject("legacy_flags").getJSONObject("enterprise_zone_quota"));
        account.raw = data.toString();

        return account;
    }

    static class Quota {

        public int max = 0;
        public int current = 0;
        public int available = 0;

        public static Quota parse(JSONObject data) throws JSONException {
            Quota q = new Quota();

            q.max = data.getInt("maximum");
            q.current = data.getInt("current");
            q.available = data.getInt("available");

            return q;
        }

    }

}
