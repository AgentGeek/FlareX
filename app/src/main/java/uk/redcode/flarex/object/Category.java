package uk.redcode.flarex.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import uk.redcode.flarex.R;

public class Category {

    private static final String TAG = "CFCategory";
    public int id;
    public String name;
    public String color;
    public String description;
    public int position;
    private ArrayList<Integer> subCategoriesId;
    public ArrayList<SubCategory> subCategories;

    public static ArrayList<Category> parse(JSONArray list, Context context) throws JSONException {
        ArrayList<Category> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i), context));
        }
        return done;
    }

    @SuppressLint("DefaultLocale")
    public static Category parse(JSONObject data, Context context) throws JSONException {
        Category c = new Category();

        c.id = data.getInt("id");
        c.name = data.getString("name");
        c.color = data.getString("color");
        if (!c.color.startsWith("#")) c.color = "#" + c.color;
        c.position = data.getInt("position");
        c.description = data.getString("description");
        c.subCategoriesId = Parser.parseIntList(data.getJSONArray("subcategory_ids"));
        c.subCategories = updateSubCategories(c.subCategoriesId, context, c.color);

        // save the category
        AppParameter.setString(context, String.format("subcategory_%d", c.id), c.name);
        AppParameter.setString(context, String.format("subcategory_%d_color", c.id), c.color);

        return c;
    }

    @SuppressLint("DefaultLocale")
    private static ArrayList<SubCategory> updateSubCategories(ArrayList<Integer> ids, Context context, String color) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ArrayList<SubCategory> done = new ArrayList<>();
        boolean toastFirstRun = false;

        for (int id : ids) {
            String loaded = AppParameter.getString(context, String.format("subcategory_%d", id), "");
            if (!loaded.isEmpty()) {
                String loadedColor = AppParameter.getString(context, String.format("subcategory_%d_color", id), "");
                done.add(new SubCategory(id, loaded, loadedColor));
                continue;
            }

            try {
                if (!toastFirstRun) {
                    Logger.info(TAG, "First run");
                    Toast.makeText(context, R.string.community_first_run, Toast.LENGTH_SHORT).show();
                    toastFirstRun = true;
                }

                URL url = new URL(String.format("https://community.cloudflare.com/c/%d", id));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                URL secondURL = new URL(connection.getHeaderField("location"));
                Logger.info(TAG, "Category: "+secondURL);
                String[] paths = secondURL.toString().split("/");
                Logger.info(TAG, "UpdateSubCategory: "+paths[paths.length-2]);

                AppParameter.setString(context, String.format("subcategory_%d", id), paths[paths.length-2].replace("-", " "));
                AppParameter.setString(context, String.format("subcategory_%d_color", id), color);
                done.add(new SubCategory(id, paths[paths.length-2], color));
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        return done;
    }

    public static class SubCategory {

        public final int id;
        public final String name;
        public final String color;

        public SubCategory(int id, String name, String color) {
            this.id = id;
            this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
            this.color = color;
        }
    }

}