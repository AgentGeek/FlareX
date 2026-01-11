package uk.redcode.flarex.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Topic;
import uk.redcode.flarex.work.ImageManager;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Topic topic;
    private final Activity context;
    private final LayoutInflater inflater;
    private final ArrayList<Topic.Post> posts;

    private static final int POST = 0;
    private static final int PINNED = 1;
    private static final int CLOSED = 2;
    private static final int SPLIT = 3;


    public PostAdapter(Activity context, Topic topic) {
        this.inflater = LayoutInflater.from(context);
        this.topic = topic;
        this.posts = topic.posts;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == PINNED) return new PinnedHolder(inflater.inflate(R.layout.row_community_post_event, parent, false));
        else if (viewType == SPLIT) return new SplitHolder(inflater.inflate(R.layout.row_community_post_event, parent, false));
        else if (viewType == CLOSED) return new ClosedHolder(inflater.inflate(R.layout.row_community_post_event, parent, false));

        return new ViewHolder(inflater.inflate(R.layout.row_community_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == PINNED) ((PinnedHolder) holder).bind(posts.get(position));
        else if (holder.getItemViewType() == SPLIT) ((SplitHolder) holder).bind(posts.get(position));
        else if (holder.getItemViewType() == CLOSED) ((ClosedHolder) holder).bind(posts.get(position));
        else if (holder.getItemViewType() == POST) ((ViewHolder) holder).bind(posts.get(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).status.equals("pinned_globally.enabled")) return PINNED;
        if (posts.get(position).status.equals("split_topic")) return SPLIT;
        if (posts.get(position).status.equals("autoclosed.enabled")) return CLOSED;
        return POST;
    }

    @Override
    public int getItemCount() { return posts.size(); }

    /*
        General Post
     */

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;
        private final TextView username;
        private final LinearLayout container;
        private final LinearLayout solutionLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.author_avatar);
            this.username = itemView.findViewById(R.id.author_name);
            this.container = itemView.findViewById(R.id.post_content);
            this.solutionLayout = itemView.findViewById(R.id.post_solution_layout);
        }

        public void bind(Topic.Post post, int position) {
            username.setText(post.username);
            solutionLayout.setVisibility(position == topic.solution-1 && topic.solution != -1 ? View.VISIBLE : View.GONE);
            ImageManager.with(context).load(post.avatar).into(avatar).run();
            buildContent(post);
        }

        private void buildContent(Topic.Post post) {
            container.removeAllViews();
            for (Topic.Content content : post.content) {
                container.addView(content.build(context, inflater, container));
            }
        }
    }

    /*
        Pinned Post
     */

    public class PinnedHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;

        public PinnedHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.author_avatar);
            ((TextView) itemView.findViewById(R.id.label)).setText(R.string.post_pinned);
            ((ImageView) itemView.findViewById(R.id.post_label_icon)).setImageResource(R.drawable.ic_pinned);
        }

        public void bind(Topic.Post post) {
            ImageManager.with(context).load(post.avatar).into(avatar).run();
        }

    }

    /*
        Split Post
     */

    public class SplitHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;
        private final TextView label;

        public SplitHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.author_avatar);
            this.label = itemView.findViewById(R.id.label);
            ((ImageView) itemView.findViewById(R.id.post_label_icon)).setImageResource(R.drawable.ic_split);
        }

        public void bind(Topic.Post post) {
            ImageManager.with(context).load(post.avatar).into(avatar).run();
            label.setText(Html.fromHtml(post.cooked));
            label.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }

    /*
        Closed Post
     */

    public class ClosedHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;

        public ClosedHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.author_avatar);
            ((TextView) itemView.findViewById(R.id.label)).setText(R.string.post_closed);
            ((ImageView) itemView.findViewById(R.id.post_label_icon)).setImageResource(R.drawable.ic_lock);
        }

        public void bind(Topic.Post post) {
            ImageManager.with(context).load(post.avatar).into(avatar).run();
        }

    }

}
