package uk.redcode.flarex.object;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.ui.ViewManager;

public class CFPost {

    private static final String BASE_URL = "https://blog.cloudflare.com";

    public String url;
    public String title;
    public String preview;
    public String date;
    public ArrayList<String> tags = new ArrayList<>();
    public ArrayList<Author> writers = new ArrayList<>();
    public final ArrayList<Content> contents = new ArrayList<>();

    public static ArrayList<CFPost> parseMultiple(String html) {
        ArrayList<CFPost> done = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Element main = doc.select("main#main-body").first();
        Elements articles = main.select("article");
        for (Element article : articles) {
            done.add(parse(article));
        }
        return done;
    }

    public static CFPost parse(Element article) {
        CFPost post = new CFPost();

        post.title = article.select("h2").first().text();
        post.preview = article.select(".lh-copy").first().text();
        post.url = BASE_URL + article.select("a").first().attr("href");
        post.date = article.select("p[datetime]").size() == 0 ? article.select("p[data-iso-date]").first().attr("data-iso-date") :  article.select("p[datetime]").first().attr("datetime");

        // Authors
        post.writers = parseAuthors(article.select("ul").first());
        // tags
        post.tags = parseTags(article);

        return post;
    }

    public static CFPost parseFromPage(String url, String html) {
        CFPost post = new CFPost();
        Element main = Jsoup.parse(html).select("main#post article").first();

        post.url = url;
        post.title = main.select("h1").first().text();
        post.preview = "";
        post.date = main.select("p[datetime]").size() == 0 ? main.select("p[data-iso-date]").first().attr("data-iso-date") :  main.select("p[datetime]").first().attr("datetime");

        post.writers = parseAuthors(main.select("ul").first());
        post.tags = parseTags(main);

        return post;
    }

    private static ArrayList<String> parseTags(Element article) {
        ArrayList<String> array = new ArrayList<>();
        Elements tags = article.select("a.dib");

        for (Element tag : tags) {
            array.add(tag.text());
        }

        return array;
    }

    private static ArrayList<Author> parseAuthors(Element ul) {
        ArrayList<CFPost.Author> authors = new ArrayList<>();
        if (ul == null) return authors;

        Elements writers = ul.select("li");
        for (Element writer : writers) {
            CFPost.Author author = new CFPost.Author();

            Element img = writer.select("img.author-profile-image").first();

            if (img.hasAttr("src")) author.setAvatar(img.attr("src"));
            else author.setAvatar(img.attr("data-cfsrc"));

            author.name = img.attr("alt");
            authors.add(author);
        }
        return authors;
    }

    /*
        Notification
     */

    public NotificationCompat.Builder createNotification(Context context) {
        Intent postIntent = new Intent(context, MainActivity.class);
        postIntent.putExtra("view", ViewManager.VIEW_CLOUDFLARE_POST);
        postIntent.putExtra("post-url", url);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(postIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(context, NotificationManager.CHANNEL_ID_BLOG_POST)
                .setSmallIcon(R.drawable.ic_coldcloud)
                .setContentTitle(Html.fromHtml(title))
                .setContentText(Html.fromHtml(preview))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(preview)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(NotificationManager.GROUP_BLOG_POST)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    /*
        Authors
     */

    public static class Author {
        public String name;
        public String avatar;

        public void setAvatar(String url) {
            if (url.startsWith("http")) {
                avatar = url;
            } else if (url.startsWith("//www")) {
                avatar = "https:" + url;
            } else {
                avatar = BASE_URL + url;
            }
        }
    }

    /*
        Content
     */

    public static class Content {

        public static final int TYPE_TEXT = 0;
        public static final int TYPE_IMG = 1;
        public static final int TYPE_TITLE = 2;
        public static final int TYPE_QUOTE = 3;
        public static final int TYPE_CODE = 4;
        public static final int TYPE_BTN = 5;
        public static final int TYPE_LIST = 6;
        public static final int TYPE_VIDEO = 7;
        public static final int TYPE_TABLE = 8;

        public final int type;
        public int textAlign = View.TEXT_ALIGNMENT_TEXT_START;
        private String text;
        private String title;
        private String img;
        private String quote;
        private String code;
        private String btn;
        private String btnLink;
        private String list;
        private String video;
        private String table;

        public Content(int type) {
            this.type = type;
        }

        // Img

        public void setImg(String src) {
            if (type != TYPE_IMG) return;
            img = src;
        }
        public String getImg() {
            return type == TYPE_IMG ? img : "";
        }

        // Text

        public void setText(String html) {
            if (type != TYPE_TEXT) return;
            text = html;
        }
        public String getText() {
            return type == TYPE_TEXT ? text : "Error";
        }
        public void setAlignment(int alignment) {
            if (type != TYPE_TEXT) return;
            textAlign = alignment;
        }
        public int getAlignment() {
            return textAlign;
        }

        // Quote

        public void setQuote(String html) {
            if (type != TYPE_QUOTE) return;
            quote = html;
        }
        public String getQuote() {
            return type == TYPE_QUOTE ? quote : "Error";
        }

        // Title

        public void setTitle(String html) {
            if (type != TYPE_TITLE) return;
            title = html;
        }
        public String getTitle() {
            return type == TYPE_TITLE ? title : "Error";
        }

        // Code

        public void setCode(String html) {
            if (type != TYPE_CODE) return;
            code = html;
        }
        public String getCode() {
            return type == TYPE_CODE ? code : "Error";
        }

        // Button

        public void setButton(String html) {
            if (type != TYPE_BTN) return;
            btn = html;
        }
        public String getButton() {
            return type == TYPE_BTN ? btn : "Error";
        }
        public void setButtonLink(String link) {
            if (type != TYPE_BTN) return;
            btnLink = link;
        }
        public String getButtonLink() {
            return type == TYPE_BTN ? btnLink : "";
        }

        // List

        public void setList(String html) {
            if (type != TYPE_LIST) return;
            list = html;
        }
        public String getList() {
            return type == TYPE_LIST ? list : "Error";
        }

        // Video

        public void setVideo(String html) {
            if (type != TYPE_VIDEO) return;
            video = html;
        }
        public String getVideo() {
            return type == TYPE_VIDEO ? video : "Error";
        }

        // Table

        public void setTable(String html) {
            if (type != TYPE_TABLE) return;
            table = html;
        }
        public String getTable() {
            return type == TYPE_TABLE ? table : "";
        }
    }

}
