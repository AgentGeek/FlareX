package uk.redcode.flarex.object;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;

public class RuleSet {

    public String id;
    public String name;
    public String description;
    public String source;
    public String kind;
    public String version;
    public String last_updated;
    public String phase;
    public boolean needUpdate = true;
    public ArrayList<FirewallRule> rules = new ArrayList<FirewallRule>();

    public interface RuleListener {
        void onRuleRefreshed();
        void onError(Exception e);
    }

    public int getPhaseLabel() {
        return getPhaseLabel(phase);
    }

    public static ArrayList<RuleSet> parse(JSONArray list) throws JSONException {
        ArrayList<RuleSet> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    private static RuleSet parse(JSONObject data) throws JSONException {
        RuleSet r = new RuleSet();
        r.id = data.getString("id");
        r.name = data.getString("name");
        r.description = data.getString("description");
        r.source = data.has("source") ? data.getString("source") : "";
        r.kind = data.getString("kind");
        r.version = data.getString("version");
        r.last_updated = data.getString("last_updated");
        r.phase = data.getString("phase");
        r.needUpdate = true;
        return r;
    }

    public void refreshRules(Context context, String zoneId, RuleListener listener) {
        CFApi.getZoneRuleDetail(context, zoneId, id, new CFApi.RuleListener() {
            @Override
            public void onResult(ArrayList<FirewallRule> rules) {
                RuleSet.this.rules = rules;
                RuleSet.this.needUpdate = false;
                listener.onRuleRefreshed();
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    public static int getPhaseLabel(String phase) {
        switch (phase) {
            case "ddos_l4": return R.string.phase_ddos_l4;
            case "magic_transit": return R.string.phase_magic_transit;
            case "http_request_sanitize": return R.string.phase_http_request_sanitize;
            case "http_request_transform": return R.string.phase_http_request_transform;
            case "http_request_origin": return R.string.phase_http_request_origin;
            case "http_request_cache_settings": return R.string.phase_http_request_cache_settings;
            case "http_config_settings": return R.string.phase_http_config_settings;
            case "http_request_dynamic_redirect": return R.string.phase_http_request_dynamic_redirect;
            case "ddos_l7": return R.string.phase_ddos_l7;
            case "http_request_firewall_custom": return R.string.phase_http_request_firewall_custom;
            case "http_ratelimit": return R.string.phase_http_ratelimit;
            case "http_request_firewall_managed": return R.string.phase_http_request_firewall_managed;
            case "http_request_sbfm": return R.string.phase_http_request_sbfm;
            case "http_request_redirect": return R.string.phase_http_request_redirect;
            case "http_request_late_transform": return R.string.phase_http_request_late_transform;
            case "http_custom_errors": return R.string.phase_http_custom_errors;
            case "http_response_headers_transform": return R.string.phase_http_response_headers_transform;
            case "http_response_firewall_managed": return R.string.phase_http_response_firewall_managed;
            case "http_log_custom_fields": return R.string.phase_http_log_custom_fields;
            default: return R.string.question;
        }
    }
}