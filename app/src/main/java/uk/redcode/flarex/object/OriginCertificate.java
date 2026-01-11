package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OriginCertificate {

    private String certificateId;
    private String expirationDate;
    public ArrayList<String> hosts;
    private int requestValidity;
    public String requestType = "";
    private String certificate;
    private String csr = "";

    public static ArrayList<OriginCertificate> parse(JSONArray list) throws JSONException {
        ArrayList<OriginCertificate> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static OriginCertificate parse(JSONObject data) throws JSONException {
        OriginCertificate c = new OriginCertificate();

        c.certificateId = data.getString("id");
        c.expirationDate = data.getString("expires_on");
        if (data.has("request_type")) c.requestType = data.getString("request_type");
        if (data.has("requested_validity")) c.requestValidity = data.getInt("requested_validity");

        c.certificate = data.getString("certificate");
        if (data.has("csr")) c.csr = data.getString("csr");

        c.hosts = Parser.parseStringList(data.getJSONArray("hostnames"));
        return c;
    }

}
