package uk.redcode.flarex.params;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.Certificate;
import uk.redcode.flarex.object.Zone;

public class ParamEdgeCertificates extends uk.redcode.flarex.object.Param {

    // GET https://api.cloudflare.com/client/v4/zones/ZONE-ID/ssl/certificate_packs
    private static final String TAG = "Param-EdgeCertificates";
    private LayoutInflater inflater;
    private LinearLayout container;

    @Override
    public void onDraw(LayoutInflater inflater, LinearLayout parent, Zone zone) {
        View root = inflater.inflate(R.layout.param_certificates, parent, false);
        this.inflater = inflater;
        super.onDraw(root, zone);

        container = root.findViewById(R.id.param_container);

        parent.addView(root);
    }

    @Override
    public void refresh() {
        setLoading(true);

        CFApi.getEdgeCertificates(context, zone.zoneId, new CFApi.CertificateListener() {
            @Override
            public void onResult(ArrayList<Certificate> certificates) {
                buildView(certificates);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setError(true);
                setLoading(false);
            }
        });
    }

    private void buildView(ArrayList<Certificate> certificates) {
        container.removeAllViews();

        TextView title = new TextView(context);
        title.setText(R.string.edge_certificates);
        title.setTextSize(16f);
        title.setTextColor(context.getColor(R.color.primary));
        container.addView(title);

        for (Certificate cert : certificates) {
            View view = inflater.inflate(R.layout.row_certificate, container, false);

            ((Chip) view.findViewById(R.id.certificate_status)).setText(cert.status);
            ((Chip) view.findViewById(R.id.certificate_status)).setTextColor(cert.getStatusColor(context));
            ((TextView) view.findViewById(R.id.certificate_type)).setText(cert.type);

            // build hosts
            ((LinearLayout) view.findViewById(R.id.certificate_hosts)).removeAllViews();
            for (String host : cert.hosts) {
                Chip chip = new Chip(context);
                chip.setText(host);
                ((LinearLayout) view.findViewById(R.id.certificate_hosts)).addView(chip);
            }

            container.addView(view);
        }
    }

}
