package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.tester.Tester;

public class TokenTestAdapter extends RecyclerView.Adapter<TokenTestAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Tester> testers;

    public TokenTestAdapter(Context context, ArrayList<Tester> testers) {
        this.inflater = LayoutInflater.from(context);
        this.testers = testers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_tester, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TokenTestAdapter.ViewHolder holder, int position) {
        holder.bind(testers.get(position));
    }

    @Override
    public int getItemCount() {
        return testers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final ProgressBar progress;
        public final TextView title;
        public final TextView result;
        public final ImageView resultIcon;

        ViewHolder(View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.tester_progress);
            title = itemView.findViewById(R.id.tester_title);
            result = itemView.findViewById(R.id.tester_result);
            resultIcon = itemView.findViewById(R.id.tester_icon);
        }

        public void bind(Tester tester) {
            tester.bind(this);
        }
    }
}
