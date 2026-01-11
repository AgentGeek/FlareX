package uk.redcode.flarex.object;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import uk.redcode.flarex.R;

public class ContentType {

    private static final String TAG = "ContentType";

    public int bytes;
    public int requests;
    public String key;

    public static ContentType parse(JSONObject data) throws JSONException {
        ContentType t = new ContentType();

        t.bytes = data.getInt("bytes");
        t.key = data.getString("key");
        t.requests = data.getInt("requests");

        return t;
    }

    public Integer getColor(Context context) {

        switch (key) {
            case "txt": return context.getColor(R.color.content_text);
            case "js": return context.getColor(R.color.content_js);
            case "json": return context.getColor(R.color.content_json);
            case "jpeg": return context.getColor(R.color.content_jpeg);
            case "png": return context.getColor(R.color.content_png);
            case "css": return context.getColor(R.color.content_css);
            case "html": return context.getColor(R.color.content_html);
            case "pdf": return context.getColor(R.color.content_pdf);
            case "svg": return context.getColor(R.color.content_svg);
            case "ico": return context.getColor(R.color.content_ico);
            case "gif": return context.getColor(R.color.content_gif);
            case "woff": return context.getColor(R.color.content_woff);
            case "bin": return context.getColor(R.color.content_bin);
            case "unknown":
            case "empty":
            default:
                Logger.warning(TAG, "Content ??? -> "+key);
                return context.getColor(R.color.divider);
        }
    }
}

