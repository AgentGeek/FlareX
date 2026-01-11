package uk.redcode.flarex.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CFPost;
import uk.redcode.flarex.work.ImageManager;

public class CFAuthorsAdapter extends RecyclerView.Adapter<CFAuthorsAdapter.ViewHolder> {

    private final Activity context;
    private final LayoutInflater inflater;
    private final ArrayList<CFPost.Author> authors;

    public CFAuthorsAdapter(Activity context, ArrayList<CFPost.Author> authors) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.authors = authors;
    }

    @NonNull
    @Override
    public CFAuthorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_post_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CFAuthorsAdapter.ViewHolder holder, int position) {
        holder.bind(authors.get(position), context);
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView img;
        final TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.author_avatar);
            name = itemView.findViewById(R.id.author_name);
        }


        public void bind(CFPost.Author author, Activity context) {
            name.setText(author.name);
            ImageManager.with(context).load(author.avatar).into(img).run();
            //new DownloadImageWork(img).execute(author.avatar);
        }
    }
}