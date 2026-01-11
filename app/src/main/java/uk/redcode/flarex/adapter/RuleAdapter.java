package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.divider.MaterialDivider;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.FirewallRule;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<FirewallRule> rules;

    public RuleAdapter(Context context, ArrayList<FirewallRule> rules) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.rules = rules;
    }

    @NonNull
    @Override
    public RuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_rule, parent, false);
        return new RuleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleAdapter.ViewHolder holder, int position) {
        holder.bind(rules.get(position));
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView action;
        private final TextView description;
        private final TextView content;
        private final ImageView enabled;
        private final ChipGroup categories;
        private final MaterialDivider divider;

        ViewHolder(View itemView) {
            super(itemView);
            action = itemView.findViewById(R.id.rule_action);
            description = itemView.findViewById(R.id.rule_description);
            content = itemView.findViewById(R.id.rule_content);
            enabled = itemView.findViewById(R.id.rule_enabled);
            categories = itemView.findViewById(R.id.rule_categories);
            divider = itemView.findViewById(R.id.divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //if (listener != null) listener.onRuleSetSelected(ruleSets.get(getAdapterPosition()));
        }

        public void bind(FirewallRule rule) {
            action.setText(rule.getActionLabel());
            description.setText(rule.description);
            content.setText(rule.getContent());
            content.setVisibility(rule.getContent().isEmpty() ? View.GONE : View.VISIBLE);
            enabled.setImageResource(rule.enabled ? R.drawable.ic_status_ok : R.drawable.ic_status_pause);
            divider.setVisibility(getAdapterPosition() == rules.size()-1 ? View.GONE : View.VISIBLE);
            buildCategories(rule.categories);
        }

        private void buildCategories(ArrayList<String> list) {
            categories.removeAllViews();
            for (String str : list) {
                Chip chip = new Chip(context);
                chip.setText(str);
                categories.addView(chip);
            }
        }

    }

}
