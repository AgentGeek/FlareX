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
import uk.redcode.flarex.object.CFAccount;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<CFAccount> accounts;
    private AccountListener listener = null;

    public interface AccountListener {
        void onAccountSelected(CFAccount account);
    }

    public AccountAdapter(Context context, ArrayList<CFAccount> accounts) {
        this.inflater = LayoutInflater.from(context);
        this.accounts = accounts;
    }

    public void setListener(AccountListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
        holder.bind(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView name;
        final TextView type;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.account_name);
            type = itemView.findViewById(R.id.account_type);
            itemView.findViewById(R.id.account_select).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onAccountSelected(accounts.get(getAdapterPosition()));
        }

        public void bind(CFAccount account) {
            name.setText(account.name);
            type.setText(account.type);
        }
    }
}
