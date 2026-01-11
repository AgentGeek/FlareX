package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.RuleSet;

public class RuleSetAdapter extends RecyclerView.Adapter<RuleSetAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<RuleSet> ruleSets;
    private RuleSetListener listener = null;

    public interface RuleSetListener {
        void onRuleSetSelected(RuleSet ruleSet);
    }

    public RuleSetAdapter(Context context, ArrayList<RuleSet> ruleSets) {
        this.inflater = LayoutInflater.from(context);
        this.ruleSets = ruleSets;
    }

    public void setListener(RuleSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RuleSetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_ruleset, parent, false);
        return new RuleSetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleSetAdapter.ViewHolder holder, int position) {
        holder.bind(ruleSets.get(position));
    }

    @Override
    public int getItemCount() {
        return ruleSets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final TextView description;
        private final TextView version;
        private final TextView phase;
        private final TextView kind;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ruleset_name);
            kind = itemView.findViewById(R.id.ruleset_kind);
            description = itemView.findViewById(R.id.ruleset_description);
            phase = itemView.findViewById(R.id.ruleset_phase);
            version = itemView.findViewById(R.id.ruleset_version);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onRuleSetSelected(ruleSets.get(getAdapterPosition()));
        }

        public void bind(RuleSet ruleSet) {
            name.setText(ruleSet.name);
            description.setText(ruleSet.description);
            version.setText(ruleSet.version);
            phase.setText(ruleSet.getPhaseLabel());
            kind.setText(ruleSet.kind);
        }
    }
}