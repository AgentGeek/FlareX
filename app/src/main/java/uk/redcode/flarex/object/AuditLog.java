package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AuditLog {

    public boolean result;
    public String type;
    public String date;
    public String interfaceType;
    public String newValue;
    public String oldValue;
    public Actor actor;
    public HashMap<String, String> metadata;
    public HashMap<String, String> oldValueJson = null;
    public HashMap<String, String> newValueJson = null;

    public static ArrayList<AuditLog> parse(JSONArray list) throws JSONException {
        ArrayList<AuditLog> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static AuditLog parse(JSONObject data) throws JSONException {
        AuditLog log = new AuditLog();

        log.result = data.getJSONObject("action").getBoolean("result");
        log.type = data.getJSONObject("action").getString("type");
        log.date = data.getString("when");
        log.interfaceType = data.getString("interface");
        log.metadata = Parser.jsonToHashMap(data.getJSONObject("metadata"));

        log.oldValue = data.getString("oldValue");
        log.newValue = data.getString("newValue");

        log.oldValueJson = data.has("oldValueJson") ? Parser.jsonToHashMap(data.getJSONObject("oldValueJson")) : null;
        log.newValueJson = data.has("newValueJson") ? Parser.jsonToHashMap(data.getJSONObject("newValueJson")) : null;

        log.actor = Actor.parse(data.getJSONObject("actor"));

        return log;
    }

    public static class Actor {

        public String email;
        public String id;
        public String ip;
        public String type;

        public static Actor parse(JSONObject data) throws JSONException {
            Actor a = new Actor();
            a.email = data.has("email") ? data.getString("email") : "";
            a.id = data.getString("id");
            a.ip = data.has("ip") ? data.getString("ip") : "";
            a.type = data.getString("type");
            return a;
        }

    }

}