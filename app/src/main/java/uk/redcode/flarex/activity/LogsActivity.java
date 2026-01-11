package uk.redcode.flarex.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import uk.redcode.flarex.R;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.Logger;
import uk.redcode.flarex.params.AppParamTheme;

public class LogsActivity extends AppCompatActivity {

    private LinearLayout logsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTheme(AppParamTheme.getTheme(AppParameter.getInt(this, AppParameter.THEME, 0)));
        setContentView(R.layout.activity_logs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.application_logs );

        logsView = findViewById(R.id.logs_container);
        buildLogs();
    }

    private void buildLogs() {
        for (String log : Logger.logs) {
            TextView tv = new TextView(this);
            tv.setText(getColoredText(log));
            tv.setTypeface(ResourcesCompat.getFont(this, R.font.hack_regular));
            logsView.addView(tv);
        }
    }

    private Spannable getColoredText(String str) {
        Spannable span = new SpannableString(str);
        int end = 0, color = Color.BLUE;

        if (str.startsWith("[INFO]")) {
            end = 6;
            color = getColor(R.color.info);
        } else if (str.startsWith("[ERROR]")) {
            end = 7;
            color = getColor(R.color.error);
        } else if (str.startsWith("[WARNING]")) {
            end = 9;
            color = getColor(R.color.secondary);
        } else if (str.startsWith("[NETWORK]")) {
            end = 9;
            color = getColor(R.color.network);
        }

        span.setSpan(new ForegroundColorSpan(color), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span = colorTag(str, span);
        return span;
    }

    private Spannable colorTag(String str, Spannable span) {
        int start = -1;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '{' && str.charAt(i+1) != '"') {
                start = i;
            } else if (str.charAt(i) == '}' && start != -1) {
                span.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), start, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return span;
            }
        }
        return span;
    }
}