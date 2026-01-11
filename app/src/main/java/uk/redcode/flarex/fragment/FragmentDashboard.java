package uk.redcode.flarex.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.redcode.flarex.R;
import uk.redcode.flarex.module.AttackMode;
import uk.redcode.flarex.module.ChartBandwidth;
import uk.redcode.flarex.module.ChartPerformance;
import uk.redcode.flarex.module.ChartSecurity;
import uk.redcode.flarex.module.ChartVisitors;
import uk.redcode.flarex.module.DevMode;
import uk.redcode.flarex.module.TimeRange;
// import uk.redcode.flarex.module.WorldMap; // Removed
import uk.redcode.flarex.network.CFApi;
import uk.redcode.flarex.object.ChartStat;
// import uk.redcode.flarex.object.CountryStat; // Removed
import uk.redcode.flarex.object.Parser;
import uk.redcode.flarex.ui.Alert;
import uk.redcode.flarex.ui.LayoutManager;

public class FragmentDashboard extends FragmentCC {

    private View view;
    private ScrollView scrollView;

    // Modules
    private DevMode moduleDevMode;
    private AttackMode moduleAttackMode;
    public TimeRange moduleTimeRange;
    public ChartBandwidth moduleBandwidth;
    public ChartVisitors moduleVisitors;
    private ChartSecurity moduleSecurity;
    private ChartPerformance modulePerformance;
    // private WorldMap moduleMap; // Removed

    // data
    private ArrayList<ChartStat> chartStats = null;
    // private CountryStat worldStat = null; // Removed
    private Date lastUpdate = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        scrollView = root.findViewById(R.id.dashboard_scroll);

        container = root.findViewById(R.id.dashboard_container);
        container.removeAllViews();

        moduleDevMode = new DevMode(this);
        moduleDevMode.onDraw(inflater, container);

        moduleAttackMode = new AttackMode(this);
        moduleAttackMode.onDraw(inflater, container);

        initAnalytics(inflater, container);

        this.view = root;
        return root;
    }

    private void initAnalytics(LayoutInflater inflater, ViewGroup container) {
        if (!LayoutManager.get(LayoutManager.ANALYTICS)) return;

        moduleTimeRange = new TimeRange(this);
        moduleTimeRange.onDraw(inflater, container);

        moduleBandwidth = new ChartBandwidth(this, scrollView);
        moduleBandwidth.onDraw(inflater, container);

        moduleVisitors = new ChartVisitors(this, scrollView);
        moduleVisitors.onDraw(inflater, container);

        moduleSecurity = new ChartSecurity(this, scrollView);
        moduleSecurity.onDraw(inflater, container);

        modulePerformance = new ChartPerformance(this, scrollView);
        modulePerformance.onDraw(inflater, container);

        // moduleMap = new WorldMap(this, scrollView); // Removed
        // moduleMap.onDraw(inflater, container); // Removed
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        refresh();
    }

    private void refresh() {
        moduleDevMode.refresh(this);
        moduleAttackMode.refresh(this);

        if (!LayoutManager.get(LayoutManager.ANALYTICS)) return;
        // if (chartStats != null && worldStat != null && !needUpdate()) { // Modified
        if (chartStats != null && !needUpdate()) {
            displayChart(chartStats);
            // displayMap(worldStat); // Removed
        } else {
            refreshAnalytic();
        }
    }

    private boolean needUpdate() {
        if (lastUpdate == null) return true;
        if (forcedUpdate()) return true;

        long diff = new Date().getTime() - lastUpdate.getTime();
        long minute = diff / 1000 / 60;

        Log.d("HERE", "compareDate: "+ diff);
        return (minute >= 5);
    }

    private boolean forcedUpdate() {
        SharedPreferences sharedP = requireContext().getSharedPreferences("DASHBOARD", Context.MODE_PRIVATE);

        if (sharedP.getBoolean("force_update", false)) {
            sharedP.edit().putBoolean("force_update", false).apply();
            return true;
        }
        return false;
    }

    private void displayChart(ArrayList<ChartStat> stats) {
        if (!isVisible()) return;

        moduleBandwidth.update(stats);
        moduleVisitors.update(stats);
        moduleSecurity.update(stats);
        modulePerformance.update(stats);
    }

    // private void displayMap(CountryStat stat) { // Removed
    //    if (!isVisible()) return;
    //
    //    moduleMap.update(stat);
    // } // Removed

    public void setModuleLoading(int id, boolean b) {
        view.findViewById(id).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    public void refreshAnalytic() {
        CFApi.JSONListener listener = new CFApi.JSONListener() {
            @Override
            public void onResult(JSONObject body) {
                try {
                    lastUpdate = new Date();
                    JSONArray values = body.getJSONObject("data").getJSONObject("viewer").getJSONArray("zones").getJSONObject(0).getJSONArray("zones");
                    chartStats = ChartStat.parse(values);
                    // worldStat = CountryStat.parse(values); // Removed

                    displayChart(chartStats);
                    // displayMap(worldStat); // Removed
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getContext() == null) return;
                    alert(new Alert(Alert.ERROR, R.string.error_parsing_analytic));
                }
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                e.printStackTrace();
                alert(new Alert(Alert.ERROR, e.getMessage()));
            }
        };

        try {
            setModuleLoading(R.id.bandwidth_progress, true);
            setModuleLoading(R.id.visitors_progress, true);
            setModuleLoading(R.id.security_progress, true);
            setModuleLoading(R.id.performance_progress, true);
            // setModuleLoading(R.id.map_progress, true); // Removed
            JSONObject data = getDashboardData(
                    requireContext(),
                    moduleTimeRange.selected == TimeRange.TIME_24HOURS ? R.raw.graphql_dashboard : R.raw.graphql_dashboard_day,
                    moduleTimeRange.getRange(),
                    zone.zoneId,
                    moduleTimeRange.selected
            );

            CFApi.graphql(getMain(), data, listener);
        } catch (Exception e) {
            setLoading(false);
            e.printStackTrace();
            alert(new Alert(Alert.ERROR, e.getMessage()));
        }
    }

    /*
        Static
     */

    public static JSONObject getDashboardData(Context context, int file, long range, String zoneId, int timeRange) throws Exception {
        InputStream is = context.getResources().openRawResource(file);
        byte[] b = new byte[is.available()];
        is.read(b);

        String query = new String(b);

        Log.d("DEBUG", "getDashboardData: "+query);

        Date until = new Date();
        Date since = new Date(until.getTime() - range);

        JSONObject variables = new JSONObject();
        variables.put("zoneTag", zoneId);
        variables.put("until", getJSONDateForGraphQL(until, timeRange));
        variables.put("since", getJSONDateForGraphQL(since, timeRange));

        JSONObject data = new JSONObject();
        data.put("variables", variables);
        data.put("operationName", "GetZoneAnalytics");
        data.put("query", query);

        return data;
    }

    @SuppressLint("DefaultLocale")
    static private String getJSONDateForGraphQL(Date date, int timeRange) {
        if (timeRange != TimeRange.TIME_24HOURS) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.toInstant().toString();
        } else {
            return Parser.dateToString(date);
        }
    }
}
