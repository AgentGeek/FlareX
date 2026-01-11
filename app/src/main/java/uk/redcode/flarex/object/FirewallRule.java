package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;

public class FirewallRule {

    public String id;
    public String version;
    public String action;
    public String expression;
    public String parameters;
    public String description;
    public String last_updated;
    public String ref;
    public ArrayList<String> categories = new ArrayList<>();
    public boolean enabled;

    public String getContent() {
        if (!expression.isEmpty()) return expression;
        if (!parameters.isEmpty()) return parameters;
        return "";
    }

    public int getActionLabel() {
        return getActionLabel(action);
    }

    public static ArrayList<FirewallRule> parse(JSONArray list) throws JSONException {
        ArrayList<FirewallRule> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static FirewallRule parse(JSONObject data) throws JSONException {
        FirewallRule r = new FirewallRule();

        r.id = data.getString("id");
        r.version = data.getString("version");
        r.action = data.getString("action");
        r.expression = data.has("expression") ? data.getString("expression") : "";
        r.parameters = data.has("action_parameters") ? data.getJSONObject("action_parameters").toString(2) : "";
        r.description = data.getString("description");
        r.last_updated = data.getString("last_updated");
        r.ref = data.getString("ref");
        r.categories = data.has("categories") ? Parser.parseStringList(data.getJSONArray("categories")) : new ArrayList<>();
        r.enabled = data.getBoolean("enabled");

        return r;
    }

    public static int getActionLabel(String action) {
        switch (action) {
            case "challenge": return R.string.action_challenge;
            case "js_challenge": return R.string.action_js_challenge;
            case "managed_challenge": return R.string.action_managed_challenge;
            case "block": return R.string.action_block;
            case "skip": return R.string.action_skip;
            case "log": return R.string.action_log;
            case "execute": return R.string.action_execute;
            case "rewrite": return R.string.action_rewrite;
            case "redirect": return R.string.action_redirect;
            case "route": return R.string.action_route;
            case "set_config": return R.string.action_set_config;
            default: return R.string.question;
        }
    }

}