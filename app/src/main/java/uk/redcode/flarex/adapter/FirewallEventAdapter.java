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

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.FirewallEvent;

public class FirewallEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<FirewallEvent> events;
    private final Context context;

    private static final int VIEW_EMPTY = 0;
    private static final int VIEW_EVENT = 1;

    public FirewallEventAdapter(Context context, ArrayList<FirewallEvent> events) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_EMPTY) {
            View emptyView = inflater.inflate(R.layout.row_firewall_event_empty, parent, false);
            return new ViewHolderEmpty(emptyView);
        }

        View view = inflater.inflate(R.layout.row_firewall_event, parent, false);
        return new FirewallEventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (events.size() == 0) return;

        ((FirewallEventAdapter.ViewHolder) holder).bind(events.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return events.size() == 0 ? VIEW_EMPTY : VIEW_EVENT;
    }

    @Override
    public int getItemCount() {
        return events.size() == 0 ? 1 : events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView action;
        TextView date;
        final TextView ip;
        final Chip country;
        final ImageView toggle;
        final LinearLayout container;

        ViewHolder(View itemView) {
            super(itemView);
            ip = itemView.findViewById(R.id.fevent_ip);
            action = itemView.findViewById(R.id.fevent_action);
            //date = itemView.findViewById(R.id.fevent_date);
            country = itemView.findViewById(R.id.fevent_country);
            toggle = itemView.findViewById(R.id.fevent_toggle);
            toggle.setOnClickListener(this);
            container = itemView.findViewById(R.id.fevent_container);
        }

        @Override
        public void onClick(View view) {
            if (container.getVisibility() == View.VISIBLE) {
                container.setVisibility(View.GONE);
                toggle.setImageResource(R.drawable.ic_add);
            } else {
                container.setVisibility(View.VISIBLE);
                toggle.setImageResource(R.drawable.ic_minus);
            }
        }

        public void bind(FirewallEvent event) {
            action.setText(event.action);
            ip.setText(event.clientIp);
            country.setText(event.clientCountry);

            // build params
            container.removeAllViews();
            container.setVisibility(View.GONE);
            ArrayList<String> params = FirewallEvent.getAvailableParams();
            for (String p : params) {
                View view = inflater.inflate(R.layout.row_firewall_params, container, false);
                String value = event.getParam(p);
                if (value.isEmpty()) value = context.getString(R.string.empty);

                ((TextView) view.findViewById(R.id.fevent_param_name)).setText(FirewallEvent.getParamTitle(p));
                ((TextView) view.findViewById(R.id.fevent_param_content)).setText(value);

                container.addView(view);
            }
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {

        public ViewHolderEmpty(@NonNull View itemView) {
            super(itemView);
        }
    }
}
