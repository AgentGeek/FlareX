package uk.redcode.flarex.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.MainActivity;
import uk.redcode.flarex.R;
import uk.redcode.flarex.activity.LoginActivity;
import uk.redcode.flarex.adapter.TokenTestAdapter;
import uk.redcode.flarex.object.User;
import uk.redcode.flarex.tester.CertificateTester;
import uk.redcode.flarex.tester.DNSTester;
import uk.redcode.flarex.tester.FirewallTester;
import uk.redcode.flarex.tester.GraphTester;
import uk.redcode.flarex.tester.NotificationTester;
import uk.redcode.flarex.tester.Tester;
import uk.redcode.flarex.tester.ZoneConfigTester;
import uk.redcode.flarex.tester.ZoneTester;
import uk.redcode.flarex.ui.LayoutManager;

public class FragmentTokenTest extends Fragment {

    private RecyclerView recycler;
    private int position = 0;
    private TokenTestAdapter adapter = null;
    private ArrayList<Tester> list = new ArrayList<>();

    private String zoneId = "";
    private boolean exited = false;
    private boolean finished = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_token_tester, container, false);
        recycler = root.findViewById(R.id.recycler);

        list = new ArrayList<Tester>() {{
            add(new ZoneTester(requireContext()));
            add(new GraphTester(requireContext()));
            add(new DNSTester(requireContext()));
            add(new ZoneConfigTester(requireContext()));
            add(new CertificateTester(requireContext()));
            add(new FirewallTester(requireContext()));
            add(new NotificationTester(requireContext()));
        }};

        exited = false;
        finished = false;
        requireActivity().invalidateMenu();
        return root;
    }

    public void stop() {
        exited = true;
        finished = false;
        User.logout(requireContext());
        Activity activity = requireActivity();
        if (activity instanceof LoginActivity) ((LoginActivity) activity).showLogin();
        else Toast.makeText(requireContext(), "Fatal Error: Parent is not LoginActivity", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createAdapter();
        position = 0;
        exited = false;
        finished = false;
        runTest();
    }

    private void runTest() {
        if (exited) return;
        if (position == 1 && list.get(0).icon == Tester.ERROR) {
            stop();
            return;
        }

        if (position >= list.size()) {
            finished = true;
            requireActivity().invalidateMenu();
            return;
        }

        Tester tester = list.get(position);
        tester.runTest(position, adapter, zoneId, zoneId -> {
            if (FragmentTokenTest.this.zoneId.isEmpty()) FragmentTokenTest.this.zoneId = zoneId;
            position++;
            runTest();
        });
    }

    private void createAdapter() {
        adapter = new TokenTestAdapter(requireContext(), list);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (!finished) return false;
        if (!LayoutManager.hasLayout()) {
            User.logout(requireContext());
            Toast.makeText(requireContext(), R.string.at_least_one_permission_required, Toast.LENGTH_LONG).show();
            return false;
        }

        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.tester, menu);
        return true;
    }

    public boolean validateToken() {
        if (exited) return true;
        if (!finished) {
            Toast.makeText(requireContext(), "Fatal Error: Test not finished", Toast.LENGTH_LONG).show();
        } else {
            LayoutManager.save(requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
