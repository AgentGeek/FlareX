package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Notification {

    public static ArrayList<Notification> parse(JSONArray list) throws JSONException {
        ArrayList<Notification> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static Notification parse(JSONObject data) throws JSONException {
        return new Notification();
    }
}
