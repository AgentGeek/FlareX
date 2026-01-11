package uk.redcode.flarex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.FirewallRule;

public class FirewallRuleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<FirewallRule> rules;

    private static final int VIEW_EMPTY = 0;
    private static final int VIEW_RULE = 1;

    public FirewallRuleAdapter(Context context, ArrayList<FirewallRule> rules) {
        this.inflater = LayoutInflater.from(context);
        this.rules = rules;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_EMPTY) {
            View emptyView = inflater.inflate(R.layout.row_firewall_rule_empty, parent, false);
            return new ViewHolderEmpty(emptyView);
        }

        View view = inflater.inflate(R.layout.row_firewall_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (rules.size() == 0) return;

        ((FirewallRuleAdapter.ViewHolder) holder).bind(rules.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return rules.size() == 0 ? VIEW_EMPTY : VIEW_RULE;
    }

    @Override
    public int getItemCount() {
        return rules.size() == 0 ? 1 : rules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final Chip priority;
        final Chip action;
        final TextView name;
        final SwitchMaterial paused;

        ViewHolder(View itemView) {
            super(itemView);

            priority = itemView.findViewById(R.id.rule_priority);
            action = itemView.findViewById(R.id.rule_action);
            name = itemView.findViewById(R.id.rule_name);
            paused = itemView.findViewById(R.id.rule_paused);
        }

        @Override
        public void onClick(View view) {

        }

        public void bind(FirewallRule rule) {
            /*priority.setText(String.valueOf(rule.priority));
            action.setText(rule.action);
            name.setText(rule.description);
            paused.setChecked(!rule.paused);*/
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {

        public ViewHolderEmpty(@NonNull View itemView) {
            super(itemView);
        }
    }
}
