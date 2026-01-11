package uk.redcode.flarex.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Topic;
import uk.redcode.flarex.work.ImageManager;

public class TopicAdapter  extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private final Activity context;
    private final LayoutInflater inflater;
    private final ArrayList<Topic> topics;
    private TopicListener listener = null;

    public interface TopicListener {
        void onTopicSelected(Topic topic);
    }

    public TopicAdapter(Activity context, ArrayList<Topic> topics) {
        this.inflater = LayoutInflater.from(context);
        this.topics = topics;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_topic, parent, false);
        return new TopicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {
        holder.bind(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setListener(TopicListener listener) { this.listener = listener; }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final TextView preview;
        final ImageView pinned;
        final ImageView solved;
        final ImageView locked;
        final TextView views;
        final TextView likes;
        final TextView replies;
        final ChipGroup chipContainer;
        final LinearLayout avatarContainer;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.topic_title);
            preview = itemView.findViewById(R.id.topic_preview);
            pinned = itemView.findViewById(R.id.topic_pinned);
            solved = itemView.findViewById(R.id.topic_solved);
            locked = itemView.findViewById(R.id.topic_locked);
            views = itemView.findViewById(R.id.topic_view);
            likes = itemView.findViewById(R.id.topic_like);
            replies = itemView.findViewById(R.id.topic_reply);
            chipContainer = itemView.findViewById(R.id.topic_chip_container);
            avatarContainer = itemView.findViewById(R.id.topic_avatar_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onTopicSelected(topics.get(getAdapterPosition()));
        }

        public void bind(Topic topic) {
            title.setText(topic.title);
            preview.setText(topic.preview);
            preview.setVisibility(topic.preview.isEmpty() ? View.GONE : View.VISIBLE);
            pinned.setVisibility(topic.pinned ? View.VISIBLE : View.GONE);
            solved.setVisibility(topic.solved ? View.VISIBLE : View.GONE);
            locked.setVisibility(topic.closed ? View.VISIBLE : View.GONE);
            views.setText(topic.getViewLabel());
            likes.setText(String.valueOf(topic.like));
            replies.setText(String.valueOf(topic.replies));

            chipContainer.removeAllViews();

            // avatar
            if (avatarContainer.getChildCount() <= 0) {
                avatarContainer.removeAllViews();
                for (String link : topic.avatars) {
                    View view = inflater.inflate(R.layout.avatar, avatarContainer, false);
                    avatarContainer.addView(view);
                    ImageManager.with(context).load(link).into(view.findViewById(R.id.avatar)).run();
                }
            }

            // category
            Chip cat = new Chip(context);
            cat.setText(topic.category.name);
            if (!topic.category.color.isEmpty()) cat.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(topic.category.color)));
            chipContainer.addView(cat);
            for (String tag : topic.tags) {
                Chip chip = new Chip(context);
                chip.setText(tag);
                chipContainer.addView(chip);
            }
        }
    }
}
