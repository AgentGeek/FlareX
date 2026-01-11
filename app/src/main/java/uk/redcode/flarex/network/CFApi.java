package uk.redcode.flarex.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.object.AuditLog;
import uk.redcode.flarex.object.CFAccount;
import uk.redcode.flarex.object.Intelligence;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.OriginCertificate;
import uk.redcode.flarex.object.RuleSet;
import uk.redcode.flarex.object.User;
import uk.redcode.flarex.object.Certificate;
import uk.redcode.flarex.object.DNSRecord;
import uk.redcode.flarex.object.FirewallRule;
import uk.redcode.flarex.object.Mode;
import uk.redcode.flarex.object.Notification;
import uk.redcode.flarex.object.Whois;
import uk.redcode.flarex.object.Zone;

public class CFApi {

    private static final String TAG = "CFApi";

    public static final int TYPE_MASTER_KEY = 0;
    public static final int TYPE_TOKEN = 1;

    public static final String API_URL = "https://api.cloudflare.com/client/v4";
    public static final String STATUS_URL = "https://www.cloudflarestatus.com/";
    public static final String BLOG_URL = "https://blog.cloudflare.com/page/";

    private static final RequestQueue requestQ = initRequestQueue();

    /*
        Listener
     */

    public interface TestListener {
        void onResult(boolean connected);
        void onError(Exception e);
    }
    public interface ZoneListener {
        void onResult(ArrayList<Zone> zones);
        void onError(Exception e);
    }
    public interface DNSListener {
        void onResult(ArrayList<DNSRecord> records);
        void onError(Exception e);
    }
    public interface CertificateListener {
        void onResult(ArrayList<Certificate> certificates);
        void onError(Exception e);
    }
    public interface OriginCertificateListener {
        void onResult(ArrayList<OriginCertificate> certificates);
        void onError(Exception e);
    }
    public interface ModeListener {
        void onResult(Mode devMode);
        void onError(Exception e);
    }
    public interface RuleListener {
        void onResult(ArrayList<FirewallRule> rules);
        void onError(Exception e);
    }
    public interface JSONListener {
        void onResult(JSONObject body) throws JSONException;
        void onError(Exception e);
    }
    public interface HTMLListener {
        void onResult(String html);
        void onError(Exception e);
    }
    public interface NotificationListener {
        void onResult(ArrayList<Notification> notifications);
        void onError(Exception e);
    }
    public interface StringListener {
        void onResult(String result);
        void onError(Exception e);
    }

    public interface AccountListener {
        void onResult(ArrayList<CFAccount> accounts);
        void onError(Exception e);
    }

    public interface WhoisListener {
        void onResult(Whois whois);
        void onError(Exception e);
    }

    public interface AuditLogListener {
        void onResult(ArrayList<AuditLog> logs);
        void onError(Exception e);
    }

    public interface IntelligenceListener {
        void onResult(Intelligence intel);
        void onError(Exception e);
    }

    public interface  RuleSetListener {
        void onResult(ArrayList<RuleSet> ruleSets);
        void onError(Exception e);
    }

    /*
        Core
     */

    private static RequestQueue initRequestQueue() {
        RequestQueue rq = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        rq.start();
        return rq;
    }

    public static void testAndSave(Context context, String email, String apikey, TestListener listener) {
        final String url = API_URL + "/zones";

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) {
                    if (body.getJSONArray("errors").length() > 0) listener.onError(new Exception(body.getJSONArray("errors").getJSONObject(0).getString("message")));
                    else listener.onError(new Exception("Error reading cloudflare response"));
                } else {
                    saveCredential(context, email, apikey, CFApi.TYPE_MASTER_KEY);
                    listener.onResult(true);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                listener.onError(e);
            }
        });

        r.email = email;
        r.apikey = apikey;
        requestQ.add(r);
    }

    public static void verifyToken(Context context, String token, TestListener listener) {
        final String url = API_URL + "/user/tokens/verify";

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) {
                    if (body.getJSONArray("errors").length() > 0) Toast.makeText(context, body.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show();
                    else Toast.makeText(context, "Error reading cloudflare response", Toast.LENGTH_SHORT).show();
                    listener.onError(new Exception("Error reading cloudflare response"));
                } else {
                    saveCredential(context, "Token User", token, CFApi.TYPE_TOKEN);
                    listener.onResult(true);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                listener.onError(e);
            }
        });

        r.apikey = token;
        r.mode = CFApi.TYPE_TOKEN;
        requestQ.add(r);
    }

    public static void saveCredential(Context context, String email, String apikey, int mode) {
        Logger.info(TAG, "Saving credentials ...");
        SharedPreferences.Editor editor = context.getSharedPreferences("USER", Context.MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.putString("apikey", apikey);
        editor.putInt("mode", mode);
        editor.apply();
        Logger.info(TAG, "Credentials saved !");
    }

    /*
        Account
     */

    public static void getAccountId(Context context, StringListener listener) {
        final String url = API_URL + "/memberships";

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(User.parseAccountId(body, context));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void getAccounts(Context context, CFApi.AccountListener listener) {
        final String url = API_URL + "/accounts";

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(CFAccount.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Status
    */

    public static void getStatus(Context context, HTMLListener listener) {
        StringRequest r = new StringRequest(Request.Method.GET, STATUS_URL, listener::onResult, error -> {
            Logger.error(error);
            Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            listener.onError(error);
        });

        requestQ.add(r);
    }

    public static void getBlogPosts(Context context, int page, HTMLListener listener) {
        String url = BLOG_URL + page;

        StringRequest r = new StringRequest(Request.Method.GET, url, listener::onResult, error -> {
            Logger.error(error);
            Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            listener.onError(error);
        });

        requestQ.add(r);
    }

    public static void getBlogPost(Context context, String url, HTMLListener listener) {
        StringRequest r = new StringRequest(Request.Method.GET, url, listener::onResult, error -> {
            Logger.error(error);
            Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            listener.onError(error);
        });

        requestQ.add(r);
    }

    /*
        Zones
     */

    public static void getZones(Context context, ZoneListener listener) {
        final String url = API_URL + "/zones?per_page=200";

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Zone.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

     /*
        DNS Record
     */

    public static void getDNSRecords(Context context, String zoneId, DNSListener listener) {
        final String url = String.format("%s/zones/%s/dns_records", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(DNSRecord.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void addDNSRecords(Context context, String zoneId, JSONObject record, JSONListener listener) {
        final String url = String.format("%s/zones/%s/dns_records", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.POST, url, record, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(body);
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void editDNSRecord(Context context, String zoneId, JSONObject record, String recordId, JSONListener listener) {
        final String url = String.format("%s/zones/%s/dns_records/%s", API_URL, zoneId, recordId);

        CFRequest r = new CFRequest(context, Request.Method.PATCH, url, record, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(body);
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void deleteDNSRecord(Context context, String zoneId, DNSRecord record, JSONListener listener) {
        final String url = String.format("%s/zones/%s/dns_records/%s", API_URL, zoneId, record.recordId);

        CFRequest r = new CFRequest(context, Request.Method.DELETE, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(body);
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Notifications
     */

    public static void getNotifications(Context context, NotificationListener listener) {
        final String url = String.format("%s/accounts/%s/alerting/v3/history?page=1&per_page=30", API_URL, User.getAccountId(context));

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Notification.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Graphql
     */

    public static void graphql(Context context, JSONObject data, JSONListener listener) {
        final String url = API_URL+"/graphql";

        CFRequest r = new CFRequest(context, Request.Method.POST, url, data, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (body.isNull("data")) {
                    Logger.warning(TAG, "Graphql data is null");
                    Toast.makeText(context, "Graphql data is null", Toast.LENGTH_LONG).show();
                    listener.onError(new Exception("Graphql data is null"));
                } else if (!body.getJSONObject("data").has("viewer")) {
                    Logger.warning(TAG, "No viewer body");
                    Toast.makeText(context, "No viewer body", Toast.LENGTH_LONG).show();
                    listener.onError(new Exception("No viewer body"));
                } else {
                    listener.onResult(body);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Firewall
     */

    public static void getFirewallRules(Context context, String zoneId, RuleListener listener) {
        final String url = String.format("%s/zones/%s/firewall/rules?per_page=100", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {

                listener.onResult(FirewallRule.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void addFirewallRule(Context context, String zoneId, JSONObject rule, JSONListener listener) {
        final String url = String.format("%s/zones/%s/firewall/access_rules/rules", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.POST, url, rule, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(body);
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void getZoneRuleSet(Context context, String zoneId, RuleSetListener listener) {
        final String url = String.format("%s/zones/%s/rulesets", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(RuleSet.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void getZoneRuleDetail(Context context, String zoneId, String ruleSetId, RuleListener listener) {
        final String url = String.format("%s/zones/%s/rulesets/%s", API_URL, zoneId, ruleSetId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(FirewallRule.parse(body.getJSONObject("result").getJSONArray("rules")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Settings
     */

    public static void getSetting(Context context, String zoneId, String key, JSONListener listener) {
        final String url = String.format("%s/zones/%s/settings/%s", API_URL, zoneId, key);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(body.getJSONObject("result"));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void setSetting(Context context, Zone zone, String key, String value, JSONListener listener, String valueKey, String requiredPlan) {
        final String url = String.format("%s/zones/%s/settings/%s", API_URL, zone.zoneId, key);

        try {
            JSONObject data = new JSONObject();
            data.put(valueKey, value);
            sendPatchSettings(context, url, data, listener, zone, requiredPlan);
        } catch (JSONException e) {
            Logger.error(e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            listener.onError(e);
        }
    }

    public static void setSetting(Context context, Zone zone, String key, boolean value, JSONListener listener, String valueKey, String requiredPlan) {
        final String url = String.format("%s/zones/%s/settings/%s", API_URL, zone.zoneId, key);

        try {
            JSONObject data = new JSONObject();
            data.put(valueKey, value);
            sendPatchSettings(context, url, data, listener, zone, requiredPlan);
        } catch (JSONException e) {
            Logger.error(e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            listener.onError(e);
        }
    }

    public static void setSetting(Context context, Zone zone, String key, JSONObject value, JSONListener listener, String valueKey, String requiredPlan) {
        final String url = String.format("%s/zones/%s/settings/%s", API_URL, zone.zoneId, key);

        try {
            JSONObject data = new JSONObject();
            data.put(valueKey, value);
            sendPatchSettings(context, url, data, listener, zone, requiredPlan);
        } catch (JSONException e) {
            Logger.error(e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            listener.onError(e);
        }
    }

    public static void setSetting(Context context, Zone zone, String key, int value, JSONListener listener, String valueKey, String requiredPlan) {
        final String url = String.format("%s/zones/%s/settings/%s", API_URL, zone.zoneId, key);

        try {
            JSONObject data = new JSONObject();
            data.put(valueKey, value);
            sendPatchSettings(context, url, data, listener, zone, requiredPlan);
        } catch (JSONException e) {
            Logger.error(e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            listener.onError(e);
        }
    }

    private static void sendPatchSettings(Context context, String url, JSONObject data, JSONListener listener, Zone zone, String requiredPlan) {
        if (!zone.hasRequiredPlan(requiredPlan, context)) {
            listener.onError(new Exception("This zone has not the required plan"));
            return;
        }

        CFRequest r = new CFRequest(context, Request.Method.PATCH, url, data, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                else listener.onResult(body);
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Cache
     */

    public static void purgeCache(Context context, String zoneId, JSONListener listener) {
        final String url = String.format("%s/zones/%s/purge_cache", API_URL, zoneId);

        try {
            JSONObject data = new JSONObject();
            data.put("purge_everything", true);

            CFRequest r = new CFRequest(context, Request.Method.POST, url, data, new CFRequest.Listener() {
                @Override
                public void onResult(JSONObject body) throws JSONException {
                    if (!body.getBoolean("success")) listener.onError(new Exception(body.toString()));
                    else listener.onResult(body);
                }

                @Override
                public void onError(Exception e) {
                    Logger.error(e);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    listener.onError(e);
                }
            });

            r.load(context);
            requestQ.add(r);
        } catch (JSONException e) {
            Logger.error(e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            listener.onError(e);
        }
    }

    /*
        Certificates
     */

    public static void getEdgeCertificates(Context context, String zoneId, CertificateListener listener) {
        final String url = String.format("%s/zones/%s/ssl/certificate_packs", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Certificate.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    public static void getOriginCertificates(Context context, String zoneId, OriginCertificateListener listener) {
        final String url = String.format("%s/certificates?zone_id=%s", API_URL, zoneId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(OriginCertificate.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        WHOIS
     */

    public static void whois(Context context, String accountId, String domain, WhoisListener listener) {
        final String url = String.format("%s/accounts/%s/intel/whois?domain=%s", API_URL, accountId, domain);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Whois.parse(body.getJSONObject("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Audit Logs
     */

    public static void getAuditLogs(Context context, String accountId, AuditLogListener listener) {
        final String url = String.format("%s/accounts/%s/audit_logs", API_URL, accountId);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(AuditLog.parse(body.getJSONArray("result")));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Cloudflare IP
     */

    public static void getCloudflareIPs(Context context, JSONListener listener) {
        final String url = String.format("%s/ips", API_URL);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(body);
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

    /*
        Intelligence
     */

    public static void getIntelligence(Context context, String accountId, int type, String value, IntelligenceListener listener) {
        final String url = String.format("%s/accounts/%s/intel/%s", API_URL, accountId, Intelligence.getEndURL(type, value));

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Intelligence.parse(type, body));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });

        r.load(context);
        requestQ.add(r);
    }

}