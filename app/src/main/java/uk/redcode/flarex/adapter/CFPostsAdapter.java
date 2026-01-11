package uk.redcode.flarex.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CFPost;
import uk.redcode.flarex.object.Parser;

public class CFPostsAdapter extends RecyclerView.Adapter<CFPostsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<CFPost> posts;
    private PostListener listener = null;
    private final Activity context;

    public interface PostListener {
        void onPostClicked(CFPost post);
    }

    public CFPostsAdapter(Activity context, ArrayList<CFPost> posts) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    public void setListener(PostListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public CFPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_post, parent, false);
        return new CFPostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CFPostsAdapter.ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final TextView preview;
        final TextView date;
        final ChipGroup tags;
        final RecyclerView recycler;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
            date = itemView.findViewById(R.id.post_date);
            preview = itemView.findViewById(R.id.post_preview);
            tags = itemView.findViewById(R.id.post_tags);
            recycler = itemView.findViewById(R.id.post_authors);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onPostClicked(posts.get(getAdapterPosition()));
        }

        @SuppressLint("SimpleDateFormat")
        public void bind(CFPost post) {
           title.setText(post.title);
           date.setText(new SimpleDateFormat("dd/MM/yyyy").format(Parser.parseDate(post.date).getTime()));
           preview.setText(post.preview);

           tags.removeAllViews();
           for (String tag : post.tags) {
               Chip chip = new Chip(context);

               chip.setClickable(false);
               chip.setCheckable(false);
               chip.setText(tag);
               tags.addView(chip);
           }

            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            CFAuthorsAdapter adapter = new CFAuthorsAdapter(context, post.writers);
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);
        }
    }

}
