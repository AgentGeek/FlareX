package in.ankrypt.flarex.params;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import in.ankrypt.flarex.R;
import in.ankrypt.flarex.object.Param;
import in.ankrypt.flarex.object.Zone;

public class AppParamLinks extends Param {

    private final static String LINK_TELEGRAM = "https://t.me/FlareXapp";
    private final static String LINK_GITLAB = "https://github.com/Ankverse/FlareX";
    private final static String LINK_WEBSITE = "https://ankrypt.in/flarex";

    private Activity activity;

    public AppParamLinks setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_links, parent, false);

        root.findViewById(R.id.link_telegram).setOnClickListener(view -> open(LINK_TELEGRAM));
        root.findViewById(R.id.link_gitlab).setOnClickListener(view -> open(LINK_GITLAB));
        root.findViewById(R.id.link_website).setOnClickListener(view -> open(LINK_WEBSITE));

        parent.addView(root);
    }

    private void open(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

}
