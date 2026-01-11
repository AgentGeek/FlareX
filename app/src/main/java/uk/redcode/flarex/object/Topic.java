package uk.redcode.flarex.object;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFCommunity;
import uk.redcode.flarex.work.ImageManager;

public class Topic {

    public int id;
    public String title;
    public String slug;
    public boolean pinned;
    public boolean closed;
    public boolean solved;
    public boolean archived;
    public int views = -1;
    public int like;
    public int replies;
    public ArrayList<String> tags;
    public ArrayList<Post> posts = new ArrayList<>();
    public String preview;
    public Category.SubCategory category;
    public String url;
    public ArrayList<Integer> streams;
    public ArrayList<String> avatars = new ArrayList<>();
    public int solution = -1;

    public static ArrayList<Topic> parse(JSONArray list, JSONArray users, Context context) throws JSONException {
        ArrayList<Topic> done = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            done.add(parse(list.getJSONObject(i), users, context));
        }
        return done;
    }

    @SuppressLint("DefaultLocale")
    public static Topic parse(JSONObject data, JSONArray users, Context context) throws JSONException {
        Topic t = new Topic();

        t.id = data.getInt("id");
        t.title = data.getString("title");
        t.slug = data.getString("slug");
        t.pinned = data.getBoolean("pinned");
        t.closed = data.getBoolean("closed");
        t.solved = data.getBoolean("has_accepted_answer");
        t.archived = data.getBoolean("archived");
        if (data.has("views")) t.views = data.getInt("views");
        if (data.has("like_count")) t.like = data.getInt("like_count");
        t.replies = data.getInt("posts_count");
        t.tags = Parser.parseStringList(data.getJSONArray("tags"));
        t.preview = data.has("excerpt") ? data.getString("excerpt").replace("&hellip;", "...") : "";
        t.url = String.format("%s/t/%s", CFCommunity.BASE_URL, t.slug);

        if (data.has("posters")) t.avatars = Parser.parseUserAvatar(data.getJSONArray("posters"), users);

        int catId = data.getInt("category_id");
        String catName = AppParameter.getString(context, String.format("subcategory_%d", catId), "");
        String catColor = AppParameter.getString(context, String.format("subcategory_%d_color", catId), "");
        t.category = new Category.SubCategory(catId, catName, catColor);

        return t;
    }

    @SuppressLint("DefaultLocale")
    public String getViewLabel() {
        if (views < 1000) return String.valueOf(views);
        return String.format("%d.1k", views/1000);
    }

    /*
        POST
     */

    public static class Post {

        public int id;
        public String username;
        public String avatar;
        public String status = "";
        public String cooked;
        public ArrayList<Content> content;

        public static ArrayList<Post> parse(JSONArray list) throws JSONException {
            ArrayList<Post> done = new ArrayList<>();
            for (int i = 0; i < list.length(); i++) {
                done.add(parse(list.getJSONObject(i)));
            }
            return done;
        }

        public static Post parse(JSONObject data) throws JSONException {
            Post p = new Post();

            p.id = data.getInt("id");
            p.username = data.getString("username");
            p.avatar = data.getString("avatar_template");
            p.avatar = p.avatar.replace("{size}", "45");
            if (p.avatar.startsWith("/")) p.avatar = CFCommunity.BASE_URL + p.avatar;
            p.content = Content.parse(data.getString("cooked"));
            p.cooked = data.getString("cooked");
            if (data.has("action_code")) p.status = data.getString("action_code");
            if (!p.status.isEmpty()) Log.d("HERE", "parse: STATUS: "+p.status);


            return p;
        }
    }

    /*
        POST CONTENT
     */

    public static class Content {

        public static final int TEXT = 0;
        public static final int IMG = 1;
        public static final int CODE = 2;
        public static final int QUOTE = 3;
        public static final int ORDERED_LIST = 4;
        public static final int UNSORTED_LIST = 5;

        public final int type;
        public String content;
        public Quote quote;
        public Elements list;

        public Content(int type, String content) {
            this.type = type;
            this.content = content;
        }

        public Content(int type, Quote quote) {
            this.type = type;
            this.quote = quote;
        }

        public Content(int type, Elements list) {
            this.type = type;
            this.list = list;
        }

        @SuppressLint("DefaultLocale")
        public View build(Activity context, LayoutInflater inflater, LinearLayout container) {
            if (type == TEXT) {
                TextView text = new TextView(context);
                text.setText(Html.fromHtml(content));
                text.setMovementMethod(LinkMovementMethod.getInstance());
                return text;
            }

            if (type == IMG) {
                ImageView img = new ImageView(context);
                img.setAdjustViewBounds(true);
                ImageManager.with(context).load(content).into(img).run();
                return img;
            }

            if (type == CODE) {
                TextView code = new TextView(context);
                code.setPadding(24,24,24,24);
                code.setText(content);
                Typeface typeCode = ResourcesCompat.getFont(context, R.font.hack_regular);
                code.setTypeface(typeCode);
                return code;
            }

            if (type == QUOTE) {
                View viewQuote = inflater.inflate(R.layout.row_topic_quote, container, false);
                ((TextView) viewQuote.findViewById(R.id.quote_username)).setText(Html.fromHtml(quote.username));
                ((TextView) viewQuote.findViewById(R.id.quote_username)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) viewQuote.findViewById(R.id.quote_content)).setText(Html.fromHtml(quote.content));
                ((TextView) viewQuote.findViewById(R.id.quote_content)).setMovementMethod(LinkMovementMethod.getInstance());
                ImageManager.with(context).load(quote.avatar).into(viewQuote.findViewById(R.id.quote_avatar)).run();
                //new DownloadImageWork((ImageView) viewQuote.findViewById(R.id.quote_avatar)).execute(quote.avatar);
                return viewQuote;
            }

            if (type == ORDERED_LIST || type == UNSORTED_LIST) {
                LinearLayout viewList = new LinearLayout(context);
                viewList.setOrientation(LinearLayout.VERTICAL);
                int pos = 1;
                for (Element elem : list) {
                    Log.d("HERE", "build: "+String.format("%d / %d", type, pos));
                    View rowList = inflater.inflate(type == ORDERED_LIST ? R.layout.row_ordered_item : R.layout.row_unsorted_item, viewList, false);
                    String label = type == ORDERED_LIST ? String.format("%d. %s", pos, elem.text()) : elem.text();
                    ((TextView) rowList.findViewById(R.id.item_content)).setText(label);
                    viewList.addView(rowList);
                    pos++;
                }
                return viewList;
            }

            return new View(context);
        }

        public static ArrayList<Content> parse(String cooked) {
            ArrayList<Content> done = new ArrayList<>();
            Document doc = Jsoup.parse(cooked);
            for (Element elem : doc.body().children()) {
                Log.d("HERE", "parse: "+elem.html());

                // check img
                if (elem.select("div.lightbox-wrapper").size() > 0) {
                    String img = elem.select("a.lightbox").first().attr("href");
                    done.add(new Content(IMG, img));
                    continue;
                }

                // code
                if (elem.tagName().equals("pre") && elem.select("code").size() > 0) {
                    done.add(new Content(CODE, elem.text()));
                    continue;
                }

                // quote
                if (elem.tagName().equals("aside") && elem.hasClass("quote")) {
                    Quote quote = new Quote();
                    quote.username = elem.select("div.title").first().text().trim();
                    quote.avatar = elem.select("div.title img").first().attr("src");
                    quote.content = elem.select("blockquote p").html();

                    done.add(new Content(QUOTE, quote));
                    continue;
                }

                // ordered list
                if (elem.tagName().equals("ol")) {
                    done.add(new Content(ORDERED_LIST, elem.children()));
                    continue;
                }

                // unsorted list
                if (elem.tagName().equals("ul")) {
                    Log.d("HERE", "parse: UNSORTED");
                    done.add(new Content(UNSORTED_LIST, elem.children()));
                    continue;
                }

                // replace emoji
                

                done.add(new Content(TEXT, elem.html()));
            }
            return done;
        }
    }

    /*
        QUOTE HANDLER
     */

    public static class Quote {

        public String username;
        public String avatar;
        public String content;

        public Quote() {}

    }

}
