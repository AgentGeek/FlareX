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
import uk.redcode.flarex.object.DNSRecord;
import uk.redcode.flarex.object.Zone;

public class DNSRecordAdapter extends RecyclerView.Adapter<DNSRecordAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<DNSRecord> records;
    private final Zone zone;
    private DNSListener listener = null;

    public interface DNSListener {
        void onDNSSelected(DNSRecord record);
    }

    public DNSRecordAdapter(Context context, ArrayList<DNSRecord> records, Zone zone) {
        //this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.records = records;
        this.zone = zone;
    }

    public void setListener(DNSListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_dns, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DNSRecordAdapter.ViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void remove(int position) {
        records.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, records.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView type;
        final TextView name;
        final TextView content;
        final TextView ttl;
        final ImageView proxied;

        public final View foreground;
        public final View backgroundRight;
        public final View backgroundLeft;

        ViewHolder(View itemView) {
            super(itemView);
            ttl = itemView.findViewById(R.id.dns_ttl);
            type = itemView.findViewById(R.id.dns_type);
            name = itemView.findViewById(R.id.dns_name);
            content = itemView.findViewById(R.id.dns_content);
            proxied = itemView.findViewById(R.id.dns_proxied);
            foreground = itemView.findViewById(R.id.dns_foreground);
            backgroundRight = itemView.findViewById(R.id.dns_background_right);
            backgroundLeft = itemView.findViewById(R.id.dns_background_left);
            //itemView.findViewById(R.id.zone_select).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onDNSSelected(records.get(getAdapterPosition()));
        }

        public void bind(DNSRecord record) {
            String n = record.name.replace(zone.name, "");
            if (n.isEmpty()) n = "@";

            type.setText(record.type);
            name.setText(n);
            content.setText(record.content);
            ttl.setText(record.getTTL());
            proxied.setImageResource(record.getProxiedImg());
        }
    }
}
