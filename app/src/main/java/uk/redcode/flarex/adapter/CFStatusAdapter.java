package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CFStatus;
import uk.redcode.flarex.object.CFStatusCategory;

public class CFStatusAdapter extends RecyclerView.Adapter<CFStatusAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<CFStatusCategory> list;
    private final Context context;

    public boolean drawGreenHeader = true;
    public String incidentLabel = "";

    public CFStatusAdapter(Context context, ArrayList<CFStatusCategory> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_status_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CFStatusAdapter.ViewHolder holder, int position) {
        if (position == 0)
            holder.bindGreenHeader();
        else
            holder.bind(list.get(position-1));
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView chevron;
        private final LinearLayout container;
        private final MaterialCardView card;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            chevron = itemView.findViewById(R.id.cf_status_toggle);
            container = itemView.findViewById(R.id.cf_status_container);
            card = itemView.findViewById(R.id.cf_status_card);

            chevron.setOnClickListener(view -> toggle());
        }


        private void toggle() {
            if (container.getVisibility() == View.GONE) {
                container.setVisibility(View.VISIBLE);
                chevron.setRotation(180);
            } else {
                container.setVisibility(View.GONE);
                chevron.setRotation(360);
            }
        }

        public void bind(CFStatusCategory category) {
            name.setText(category.name);
            name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            chevron.setVisibility(View.VISIBLE);

            container.removeAllViews();
            for (CFStatus s : category.status) {
                View v = inflater.inflate(R.layout.row_status, container, false);

                ((TextView) v.findViewById(R.id.cf_status_name)).setText(s.name);
                ((TextView) v.findViewById(R.id.cf_status_state)).setText(s.state);
                ((TextView) v.findViewById(R.id.cf_status_state)).setTextColor(context.getColor(s.color));

                container.addView(v);
            }
            container.setVisibility(View.GONE);
        }

        public void bindGreenHeader() {
            chevron.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
            name.setText(drawGreenHeader ? context.getString(R.string.all_systems_operational) : incidentLabel);
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            card.setCardBackgroundColor(context.getColor(drawGreenHeader ? R.color.cf_status_green : R.color.cf_status_orange));
        }

    }
}
