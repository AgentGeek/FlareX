package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;

public class FirewallEvent {

    private static final String TAG = "FirewallEvent";

    public String action;
    public String date;

    public String clientIp;
    public String clientAgent;
    public String clientCountry;

    public String requestHost;
    public String requestPath;
    public String requestQuery;
    public String requestMethod;
    public String requestProtocol;

    public static final String DATE = "date";
    public static final String REQUEST_HOST = "request-host";
    public static final String REQUEST_PATH = "request-path";
    public static final String REQUEST_QUERY = "request-query";
    public static final String REQUEST_METHOD = "request-method";
    public static final String REQUEST_PROTOCOL = "request-protocol";
    public static final String CLIENT_COUNTRY = "client-country";
    public static final String CLIENT_IP = "client-ip";
    public static final String CLIENT_AGENT = "client-agent";
    public static final String ACTION = "action";

    public static ArrayList<FirewallEvent> parse(JSONArray list) throws JSONException {
        ArrayList<FirewallEvent> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static FirewallEvent parse(JSONObject data) throws JSONException {
        FirewallEvent f = new FirewallEvent();

        f.action = data.getString("action");
        f.date = data.getString("datetime");

        f.clientCountry = data.getString("clientCountryName");
        f.clientIp = data.getString("clientIP");
        f.clientAgent = data.getString("userAgent");

        f.requestHost = data.getString("clientRequestHTTPHost");
        f.requestMethod = data.getString("clientRequestHTTPMethodName");
        f.requestProtocol = data.getString("clientRequestHTTPProtocol");
        f.requestPath = data.getString("clientRequestPath");
        f.requestQuery = data.getString("clientRequestQuery");

        return f;
    }

    public String getParam(String key) {
        switch (key) {
            case REQUEST_HOST: return requestHost;
            case REQUEST_PATH: return requestPath;
            case REQUEST_QUERY: return requestQuery;
            case REQUEST_METHOD: return requestMethod;
            case REQUEST_PROTOCOL: return requestProtocol;
            case CLIENT_COUNTRY: return clientCountry;
            case CLIENT_IP: return clientIp;
            case CLIENT_AGENT: return clientAgent;
            case ACTION: return action;
            case DATE: return date;
            default:
                Logger.warning(TAG, "Param ?? -> "+key);
                return "??";
        }
    }

    /*
        Static
     */

    public static ArrayList<String> getAvailableParams() {
        return new ArrayList<String>() {{
            add(DATE);
            add(REQUEST_HOST);
            add(REQUEST_PATH);
            add(REQUEST_QUERY);
            add(REQUEST_METHOD);
            add(REQUEST_PROTOCOL);
            add(CLIENT_COUNTRY);
            add(CLIENT_IP);
            add(CLIENT_AGENT);
            add(ACTION);
        }};
    }

    public static int getParamTitle(String key) {
        switch (key) {
            case REQUEST_HOST: return R.string.host;
            case REQUEST_PATH: return R.string.path;
            case REQUEST_QUERY: return R.string.query;
            case REQUEST_METHOD: return R.string.method;
            case REQUEST_PROTOCOL: return R.string.protocol;
            case CLIENT_COUNTRY: return R.string.country_code;
            case CLIENT_IP: return R.string.ip;
            case CLIENT_AGENT: return R.string.agent;
            case ACTION: return R.string.action;
            case DATE: return R.string.date;
            default:
                Logger.warning(TAG, "ParamTitle ?? -> "+key);
                return R.string.question;
        }
    }
}
