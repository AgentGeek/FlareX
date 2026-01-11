package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Intelligence {

    public static final int DOMAIN = 1;
    public static final int IPV4 = 2;
    public static final int IPV6 = 3;

    public int type;
        public String asked;
        public ArrayList<Category> risks = new ArrayList<>();
    // only related to ip not domain
    public IPRef ipref;
        public ArrayList<String> ptrDomains = new ArrayList<>();
        public ArrayList<String> reservations = new ArrayList<>();
    // only related to domain not ip
        public String rank;
        public String riskScore;
         public String application;
        public ArrayList<String> refs = new ArrayList<>();
        public ArrayList<Category> categories = new ArrayList<>();

    public Intelligence(int type) {
        this.type = type;
    }

    public static String getEndURL(int type, String value) {
        if (type == DOMAIN) return String.format("domain?domain=%s", value);
        if (type == IPV6) return String.format("ip?ipv6=%s", value);
        else return String.format("ip?ipv4=%s", value);
    }

    /*
        Parsing
     */

    public static Intelligence parse(int type, JSONObject body) throws JSONException {
        return (parseData(type, type == DOMAIN ? body.getJSONObject("result") : body.getJSONArray("result").getJSONObject(0)));
    }

    public static Intelligence parseData(int type, JSONObject data) throws JSONException {
        return type == DOMAIN ? parseDomain(data) : parseIP(type, data);
    }

    private static Intelligence parseDomain(JSONObject data) throws JSONException {
        Intelligence intel = new Intelligence(DOMAIN);

        intel.asked = data.getString("domain");
        intel.risks = data.has("risk_types") ? Category.parse(data.getJSONArray("risk_types")) : new ArrayList<>();
        intel.rank = data.has("popularity_rank") ? String.valueOf(data.getInt("popularity_rank")) : "?";
        intel.riskScore = data.has("risk_score") ? String.valueOf(data.getInt("risk_score")) : "?";
        intel.categories = data.has("content_categories") ? Category.parse(data.getJSONArray("content_categories")) : new ArrayList<>();
        intel.application = data.getJSONObject("application").length() == 0 ? "?" : data.getJSONObject("application").getString("name");
        intel.refs = data.has("resolves_to_refs") ? parseRefs(data.getJSONArray("resolves_to_refs")) : new ArrayList<>();

        return intel;
    }

    private static Intelligence parseIP(int type, JSONObject data) throws JSONException {
        Intelligence intel = new Intelligence(type);

        intel.asked = data.getString("ip");
        intel.reservations = data.has("iana_reservations") ? Parser.parseStringList(data.getJSONArray("iana_reservations")) : new ArrayList<>();
        intel.ptrDomains = data.has("ptr_lookup") ? Parser.parseStringList(data.getJSONObject("ptr_lookup").getJSONArray("ptr_domains")) : new ArrayList<>();
        intel.risks = data.has("risk_types") ? Category.parse(data.getJSONArray("risk_types")) : new ArrayList<>();
        intel.ipref = IPRef.parse(data.getJSONObject("belongs_to_ref"));

        return intel;
    }

    private static ArrayList<String> parseRefs(JSONArray list) throws JSONException {
        ArrayList<String> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(list.getJSONObject(i).getString("value"));
        }
        return done;
    }

    /*
        Category
     */

    static public class Category {

        public int id;
        public int category;
        public String name;

        public static ArrayList<Category> parse(JSONArray list) throws JSONException {
            ArrayList<Category> done = new ArrayList<>();
            for (int i = 0; i < list.length(); i++) {
                done.add(parse(list.getJSONObject(i)));
            }
            return done;
        }

        public static Category parse(JSONObject data) throws JSONException {
            Category r = new Category();
            r.id = data.getInt("id");
            r.category = data.getInt("super_category_id");
            r.name = data.getString("name");
            return r;
        }
    }

    /*
        IPRef
     */

    static public class IPRef {

        public String type;
        public String country;
        public String description;

        public static IPRef parse(JSONObject data) throws JSONException {
            IPRef ref = new IPRef();
            ref.type = data.getString("type");
            ref.country = data.getString("country");
            ref.description = data.getString("description");
            return ref;
        }

    }

}

// Domain

        /*"additional_information": {
            "suspected_malware_family": ""
        }*/