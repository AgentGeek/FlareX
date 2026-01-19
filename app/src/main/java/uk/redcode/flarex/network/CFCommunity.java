package uk.redcode.flarex.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import uk.redcode.flarex.object.Category;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.object.Topic;

public class CFCommunity {

    public static final String BASE_URL = "https://community.cloudflare.com";
    private static final RequestQueue requestQ = initRequestQueue();

    private static String validatedCookies = "";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.7499.194 Mobile Safari/537.36";
    private static final long WAF_TIMEOUT_SECONDS = 45; 
    private static final AtomicBoolean wafClearing = new AtomicBoolean(false);

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

    @SuppressLint("SetJavaScriptEnabled")
    public static void prepareConnection(Context context, ResultListener listener) {
        if (wafClearing.getAndSet(true)) {
            Log.d("CF_WAF", "WAF clearing already in progress, waiting...");
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        final Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(() -> {
            WebView webView = new WebView(context);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUserAgentString(USER_AGENT);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);

            webView.setWebViewClient(new WebViewClient() {
                private int checkCount = 0;
                private static final int MAX_CHECKS = 90; // 45 seconds total

                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d("CF_WAF", "Initial page load finished. Starting cookie poll...");
                    startPolling();
                }

                private void startPolling() {
                    if (success.get() || checkCount >= MAX_CHECKS) return;

                    checkCount++;
                    String currentCookies = CookieManager.getInstance().getCookie(BASE_URL);
                    
                    if (currentCookies != null && currentCookies.contains("cf_clearance")) {
                        Log.d("CF_WAF", "✓ WAF challenge passed! Cookies obtained.");
                        validatedCookies = currentCookies;
                        success.set(true);
                        latch.countDown();
                    } else {
                        Log.d("CF_WAF", "Waiting for cf_clearance... (" + checkCount + "/" + MAX_CHECKS + ")");
                        mainHandler.postDelayed(this::startPolling, 500);
                    }
                }
            });

            webView.onResume();
            webView.resumeTimers();
            
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept-Language", "en-US,en;q=0.9");
            webView.loadUrl(BASE_URL, headers);

            // Background thread to monitor the latch
            new Thread(() -> {
                try {
                    boolean completed = latch.await(WAF_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (success.get()) {
                        mainHandler.post(listener::onResult);
                    } else {
                        mainHandler.post(() -> listener.onError(new Exception("WAF challenge failed or timed out")));
                    }
                } catch (InterruptedException e) {
                    mainHandler.post(() -> listener.onError(e));
                } finally {
                    wafClearing.set(false);
                    mainHandler.post(webView::destroy);
                }
            }).start();
        });
    }

    private static void addRequest(CFRequest r) {
        if (!validatedCookies.isEmpty()) {
            r.addedHeader.put("Cookie", validatedCookies);
        }
        r.addedHeader.put("User-Agent", USER_AGENT);
        r.addedHeader.put("Accept", "application/json");
        requestQ.add(r);
    }

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
                Logger.error(e);
                listener.onError(e);
            }
        });
        addRequest(r);
    }
}