package uk.redcode.flarex.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CountryStat {

    public final ArrayList<Country> countries = new ArrayList<>();
    public float total = 0;

    public static CountryStat parse(JSONArray list) throws JSONException {
        CountryStat done = new CountryStat();
        for (int i = 0; i < list.length(); i++) {
            done = parse(done, list.getJSONObject(i).getJSONObject("sum").getJSONArray("countryMap"));
        }

        return done;
    }

    public static CountryStat parse(CountryStat stat, JSONArray list) throws JSONException {
        for (int i = 0; i < list.length(); i++) {
            JSONObject actual = list.getJSONObject(i);
            boolean found = false;
            for (int j = 0; j < stat.countries.size(); j++) {
                if (stat.countries.get(j).key.equals(actual.getString("key"))) {
                    found = true;
                    stat.countries.get(j).requests += actual.getInt("requests");
                    stat.countries.get(j).threats += actual.getInt("threats");
                    stat.total += actual.getInt("requests");
                    break;
                }
            }

            if (!found) {
                Country country = new Country();
                country.key = actual.getString("key");
                country.requests = actual.getInt("requests");
                country.threats = actual.getInt("threats");
                stat.countries.add(country);
                stat.total += country.requests;
            }
        }
        return stat;
    }

    public Country getByISO(String iso) {
        for (Country c : countries) {
            if (iso.equals(c.key)) return c;
        }
        return null;
    }

    public static class Country {
        public String key;
        public int requests;
        public int threats;
    }

}
