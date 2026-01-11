package uk.redcode.flarex.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.adapter.RuleAdapter;
import uk.redcode.flarex.adapter.RuleSetAdapter;
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.RuleSet;
import uk.redcode.flarex.ui.ViewManager;

public class FragmentFirewall extends FragmentCC implements RuleSetAdapter.RuleSetListener {

    // General
    private int actualView = VIEW_RULESET;
    private RecyclerView recycler;

    // View RuleSet
    private static final int VIEW_RULESET = 0;
    private RuleSetAdapter ruleSetAdapter = null;
    private ArrayList<RuleSet> ruleSets = null;
    private RuleSet selectedRuleSet = null;

    // View Rules
    private static final int VIEW_RULE = 1;
    private RuleAdapter ruleAdapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler, container, false);
        recycler = root.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        draw();
    }

    private void draw() {
        if (actualView == VIEW_RULESET) {
            setTitle(R.string.firewall_ruleset);
            if (ruleSets == null) {
                getRuleSets();
                return;
            }
            ruleSetAdapter = new RuleSetAdapter(requireContext(), ruleSets);
            ruleSetAdapter.setListener(this);
            recycler.setAdapter(ruleSetAdapter);
        } else if (actualView == VIEW_RULE) {
            if (selectedRuleSet == null) {
                Toast.makeText(requireContext(), R.string.no_ruleset_selected, Toast.LENGTH_SHORT).show();
                return;
            }
            setTitle(String.format(getString(R.string.firewall_rules), selectedRuleSet.name));
            if (selectedRuleSet.needUpdate) {
                updateRules();
                return;
            }
            ruleAdapter = new RuleAdapter(requireContext(), selectedRuleSet.rules);
            recycler.setAdapter(ruleAdapter);
        }

        setLoading(false);
    }

    private void updateRules() {
        setLoading(true);
        selectedRuleSet.refreshRules(requireContext(), zone.zoneId, new RuleSet.RuleListener() {
            @Override
            public void onRuleRefreshed() {
                draw();
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    /*
        RuleSet Related
     */

    private void getRuleSets() {
        setLoading(true);

        CFApi.getZoneRuleSet(requireContext(), zone.zoneId, new CFApi.RuleSetListener() {
            @Override
            public void onResult(ArrayList<RuleSet> ruleSets) {
                FragmentFirewall.this.ruleSets = ruleSets;
                draw();
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
            }
        });
    }

    @Override
    public void onRuleSetSelected(RuleSet ruleSet) {
        selectedRuleSet = ruleSet;
        actualView = VIEW_RULE;
        draw();
    }

    /*
        Toolbar icon listener
     */
    public void onBack() {
        Log.d("HERE", "onClick: RETURN: "+actualView);
        if (actualView == VIEW_RULE) {
            actualView = VIEW_RULESET;
            draw();
        } else {
            setView(ViewManager.VIEW_APPS, null);
        }
    }


    /*private TabLayout tabs;
    private RecyclerView recycler;

    private ArrayList<FirewallEvent> events = new ArrayList<>();
    private ArrayList<FirewallRule> rules = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup)  inflater.inflate(R.layout.fragment_firewall, container, false);
        recycler = root.findViewById(R.id.recycler);
        tabs = root.findViewById(R.id.tabs_firewall);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                    showEvents();
                else if (tab.getPosition() == 1)
                    showRules();
                else
                    Toast.makeText(getContext(), "No Tab position:"+tab.getPosition(), Toast.LENGTH_LONG).show();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }

    private void showEvents() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        FirewallEventAdapter eventAdapter = new FirewallEventAdapter(getContext(), events);
        recycler.setAdapter(eventAdapter);
    }

    private void showRules() {
        if (!isVisible()) return;
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        FirewallRuleAdapter rulesAdapter = new FirewallRuleAdapter(getContext(), rules);
        recycler.setAdapter(rulesAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateList();
    }

    private void updateList() {
        CFApi.JSONListener graphqlListener = new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                try {
                    JSONArray values = body.getJSONObject("data").getJSONObject("viewer").getJSONArray("zones").getJSONObject(0).getJSONArray("activity");
                    events = FirewallEvent.parse(values);
                    showEvents();
                    setLoading(false);
                } catch (JSONException e) {
                    setLoading(false);
                    Logger.error(e);
                    drawError(R.drawable.ic_error_parsing, R.string.error_parsing_graphql);
                }
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                drawError(R.drawable.ic_error_parsing, e.getLocalizedMessage());
            }
        };

        CFApi.RuleListener firewallListener = new CFApi.RuleListener() {
            @Override
            public void onResult(ArrayList<FirewallRule> rules) {
                FragmentFirewall.this.rules = rules;
                if (tabs.getSelectedTabPosition() == 1) showRules();
            }

            @Override
            public void onError(Exception e) {
            }
        };


        try {
            setLoading(true);
            JSONObject data = getFirewallPostData();

            CFApi.graphql(getMain(), data, graphqlListener);
            CFApi.getFirewallRules(getMain(), zone.zoneId, firewallListener);
        } catch (Exception e) {
            setLoading(false);
            Logger.error(e);
            drawError(R.drawable.ic_error_large, e.getLocalizedMessage());
        }

    }

    /*
        Request generation
     */

    /*private JSONObject getFirewallPostData() throws Exception {
        InputStream is = getResources().openRawResource(R.raw.graphql_firewall);
        byte[] b = new byte[is.available()];
        is.read(b);

        Date until = new Date();
        Date since = new Date(until.getTime() - (24 * 60 * 60 * 1000));


        // filter Date
        JSONObject filterDate = new JSONObject();
        filterDate.put("datetime_geq", getJSONDateForGraphQL(since));
        filterDate.put("datetime_leq", getJSONDateForGraphQL(until));

        JSONObject filterORNotRule = new JSONObject();
        filterORNotRule.put("AND", new JSONArray()
                .put(new JSONObject().put("ruleId_notlike", "9_____"))
                .put(new JSONObject().put("ruleId_notlike", "uri-9_____"))
        );

        // filter OR Rules
        JSONArray filterORRules = new JSONArray();
        filterORRules.put(new JSONObject().put("ruleId_like", "999___"));
        filterORRules.put(new JSONObject().put("ruleId_like", "900___"));
        filterORRules.put(new JSONObject().put("ruleId", "981176"));
        filterORRules.put(filterORNotRule);

        // filter OR
        JSONObject filterOR = new JSONObject();
        filterOR.put("OR", filterORRules);

        // filter action
        JSONArray filterANDAction = new JSONArray();
        filterANDAction.put(new JSONObject().put("action_neq", "challenge_solved"));
        filterANDAction.put(new JSONObject().put("action_neq", "challenge_failed"));
        filterANDAction.put(new JSONObject().put("action_neq", "challenge_bypassed"));
        filterANDAction.put(new JSONObject().put("action_neq", "jschallenge_solved"));
        filterANDAction.put(new JSONObject().put("action_neq", "jschallenge_failed"));
        filterANDAction.put(new JSONObject().put("action_neq", "jschallenge_bypassed"));
        filterANDAction.put(new JSONObject().put("action_neq", "managed_challenge_skipped"));
        filterANDAction.put(new JSONObject().put("action_neq", "managed_challenge_non_interactive_solved"));
        filterANDAction.put(new JSONObject().put("action_neq", "managed_challenge_interactive_solved"));
        filterANDAction.put(new JSONObject().put("action_neq", "managed_challenge_bypassed"));
        filterANDAction.put(filterOR);

        // filter AND
        JSONArray filterAND = new JSONArray();
        filterAND.put(filterDate);
        filterAND.put( new JSONObject().put("AND", filterANDAction));

        // filter
        JSONObject filter = new JSONObject();
        filter.put("AND", filterAND);

        // variables
        JSONObject variables = new JSONObject();
        variables.put("zoneTag", zone.zoneId);
        variables.put("limit", 1000);
        variables.put("filter", filter);
        variables.put("activityFilter", filter);

        // final object
        JSONObject data = new JSONObject();
        data.put("operationName", "ActivityLogQuery");
        data.put("variables", variables);
        data.put("query", new String(b));

        //Log.d("HERE", "refreshAnalytic: "+data.toString());
        return data;
    }

    private String getJSONDateForGraphQL(Date date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.toInstant().toString();
        } else {
            return Parser.dateToString(date);
        }
    }*/


}
