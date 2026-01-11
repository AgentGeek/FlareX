package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.App;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<App> apps;
    private AppListener listener = null;

    public interface AppListener {
        void onAppSelected(App app);
    }

    public AppAdapter(Context context, ArrayList<App> apps) {
        this.inflater = LayoutInflater.from(context);
        this.apps = apps;
    }

    public void setListener(AppListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cube_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapter.ViewHolder holder, int position) {
        holder.bind(apps.get(position));
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView label;
        final ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.app_label);
            icon = itemView.findViewById(R.id.app_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onAppSelected(apps.get(getAdapterPosition()));
        }

        public void bind(App app) {
            label.setText(app.label);
            icon.setImageResource(app.icon);
        }
    }
}
