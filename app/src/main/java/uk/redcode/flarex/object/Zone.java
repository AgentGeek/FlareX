package uk.redcode.flarex.object;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.R;

public class Zone {

    private static final String TAG = "Zone";

    public String zoneId;
    public String name;
    public String plan;
    public String planId;
    public String status;
    public String raw;

    boolean paused;

    public ArrayList<String> nameServers = new ArrayList<>();
    public ArrayList<String> nameServersOrigin = new ArrayList<>();

    public static final String PLAN_FREE = "free";
    public static final String PLAN_PRO = "pro";
    public static final String PLAN_BUSINESS = "business";
    public static final String PLAN_ENTERPRISE = "enterprise";
    public static final ArrayList<String> PLAN_ORDER = new ArrayList<String>(){{
        add(PLAN_FREE);
        add(PLAN_PRO);
        add(PLAN_BUSINESS);
        add(PLAN_ENTERPRISE);
    }};

    public static ArrayList<Zone> parse(JSONArray list) throws JSONException {
        ArrayList<Zone> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i)));
        }
        return done;
    }

    public static Zone parse(JSONObject data) throws JSONException {
        Zone z = new Zone();

        z.zoneId = data.getString("id");
        z.name = data.getString("name");
        z.plan = data.getJSONObject("plan").getString("name");
        z.planId = data.getJSONObject("plan").getString("legacy_id");
        z.status = data.getString("status");
        z.paused = data.getBoolean("paused");
        z.nameServers = Parser.parseStringList(data.getJSONArray("name_servers"));
        if (!data.isNull("original_name_servers")) z.nameServersOrigin = Parser.parseStringList(data.getJSONArray("original_name_servers"));
        z.raw = data.toString();

        return z;
    }

    public int getStatusIcon() {
        if (paused) return R.drawable.ic_status_pause;
        else if (status.equals("active")) return R.drawable.ic_status_ok;
        else if (status.equals("pending")) return R.drawable.ic_status_pending;
        else if (status.equals("moved")) return R.drawable.ic_status_moved;

        Log.d("HERE", "getStatusIcon: "+status);

        return R.drawable.ic_status_bug;
    }

    public boolean hasRequiredPlan(String askedPlan, Context context) {
        int actual = PLAN_ORDER.indexOf(planId);
        int asked = PLAN_ORDER.indexOf(askedPlan);

        if (actual < asked) Toast.makeText(context, getRequiredPlanString(askedPlan), Toast.LENGTH_SHORT).show();
        return actual >= asked;
    }

    private int getRequiredPlanString(String asked) {
        if (asked.equals(PLAN_PRO)) return R.string.plan_pro_required;
        if (asked.equals(PLAN_BUSINESS)) return R.string.plan_business_required;
        if (asked.equals(PLAN_ENTERPRISE)) return R.string.plan_enterprise_required;

        Logger.warning(TAG, "Required plan ?? -> "+asked);
        return R.string.internal_error;
    }

    public void print() {
        Log.d("HERE", "print: id: "+zoneId);
        Log.d("HERE", "print: name: "+name);
        Log.d("HERE", "print: plan: "+plan);
    }
}