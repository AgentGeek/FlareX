package uk.redcode.flarex.object;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;

public class Certificate {

    private String certificateId;
    public String type;
    public String status;
    private String validationMethod;
    private int validityDays;
    public ArrayList<String> hosts;

    public static ArrayList<Certificate> parse(JSONArray list) throws JSONException {
        ArrayList<Certificate> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static Certificate parse(JSONObject data) throws JSONException {
        Certificate c = new Certificate();

        c.certificateId = data.getString("id");
        c.type = data.getString("type");
        c.status = data.getString("status");
        c.validationMethod = data.getString("validation_method");

        c.validityDays = data.getInt("validity_days");

        c.hosts = Parser.parseStringList(data.getJSONArray("hosts"));
        return c;
    }

    public int getStatusColor(Context context) {
        if (status.equals("active")) return context.getColor(R.color.statusActive);
        return context.getColor(R.color.error);
    }
}