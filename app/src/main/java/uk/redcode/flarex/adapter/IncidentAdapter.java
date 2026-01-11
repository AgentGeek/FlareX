package uk.redcode.flarex.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.CFIncident;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder> {

    private final ArrayList<CFIncident> list;
    private final LayoutInflater inflater;

    public IncidentAdapter(Context context, ArrayList<CFIncident> data) {
        this.inflater = LayoutInflater.from(context);
        this.list = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_incident_handler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final TextView noIncident;
        private final LinearLayout container;

        ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.incident_date);
            noIncident = itemView.findViewById(R.id.incident_empty);
            container = itemView.findViewById(R.id.incident_container);
        }

        public void bind(CFIncident handler) {
            date.setText(handler.date);

            if (handler.incidents.size() == 0) {
                noIncident.setVisibility(View.VISIBLE);
                return;
            }

            for (CFIncident.Incident incident : handler.incidents) {
                View view = inflater.inflate(R.layout.row_incident, container, false);

                ((TextView) view.findViewById(R.id.cf_incident_title)).setText(incident.title);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    ((TextView) view.findViewById(R.id.cf_incident_update)).setText(Html.fromHtml(incident.getUpdates(), Html.FROM_HTML_MODE_COMPACT));
                else
                    ((TextView) view.findViewById(R.id.cf_incident_update)).setText(Html.fromHtml(incident.getUpdates()));

                container.addView(view);
            }
        }

    }

}
