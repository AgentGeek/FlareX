package uk.redcode.flarex.tester;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.ui.LayoutManager;

public class Tester {

    public String name = "No Name";
    public String result = "";
    public String permission = "Error";
    public int icon = -1;
    public boolean loading = false;
    public Context context = null;

    private int position = -1;
    private TokenTestAdapter adapter = null;

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int WARNING = 2;

    public Tester(Context context) {
        this.context = context;
    }

    public interface TestListener {
        void onFinish(String zoneId);
    }

    public void bind(TokenTestAdapter.ViewHolder holder) {
        holder.title.setText(getSpan());
        holder.result.setText(result);

        holder.progress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        if (icon != -1) {
            holder.progress.setVisibility(View.GONE);
            holder.resultIcon.setVisibility(View.VISIBLE);
            holder.resultIcon.setImageResource(getIcon());
        }
    }

    private Spannable getSpan() {
        if (permission.isEmpty()) return new SpannableString(this.name);

        Spannable span = new SpannableString(name + " - " + permission);
        span.setSpan(new ForegroundColorSpan(context.getColor(R.color.secondary)), name.length()+3, name.length()+3+permission.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return span;
    }

    private int getIcon() {
        switch (icon) {
            case ERROR: return R.drawable.ic_error;
            case WARNING: return R.drawable.ic_warning;
            case SUCCESS: return R.drawable.ic_status_ok;
            default: return R.drawable.ic_question;
        }
    }

    public void setLoading(boolean show) {
        if (position == -1 || adapter == null) return;

        this.loading = show;
        this.adapter.notifyItemChanged(position);
    }

    public void setLayout(String key, boolean value) {
        LayoutManager.set(key, value);
    }
    public void setLayout(String key, String keyEdit, boolean value) {
        setLayout(key, value);
        setLayout(keyEdit, value);
    }
    public void setLayout(ArrayList<String> keys, boolean value) {
        for (String key : keys) {
            LayoutManager.set(key, value);
        }
    }

    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        this.position = position;
        this.adapter = adapter;
    }
}
