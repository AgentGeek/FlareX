package uk.redcode.flarex.object;

import org.json.JSONException;
import org.json.JSONObject;

public class Mode {

    public String id;
    public boolean value;
    public boolean editable;
    public int timeRemaining;

    public static Mode parse(JSONObject data) throws JSONException {
        Mode mode = new Mode();

        mode.id = data.getString("id");
        mode.value = !data.getString("value").equals("off");
        mode.editable = data.getBoolean("editable");
        mode.timeRemaining = data.has("time_remaining") ? data.getInt("time_remaining") : 0;

        return mode;
    }
}