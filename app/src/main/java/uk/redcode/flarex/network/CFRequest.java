package uk.redcode.flarex.network;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.User;
import uk.redcode.flarex.ui.Alert;

public class CFRequest extends JsonObjectRequest {

    private final String TAG = "CFRequest-"+Math.round((Math.random() * (9999 - 1111)) + 1111);
    private final Listener listener;
    public String apikey = "";
    public String email = "";
    public int mode = 0;
    public final HashMap<String, String> addedHeader = new HashMap<>();

    public interface Listener {
        void onResult(JSONObject body) throws JSONException;
        void onError(Exception e);
    }
    public CFRequest(Context context, int method, String url, @Nullable JSONObject data, Listener listener) {
        super(method, url, data, response -> {
            try {
                listener.onResult(response);
            } catch (JSONException e) {
                Logger.error(e);
                listener.onError(e);
            }
        }, (error) -> {
            Logger.network("CFRequest", "Response Error: " + (error.networkResponse != null ? error.networkResponse.statusCode : "No Response"));
            Logger.error(error);
            listener.onError(error);
            if (context instanceof MainActivity) ((MainActivity) context).showAlert(new Alert(Alert.ERROR, error.getMessage()));
        });
        
        load(context);
        Logger.network(TAG, url);
        this.listener = listener;
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        NetworkResponse response = volleyError.networkResponse;

        if (volleyError.networkResponse == null) {
            return volleyError;
        }

        switch (response.statusCode) {
            case 400:
                return new VolleyError("400: Bad Request");
            case 401:
                return new VolleyError("401: Unauthorized - Check your API Token");
            case 403:
                return new VolleyError(this.mode == CFApi.TYPE_TOKEN ? "403: Forbidden, Missing token permission" : "403: Forbidden");
            case 429:
                return new VolleyError("429: Too many request, please wait a few");
            case 405:
                return new VolleyError("405: Method not allowed, this is a core application error");
            case 415:
                return new VolleyError("415: Invalid JSON, this is a core application error");
            default:
                return volleyError;
        }
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject jsonObject = new JSONObject(json);
            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Logger.error(e);
            return Response.error(new VolleyError(e.getMessage()));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        try {
            listener.onResult(response);
        } catch (JSONException e) {
            Logger.error(e);
            listener.onError(e);
        }
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "FlareX/O.1.1-beta1");
        headers.put("Content-Type", "application/json");
        headers.putAll(addedHeader);

        String trimmedKey = apikey.trim();

        if (mode == CFApi.TYPE_MASTER_KEY) {
            if (!trimmedKey.isEmpty()) headers.put("X-Auth-Key", trimmedKey);
            if (!email.isEmpty()) headers.put("X-Auth-Email", email.trim());
        } else {
            if (!trimmedKey.isEmpty()) {
                headers.put("Authorization", "Bearer " + trimmedKey);
            }
        }

        return headers;
    }

    public void load(Context context) {
        email = User.getEmail(context);
        apikey = User.getKey(context);
        mode = User.getMode(context);
    }
}