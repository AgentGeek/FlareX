package uk.redcode.flarex.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CFPost;
import uk.redcode.flarex.work.ImageManager;

public class CFPostContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<CFPost.Content> contents;
    private final LayoutInflater inflater;
    private final Activity context;
    private final CFPost post;

    private final static int TYPE_AUTHORS = 9998;
    private final static int TYPE_TAGS = 9999;

    public CFPostContentAdapter(Activity context, CFPost post) {
        this.inflater = LayoutInflater.from(context);
        this.contents = post.contents;
        this.context = context;
        this.post = post;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_AUTHORS:
                return new ViewAuthors(inflater.inflate(R.layout.post_authors, parent, false));
            case TYPE_TAGS:
                return new ViewTags(inflater.inflate(R.layout.post_tags, parent, false));
            case CFPost.Content.TYPE_TEXT:
                return new ViewText(inflater.inflate(R.layout.post_text, parent, false));
            case CFPost.Content.TYPE_TITLE:
                return new ViewTitle(inflater.inflate(R.layout.post_title, parent, false));
            case CFPost.Content.TYPE_IMG:
                return new ViewImg(inflater.inflate(R.layout.post_img, parent, false));
            case CFPost.Content.TYPE_QUOTE:
                return new ViewQuote(inflater.inflate(R.layout.post_quote, parent, false));
            case CFPost.Content.TYPE_CODE:
                return new ViewCode(inflater.inflate(R.layout.post_code, parent, false));
            case CFPost.Content.TYPE_BTN:
                return new ViewButton(inflater.inflate(R.layout.post_button, parent, false));
            case CFPost.Content.TYPE_LIST:
                return new ViewList(inflater.inflate(R.layout.post_text, parent, false));
            case CFPost.Content.TYPE_VIDEO:
                return new ViewVideo(inflater.inflate(R.layout.post_video, parent, false));
            case CFPost.Content.TYPE_TABLE:
                return new ViewTable(inflater.inflate(R.layout.post_table, parent, false));
            default:
                return new ViewText(inflater.inflate(R.layout.post_error, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((ViewAuthors) holder).bind(post);
            return;
        } else if (position == contents.size()+1) {
            ((ViewTags) holder).bind(post);
            return;
        }

        CFPost.Content content = contents.get(position-1);
        switch (holder.getItemViewType()) {
            case TYPE_TAGS:

            case CFPost.Content.TYPE_TEXT:
                ((ViewText) holder).bind(content);
                break;
            case CFPost.Content.TYPE_TITLE:
                ((ViewTitle) holder).bind(content);
                break;
            case CFPost.Content.TYPE_IMG:
                ((ViewImg) holder).bind(content, context);
                break;
            case CFPost.Content.TYPE_QUOTE:
                ((ViewQuote) holder).bind(content);
                break;
            case CFPost.Content.TYPE_CODE:
                ((ViewCode) holder).bind(content);
                break;
            case CFPost.Content.TYPE_BTN:
                ((ViewButton) holder).bind(content);
                break;
            case CFPost.Content.TYPE_LIST:
                ((ViewList) holder).bind(content);
                break;
            case CFPost.Content.TYPE_VIDEO:
                ((ViewVideo) holder).bind(content);
                break;
            case CFPost.Content.TYPE_TABLE:
                ((ViewTable) holder).bind(content);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_AUTHORS;
        if (position == contents.size()+1) return TYPE_TAGS;
        return contents.get(position-1).type;
    }

    @Override
    public int getItemCount() {
        return contents.size()+2;
    }

    /*
        Authors
     */

    public class ViewAuthors extends RecyclerView.ViewHolder {

        final RecyclerView recycler;

        ViewAuthors(View itemView) {
            super(itemView);
            recycler = itemView.findViewById(R.id.post_authors);
        }

        public void bind(CFPost post) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            CFAuthorsAdapter adapter = new CFAuthorsAdapter(context, post.writers);
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);
        }
    }

    /*
        Tags
     */

    public class ViewTags extends RecyclerView.ViewHolder {

        final ChipGroup tags;

        ViewTags(View itemView) {
            super(itemView);
            tags = itemView.findViewById(R.id.post_tags);
        }

        public void bind(CFPost post) {
            tags.removeAllViews();
            for (String tag : post.tags) {
                Chip chip = new Chip(context);

                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setText(tag);
                tags.addView(chip);
            }
        }
    }

    /*
        Text
     */

    public static class ViewText extends RecyclerView.ViewHolder {

        final TextView text;

        ViewText(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.post_text);
        }

        public void bind(CFPost.Content content) {
            text.setText(Html.fromHtml(content.getText()));
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setTextAlignment(content.getAlignment());
        }
    }

    /*
        Title
     */

    public static class ViewTitle extends RecyclerView.ViewHolder {

        final TextView title;

        ViewTitle(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
        }

        public void bind(CFPost.Content content) {
            title.setText(Html.fromHtml(content.getTitle()));
            title.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /*
        Img
     */

    public static class ViewImg extends RecyclerView.ViewHolder {

        private final ImageView img;

        ViewImg(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.post_img);
        }

        public void bind(CFPost.Content content, Activity context) {
            ImageManager.with(context).load(content.getImg()).into(img).run();
        }
    }

    /*
        Quote
     */

    public static class ViewQuote extends RecyclerView.ViewHolder {

        final TextView quote;

        ViewQuote(View itemView) {
            super(itemView);
            quote = itemView.findViewById(R.id.post_quote);
        }

        public void bind(CFPost.Content content) {
            quote.setText(Html.fromHtml(content.getQuote()));
        }
    }

    /*
        Code
     */

    public static class ViewCode extends RecyclerView.ViewHolder {

        final TextView code;

        ViewCode(View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.post_code);
        }

        public void bind(CFPost.Content content) {
            code.setText(content.getCode());
        }
    }

    /*
        Button
     */

    public class ViewButton extends RecyclerView.ViewHolder {

        final MaterialButton btn;

        ViewButton(View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.post_btn);
        }

        public void bind(CFPost.Content content) {
            btn.setText(content.getButton());
            btn.setOnClickListener(view -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getButtonLink()));
                context.startActivity(browserIntent);
            });
        }
    }

    /*
        List
     */

    public static class ViewList extends RecyclerView.ViewHolder {

        final TextView list;

        ViewList(View itemView) {
            super(itemView);
            list = itemView.findViewById(R.id.post_text);
        }

        public void bind(CFPost.Content content) {
            list.setText(Html.fromHtml(content.getList()));
        }
    }

    /*
        Video
     */

    public static class ViewVideo extends RecyclerView.ViewHolder {

        final WebView webView;

        ViewVideo(View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.post_video);
        }

        @SuppressLint("SetJavaScriptEnabled")
        public void bind(CFPost.Content content) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadData(content.getVideo(), "text/html", null);
        }
    }

    /*
        Table
     */

    public class ViewTable extends RecyclerView.ViewHolder {

        final TableLayout table;

        ViewTable(View itemView) {
            super(itemView);
            table = itemView.findViewById(R.id.post_table);
        }

        public void bind(CFPost.Content content) {
            table.removeAllViews();
            for (String row : content.getTable().split("<tr>")) {
                row = row.replace("</tr>", "");
                TableRow tableRow = new TableRow(context);

                for (String cell : row.split("<td(?:[^>]+)>")) {
                    cell = cell.trim();
                    if (cell.isEmpty()) continue;
                    cell = cell.replace("</td>", "");
                    cell = cell.replaceAll("#000000", "#FFFFFF");
                    Log.d("HERE", "bind: CELL: "+cell);

                    TextView text = new TextView(context);
                    text.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                    text.setText(Html.fromHtml(cell));
                    tableRow.addView(text);
                }
                table.addView(tableRow);
            }
        }
    }

}
