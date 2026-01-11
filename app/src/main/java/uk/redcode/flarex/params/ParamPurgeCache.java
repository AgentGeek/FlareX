package uk.redcode.flarex.params;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Zone;

public class ParamPurgeCache extends uk.redcode.flarex.object.Param implements View.OnClickListener {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/purge_cache
    private static final String TAG = "Param-PurgeCache";
    private MaterialButton btnPurgeEverything;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_btn, parent, false);
        super.onDraw(root, zone);

        ((TextView) root.findViewById(R.id.param_name)).setText(R.string.purge_cache);
        ((TextView) root.findViewById(R.id.param_description)).setText(R.string.purge_cache_description);

        btnPurgeEverything = root.findViewById(R.id.param_btn);
        btnPurgeEverything.setText(R.string.purge_everything);
        btnPurgeEverything.setOnClickListener(this);

        parent.addView(root);
    }

    @Override
    public void onClick(View v) {
        // avoid user being able to spam it
        btnPurgeEverything.setOnClickListener(null);
        setLoading(true);

        CFApi.purgeCache(context, zone.zoneId, new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                Log.d(TAG, "onChange: "+body.toString());
                Toast.makeText(context, R.string.cache_purged, Toast.LENGTH_SHORT).show();
                btnPurgeEverything.setOnClickListener(ParamPurgeCache.this);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setError(true);
                setLoading(false);
            }
        });
    }
}
