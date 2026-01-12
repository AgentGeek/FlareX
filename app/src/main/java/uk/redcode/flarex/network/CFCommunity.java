package uk.redcode.flarex.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.redcode.flarex.object.Category;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Topic;

public class CFCommunity {

    public static final String BASE_URL = "https://community.cloudflare.com";
    private static final RequestQueue requestQ = initRequestQueue();

    // Holds the session cookies cleared by the WAF
    private static String validatedCookies = "";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36";

    public interface TopicListener {
        void onResult(ArrayList<Topic> topics);
        void onError(Exception e);
    }
    public interface CategoryListener {
        void onResult(ArrayList<Category> categories);
        void onError(Exception e);
    }
    public interface ResultListener {
        void onResult();
        void onError(Exception e);
    }

    private static RequestQueue initRequestQueue() {
        RequestQueue rq = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        rq.start();
        return rq;
    }

    /**
     * WAF SOLVER: Call this once before any other methods.
     * It waits for the 5-10 second browser check to pass.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void prepareConnection(Context context, ResultListener listener) {
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(USER_AGENT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies != null && cookies.contains("cf_clearance")) {
                    validatedCookies = cookies;
                    Log.d("CF_WAF", "WAF Cleared. Cookies: " + validatedCookies);
                    listener.onResult();
                    webView.destroy(); // Clean up memory
                }
            }
        });

        webView.loadUrl(BASE_URL);
    }

    private static void addRequest(CFRequest r) {
        // Inject the WAF cookies and User-Agent before sending
        if (!validatedCookies.isEmpty()) {
            r.addedHeader.put("Cookie", validatedCookies);
        }
        r.addedHeader.put("User-Agent", USER_AGENT);
        requestQ.add(r);
    }

    /* --- Modified Requests --- */

    public static void getLatest(Context context, int page, TopicListener listener) {
        @SuppressLint("DefaultLocale") final String url = String.format("%s/latest.json?ascending=false&page=%d", BASE_URL, page);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Topic.parse(body.getJSONObject("topic_list").getJSONArray("topics"), body.getJSONArray("users"), context));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
        addRequest(r);
    }

    public static void getTop(Context context, int page, TopicListener listener) {
        @SuppressLint("DefaultLocale") final String url = String.format("%s/top.json?ascending=false&page=%d", BASE_URL, page);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Topic.parse(body.getJSONObject("topic_list").getJSONArray("topics"), body.getJSONArray("users"), context));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
        addRequest(r);
    }

    public static void getCategories(Context context, CategoryListener listener) {
        final String url = String.format("%s/categories_and_latest", BASE_URL);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Category.parse(body.getJSONObject("category_list").getJSONArray("categories"), context));
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
        addRequest(r);
    }

    @SuppressLint("DefaultLocale")
    public static void getContent(Context context, Topic topic, ResultListener listener) {
        final String url = String.format("%s/t/%d.json?track_visit=false", BASE_URL, topic.id);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                topic.streams = Parser.parseIntList(body.getJSONObject("post_stream").getJSONArray("stream"));
                topic.posts = Topic.Post.parse(body.getJSONObject("post_stream").getJSONArray("posts"));
                if (body.has("accepted_answer") && !body.isNull("accepted_answer"))
                    topic.solution = body.getJSONObject("accepted_answer").getInt("post_number");
                listener.onResult();
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
        addRequest(r);
    }

    @SuppressLint("DefaultLocale")
    public static void getNextContent(Context context, Topic topic, ResultListener listener) {
        final String url = String.format("%s/t/%d/posts.json?%s", BASE_URL, topic.id, createUrlArgument(topic));

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                topic.posts.addAll(Topic.Post.parse(body.getJSONObject("post_stream").getJSONArray("posts")));
                listener.onResult();
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
        addRequest(r);
    }

    private static String createUrlArgument(Topic topic) {
        StringBuilder arg = new StringBuilder();
        int step = topic.posts.size();
        while (step < topic.streams.size()) {
            arg.append("post_ids[]=").append(topic.streams.get(step));
            arg.append("&");
            step++;
            if (step > topic.posts.size() + 10) break;
        }
        if (arg.length() > 0) arg.delete(arg.length() - 1, arg.length());
        return arg.toString();
    }

    public static void search(Context context, String search, TopicListener listener) {
        @SuppressLint("DefaultLocale") final String url = String.format("%s/search?q=%s&page=1", BASE_URL, search);

        CFRequest r = new CFRequest(context, Request.Method.GET, url, null, new CFRequest.Listener() {
            @Override
            public void onResult(JSONObject body) throws JSONException {
                listener.onResult(Topic.parse(body.getJSONArray("topics"), body.getJSONArray("users"), context));
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
        r.addedHeader.put("Accept", "application/json");
        addRequest(r);
    }
}