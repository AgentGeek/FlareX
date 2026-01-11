package uk.redcode.flarex.object;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Whois {

    public String domain;
    public String creationDate;
    public String updateDate;
    public String registrant;
    public String country;
    public String email;
    public String registrar;
    public String organisation;
    public ArrayList<String> nameservers;
    public boolean notFound = false;

    public static Whois parse(JSONObject data) throws JSONException {
        Whois w = new Whois();
        if (data.length() == 0) {
            w.notFound = true;
            return w;
        }

        w.domain = data.getString("domain");
        w.creationDate = data.getString("created_date");
        w.updateDate = data.getString("updated_date");
        w.registrant = data.getString("registrant");
        w.registrar = data.getString("registrar");
        w.country = data.has("registrant_country") ? data.getString("registrant_country") : "?";
        w.email = data.has("registrant_email") ? data.getString("registrant_email") : "?";
        w.organisation = data.has("registrant_org") ? data.getString("registrant_org") : "?";
        w.nameservers = Parser.parseStringList(data.getJSONArray("nameservers"));
        return w;
    }

}
