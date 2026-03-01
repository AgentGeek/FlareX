package in.ankrypt.flarex.tester;

import android.content.Context;

import java.util.ArrayList;

import in.ankrypt.flarex.adapter.TokenTestAdapter;
import in.ankrypt.flarex.network.CFApi;
import in.ankrypt.flarex.object.Certificate;
import in.ankrypt.flarex.ui.LayoutManager;

public class CertificateTester extends Tester {

    private static final String key = LayoutManager.CERTIFICATES;

    public CertificateTester(Context context) {
        super(context);
        this.name = "Certificates";
        this.permission = "Zone.SSL and Certificates";
    }

    @Override
    public void runTest(int position, TokenTestAdapter adapter, String zone, TestListener listener) {
        super.runTest(position, adapter, zone, listener);
        setLoading(true);

        CFApi.getEdgeCertificates(context, zone, new CFApi.CertificateListener() {
            @Override
            public void onResult(ArrayList<Certificate> certificates) {
                CertificateTester.this.icon = SUCCESS;
                CertificateTester.this.result = "Can read certificates";
                setLoading(false);
                listener.onFinish(zone);
            }

            @Override
            public void onError(Exception e) {
                CertificateTester.this.icon = WARNING;
                CertificateTester.this.result = "Can't read certificates";
                setLayout(key, false);
                setLoading(false);
            }
        });
    }
}
