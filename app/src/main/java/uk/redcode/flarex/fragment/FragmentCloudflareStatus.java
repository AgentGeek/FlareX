package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.CFStatusAdapter;
import uk.redcode.flarex.adapter.IncidentAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.CFIncident;
import uk.redcode.flarex.object.CFStatus;
import uk.redcode.flarex.object.CFStatusCategory;

public class FragmentCloudflareStatus extends FragmentCC {

    private ArrayList<CFStatusCategory> statusList = new ArrayList<>();
    private ArrayList<CFIncident> incidentsList = new ArrayList<>();

    private RecyclerView recycler;

    private boolean refreshing = false;
    private boolean drawGreenHeader = true;
    private String incidentLabel = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableBackView = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cloudflare_status, container, false);

        recycler = root.findViewById(R.id.recycler);

        ((TabLayout) root.findViewById(R.id.status_tabs)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                    showStatus();
                else if (tab.getPosition() == 1)
                    showIncidents();
                else
                    Toast.makeText(getContext(), "No Tab position:"+tab.getPosition(), Toast.LENGTH_LONG).show();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (!refreshing) updateList();
    }

    public void updateList() {
        refreshing = true;
        setLoading(true);
        recycler.setVisibility(View.GONE);
        CFApi.getStatus(getContext(), new CFApi.HTMLListener() {
            @Override
            public void onResult(String html) {
                parse(html);
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showStatus() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        CFStatusAdapter statusAdapter = new CFStatusAdapter(getContext(), statusList);
        statusAdapter.drawGreenHeader = drawGreenHeader;
        statusAdapter.incidentLabel = incidentLabel;
        recycler.setAdapter(statusAdapter);
    }

    private void showIncidents() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        IncidentAdapter incidentAdapter = new IncidentAdapter(getContext(), incidentsList);
        recycler.setAdapter(incidentAdapter);
    }

    /*
        Parsing Part
     */

    private void parse(String html) {
        try {
            Document doc = Jsoup.parse(html);
            Element main = doc.select(".container").first();

            drawGreenHeader = true;
            incidentLabel = "";
            parseStatus(main);
            parseIncidents(main);

            showStatus();
            recycler.setVisibility(View.VISIBLE);
            refreshing = false;
            setLoading(false);
        } catch (Exception e) {
            setLoading(false);
            e.printStackTrace();
            Toast.makeText(getContext(), "Parsing Error", Toast.LENGTH_LONG).show();
        }
    }

    private void parseStatus(Element main) throws NullPointerException {
        this.statusList = new ArrayList<>();

        // check header
        if (main.select(".page-status").isEmpty()) {
            drawGreenHeader = false;
            incidentLabel = main.select(".unresolved-incidents a.actual-title").text().trim();
        }

        // first parse the status
        Elements cats = main.select(".components-section .components-container .is-group");

        for (Element elem : cats) {
            CFStatusCategory cat = new CFStatusCategory();
            cat.name = elem.select(".component-inner-container .name").first().text().trim();

            //Log.d("DEBUG", "parsePage: Category: "+cat.name);

            Elements list = elem.select(".child-components-container .component-inner-container");
            for (Element actual : list) {
                CFStatus s = new CFStatus();

                s.name = actual.select(".name").text().trim();
                s.state = actual.select(".component-status").text().trim();

                if (actual.hasClass("status-orange")) s.color = CFStatus.ORANGE;
                if (actual.hasClass("status-yellow")) s.color = CFStatus.ORANGE;

                //s.print();
                cat.status.add(s);
            }
            statusList.add(cat);
        }
    }

    private void parseIncidents(Element main) throws NullPointerException {
        this.incidentsList = new ArrayList<>();

        Elements list = main.select(".incidents-list div.status-day");

        for (Element actual : list) {
            CFIncident handler = new CFIncident();

            // parse date
            handler.date = actual.select(".date").text().trim();

            // check if no incident
            if (actual.select(".incident-container").size() == 0) {
                incidentsList.add(handler);
                continue;
            }

            Elements incidents = actual.select(".incident-container");
            for (Element elem : incidents) {
                CFIncident.Incident incident = new CFIncident.Incident();

                incident.title = elem.select(".incident-title a").text().trim();
                Elements updates = elem.select(".updates-container .update");
                for (Element update : updates) {
                    CFIncident.Update u = new CFIncident.Update();
                    u.status = update.select("strong").text().trim();
                    u.date = update.select("small").text().trim();
                    u.text = update.text().trim()
                            .replace(String.format("%s - ", u.status), "")
                            .replace(String.format("%s", u.date), "");
                    incident.updates.add(u);
                }
                handler.incidents.add(incident);
            }

            incidentsList.add(handler);
        }
    }
}
