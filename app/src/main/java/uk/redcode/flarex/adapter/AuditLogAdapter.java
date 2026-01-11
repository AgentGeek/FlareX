package uk.redcode.flarex.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AuditLog;
import uk.redcode.flarex.object.Parser;

public class AuditLogAdapter extends RecyclerView.Adapter<AuditLogAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<AuditLog> logs;

    public AuditLogAdapter(Context context, ArrayList<AuditLog> logs) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.logs = logs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_audit_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditLogAdapter.ViewHolder holder, int position) {
        holder.bind(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final Chip interfaceType;
        final TextView ip;
        final TextView date;
        final TextView actor;
        final TextView email;
        final TextView type;
        final ImageView result;
        final LinearLayout values;
        final LinearLayout metadata;
        final LinearLayout ipLayout;
        final LinearLayout userLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ip = itemView.findViewById(R.id.log_ip);
            date = itemView.findViewById(R.id.log_date);
            type = itemView.findViewById(R.id.log_type);
            actor = itemView.findViewById(R.id.log_actor_type);
            email = itemView.findViewById(R.id.log_actor_email);
            values = itemView.findViewById(R.id.log_values);
            metadata = itemView.findViewById(R.id.log_metadata);
            ipLayout = itemView.findViewById(R.id.log_ip_layout);
            userLayout = itemView.findViewById(R.id.log_user_layout);

            result = itemView.findViewById(R.id.log_result);
            interfaceType = itemView.findViewById(R.id.log_interface);
        }

        public void bind(AuditLog log) {
            // interface type
            interfaceType.setText(getChipLabel(log));
            interfaceType.setVisibility(getChipVisibility(log));
            interfaceType.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, getInterfaceColor(log))));
            // type & result
            type.setText(log.type);
            result.setImageResource(log.result ? R.drawable.ic_status_ok : R.drawable.ic_status_ko);
            // actor
            actor.setText(log.actor.type);
            email.setText(log.actor.email);
            userLayout.setVisibility(log.actor.type.equals("system") ? View.GONE : View.VISIBLE);
            date.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT, Locale.getDefault()).format(Parser.parseDate(log.date).getTimeInMillis()));
            ip.setText(log.actor.ip);
            ipLayout.setVisibility(log.actor.type.equals("system") ? View.GONE : View.VISIBLE);
            // metadata
            buildMetadata(metadata, log.metadata);
            // value
            buildValues(values, log);
        }

        private String getChipLabel(AuditLog log) {
            if (!log.interfaceType.isEmpty()) return log.interfaceType;
            if (log.actor.type.equals("system")) return log.actor.type;
            return "";
        }

        private int getChipVisibility(AuditLog log) {
            if (!log.interfaceType.isEmpty()) return View.VISIBLE;
            if (log.actor.type.equals("system")) return View.VISIBLE;
            return View.GONE;
        }

        private int getInterfaceColor(AuditLog log) {
            if (log.interfaceType.equals("UI")) return R.color.info;
            if (log.interfaceType.equals("API")) return R.color.secondary;
            if (log.actor.type.equals("system")) return R.color.network;
            return R.color.error;
        }

        private void buildMetadata(LinearLayout container, HashMap<String, String> list) {
            container.removeAllViews();
            buildMetadataHeader(container, list);
            for (HashMap.Entry<String, String> entry : list.entrySet()) {
                View row = inflater.inflate(R.layout.row_key_value, container, false);
                ((TextView) row.findViewById(R.id.key)).setText(entry.getKey());
                ((TextView) row.findViewById(R.id.value)).setText(entry.getValue());
                container.addView(row);
            }
        }

        private void buildMetadataHeader(LinearLayout container, HashMap<String, String> list) {
            if (list.size() > 0) return;
            TextView header = new TextView(context);
            header.setText(R.string.no_metadata);
            header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            container.addView(header);
        }

        private void buildValues(LinearLayout container, AuditLog log) {
            container.removeAllViews();
            if (!buildValuesHeader(container, log)) return;

            if (log.oldValueJson == null) {
                View row = inflater.inflate(R.layout.row_value_change, container, false);
                row.findViewById(R.id.value_label).setVisibility(View.GONE);
                row.findViewById(R.id.spacer).setVisibility(View.GONE);
                ((TextView) row.findViewById(R.id.old_value)).setText(log.oldValue);
                ((TextView) row.findViewById(R.id.new_value)).setText(log.newValue);
                container.addView(row);
                return;
            }

            for (HashMap.Entry<String, String> entry : log.oldValueJson.entrySet()) {
                View row = inflater.inflate(R.layout.row_value_change, container, false);
                ((TextView) row.findViewById(R.id.value_label)).setText(entry.getKey());
                ((TextView) row.findViewById(R.id.old_value)).setText(entry.getValue());
                ((TextView) row.findViewById(R.id.new_value)).setText(log.newValueJson == null ? "⍉" : log.newValueJson.get(entry.getKey()));
                container.addView(row);
            }
        }

        private boolean buildValuesHeader(LinearLayout container, AuditLog log) {
            if (log.oldValueJson != null || (!log.oldValue.equals(log.newValue))) return true;
            TextView header = new TextView(context);
            header.setText(R.string.no_value_changed);
            header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            container.addView(header);
            return false;
        }
    }
}
