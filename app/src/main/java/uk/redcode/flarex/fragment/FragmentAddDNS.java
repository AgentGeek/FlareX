package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import uk.redcode.flarex.R;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.DNSRecord;
import uk.redcode.flarex.ui.Alert;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentAddDNS extends FragmentCC {

    private static final int A = 0;
    private static final int AAAA = 1;
    private static final int CAA = 2;
    private static final int CERT = 3;
    private static final int CNAME = 4;
    private static final int DS = 0;
    private static final int HTTPS = 0;
    private static final int LOC = 0;
    private static final int MX = 0;
    private static final int NAPTR = 0;
    private static final int NS = 0;
    private static final int PTR = 0;
    private static final int SMIMEA = 0;
    private static final int SPF = 0;
    private static final int SRV = 0;
    private static final int SSHFP = 0;
    private static final int SVCB = 0;
    private static final int TLSA = 0;
    private static final int TXT = 0;
    private static final int URI = 0;

    private Spinner ttl;
    private Spinner type;
    private ArrayAdapter<CharSequence> adapterType;
    private ImageView proxy;
    private TextInputEditText content;
    private TextInputLayout contentLayout;
    private TextInputEditText name;
    private TextInputLayout nameLayout;
    private MaterialButton addBtn;
    public DNSRecord record = null;

    private boolean proxied = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableBackView = true;
        this.lastView = ViewManager.VIEW_DNS;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_dns, container, false);

        // spinner type
        adapterType = ArrayAdapter.createFromResource(getContext(), R.array.dns_records, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type = root.findViewById(R.id.record_type);
        type.setAdapter(adapterType);

        // spinner ttl
        ArrayAdapter<CharSequence> adapterTTL = ArrayAdapter.createFromResource(getContext(), R.array.ttl, android.R.layout.simple_spinner_item);
        adapterTTL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ttl = root.findViewById(R.id.record_ttl);
        ttl.setAdapter(adapterTTL);

        proxy = root.findViewById(R.id.record_proxy);
        proxy.setOnClickListener(v -> {
            proxied = !proxied;
            proxy.setImageResource(proxied ? R.drawable.ic_proxied : R.drawable.ic_no_proxy);
        });
        content = root.findViewById(R.id.record_content);
        contentLayout = root.findViewById(R.id.record_content_layout);
        name = root.findViewById(R.id.record_name);
        nameLayout = root.findViewById(R.id.record_name_layout);
        addBtn = root.findViewById(R.id.record_add);
        addBtn.setOnClickListener(v -> createDNSRecord());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecord();
    }

    private void loadRecord() {
        name.setText(record == null ? "" : record.name);
        content.setText(record == null ? "" : record.content);
        type.setSelection(record == null ? 0 : adapterType.getPosition(record.type));
        addBtn.setText(record == null ? R.string.add : R.string.edit);
        ttl.setSelection(record == null ? 0 : getTTLPosition(record.ttl));
        if (record != null) {
            if (proxied != record.proxied) proxy.callOnClick();
        } else if (!proxied) {
            proxy.callOnClick();
        }
    }

    private void createDNSRecord() {
        if (!verify()) return;

        try {
            JSONObject record = new JSONObject();
            record.put("type", type.getSelectedItem().toString());
            record.put("content", content.getText().toString());
            record.put("name", name.getText().toString());
            record.put("ttl", getTTLValue());
            record.put("proxied", proxied);

            setLoading(true);
            send(record);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
            setLoading(false);
        }
    }

    private void send(JSONObject data) {
        CFApi.JSONListener listener = new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                setLoading(false);
                new Alert(Alert.SUCCESS, record == null ? R.string.dns_record_added : R.string.dns_record_edited);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                new Alert(Alert.ERROR, e.getLocalizedMessage());
            }
        };

        if (record == null)
            CFApi.addDNSRecords(getMain(), zone.zoneId, data, listener);
        else
            CFApi.editDNSRecord(getMain(), zone.zoneId, data, record.recordId, listener);
    }

    private int getTTLValue() {
        String value = ttl.getSelectedItem().toString().toLowerCase();
        if (value.equals("auto")) return 1;

        String[] split = value.split(" ");
        int base = Integer.parseInt(split[0]);

        if (split[1].contains("minute")) {
            base *= 60;
        } else if (split[1].contains("hour")) {
            base *= 60 * 60;
        } else if (split[1].contains("day")) {
            base *= 60 * 60 * 24;
        }

        return base;
    }

    private int getTTLPosition(int ttl) {
        switch (ttl) {
            case 1800: return 1; // 30m
            case 3600: return 2; // 1h
            case 7200: return 3; // 2h
            case 10800: return 4; // 3h
            case 14400: return 5; // 4h
            case 18000: return 6; // 5h
            case 21600: return 7; // 6h
            case 28800: return 8; // 8h
            case 43200: return 9; // 12h
            case 57600: return 10; // 16h
            case 72000: return 11; // 20h
            case 86400: return 12; // 1d
            case 1: default: return 0; // auto
        }
    }

    private boolean verify() {
        boolean exit = true;

        if (name.getText().length() == 0) {
            nameLayout.setErrorEnabled(true);
            nameLayout.setError(getString(R.string.cant_be_empty));
            exit = false;
        } else if (name.getText().length() > 255) {
            nameLayout.setErrorEnabled(true);
            nameLayout.setError(getString(R.string.too_long));
            exit = false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        if (content.getText().length() == 0) {
            contentLayout.setErrorEnabled(true);
            contentLayout.setError(getString(R.string.cant_be_empty));
            exit = false;
        } else {
            contentLayout.setErrorEnabled(false);
        }

        return exit;
    }
}
