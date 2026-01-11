package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.Zone;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Zone> zones;
    private final Context context;
    private ZoneListener listener = null;

    public interface ZoneListener {
        void onZoneSelected(Zone zone);
    }

    public ZoneAdapter(Context context, ArrayList<Zone> zones) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.zones = zones;
    }

    public ZoneAdapter(Context context, ArrayList<Zone> zones, LayoutInflater inflater) {
        this.context = context;
        this.inflater = inflater;
        this.zones = zones;
    }

    public void setListener(ZoneListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_zone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneAdapter.ViewHolder holder, int position) {
        holder.bind(zones.get(position));
    }

    @Override
    public int getItemCount() {
        return zones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView name;
        final TextView plan;
        final ImageView status;
        ChipGroup nameservers;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.zone_name);
            plan = itemView.findViewById(R.id.zone_plan);
            status = itemView.findViewById(R.id.zone_status);
            //nameservers = itemView.findViewById(R.id.zone_nameserver);
            itemView.findViewById(R.id.zone_select).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onZoneSelected(zones.get(getAdapterPosition()));
        }

        public void bind(Zone zone) {
            name.setText(zone.name);
            plan.setText(zone.plan);
            status.setImageResource(zone.getStatusIcon());
            status.setOnClickListener(view -> Toast.makeText(context, zone.status, Toast.LENGTH_SHORT).show());
            //buildNameServers(zone.nameServers);
        }

        private void buildNameServers(ArrayList<String> list) {
            nameservers.removeAllViews();
            for (String server : list) {
                Chip chip = new Chip(context);
                chip.setText(server);

                nameservers.addView(chip);
            }
        }
    }
}
