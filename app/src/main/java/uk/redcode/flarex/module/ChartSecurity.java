package uk.redcode.flarex.module;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.dialog.DialogChart;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.object.ChartStat;
import uk.redcode.flarex.object.Threat;

public class ChartSecurity extends Module implements OnChartValueSelectedListener {

    private ProgressBar progress;

    private PieChart chartTLS;
    private PieData chartDataTLS;

    private PieChart chartThreats;
    private PieData chartDataThreats;

    private ArrayList<ChartStat> stats;
    private final TypedValue textColor;

    public ChartSecurity(FragmentDashboard parent, ScrollView scrollView) {
        super(parent, scrollView);
        textColor = new TypedValue();
        parent.requireContext().getTheme().resolveAttribute(R.attr.chartTextColor, textColor, true);
    }

    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.module_chart_security, container, false);

        progress = view.findViewById(R.id.security_progress);
        chartTLS = view.findViewById(R.id.security_chart_tls);
        chartThreats = view.findViewById(R.id.security_chart_threat);

        // style TLS
        //chartTLS.setOnChartValueSelectedListener(this);
        styleTLS(chartTLS);
        chartTLS.setOnChartGestureListener(new OnChartGestureListener() {
            @Override public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartDoubleTapped(MotionEvent me) {}
            @Override public void onChartSingleTapped(MotionEvent me) {}
            @Override public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
            @Override public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
            @Override public void onChartTranslate(MotionEvent me, float dX, float dY) {}

            @Override
            public void onChartLongPressed(MotionEvent me) {
                DialogChart dialog = new DialogChart();
                dialog.title = "TLS";
                dialog.setListener(chart -> {
                    styleTLS(chart);
                    chart.setData(chartDataTLS);
                });
                dialog.show(parent.getParentFragmentManager(), "tag");
            }
        });

        // style Threats
        styleThreats(chartThreats);
        chartThreats.setOnChartGestureListener(new OnChartGestureListener() {
            @Override public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartDoubleTapped(MotionEvent me) {}
            @Override public void onChartSingleTapped(MotionEvent me) {}
            @Override public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
            @Override public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
            @Override public void onChartTranslate(MotionEvent me, float dX, float dY) {}

            @Override
            public void onChartLongPressed(MotionEvent me) {
                DialogChart dialog = new DialogChart();
                dialog.title = "Threats";
                dialog.setListener(chart -> {
                    styleThreats(chart);
                    chart.setData(chartDataThreats);
                });
                dialog.show(parent.getParentFragmentManager(), "tag");
            }
        });

        container.addView(view);
    }

    private void styleThreats(PieChart chart) {
        chart.setOnChartValueSelectedListener(this);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setCenterText(parent.getString(R.string.threats));
        chart.setCenterTextColor(textColor.data);
        chart.setCenterTextSize(22f);
        chart.setTransparentCircleRadius(0f);
        chart.setHoleRadius(60);
        chart.setHoleColor(parent.requireContext().getColor(android.R.color.transparent));
        chart.setRotationEnabled(false);
        chart.setNoDataText(parent.getString(R.string.fetching_data));
    }

    private void styleTLS(PieChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setCenterText(parent.getString(R.string.tls_version));
        chart.setHoleRadius(0f);
        chart.setTransparentCircleRadius(0);
        chart.setCenterTextRadiusPercent(0f);
        chart.setRotationEnabled(false);
        chart.setNoDataText(parent.getString(R.string.fetching_data));
    }


    public void update(ArrayList<ChartStat> stats) {
        this.stats = stats;

        updateTLS();
        updateThreats();

        // invalidate
        chartTLS.invalidate();
        chartThreats.invalidate();
        progress.setVisibility(View.INVISIBLE);
    }

    /*
        TLS
     */

    private void updateTLS() {
        // compute total
        int tlsNone = 0, tls10 = 0, tls12 = 0, tls13 = 0;
        int http1 = 0, http2 = 0, http3 = 0;
        for (ChartStat stat : stats) {
            tlsNone += stat.tlsNone;
            tls10 += stat.tls10;
            tls12 += stat.tls12;
            tls13 += stat.tls13;
        }

        // create entries
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (tlsNone > 0) {
            entries.add(new PieEntry(tlsNone, "No TLS") );
            colors.add(parent.requireContext().getColor(R.color.tls_none));
        }
        if (tls10 > 0) {
            entries.add(new PieEntry(tls10, "TLS 1.0"));
            colors.add(parent.requireContext().getColor(R.color.tls10));
        }
        if (tls12 > 0) {
            entries.add(new PieEntry(tls12, "TLS 1.2") );
            colors.add(parent.requireContext().getColor(R.color.tls12));
        }
        if (tls13 > 0) {
            entries.add(new PieEntry(tls13, "TLS 1.3") );
            colors.add(parent.requireContext().getColor(R.color.tls13));
        }

        // create dataset
        PieDataSet dataSet = new PieDataSet(entries, "TLS Request");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setDrawIcons(false);
        chartDataTLS = new PieData(dataSet);

        // add to chart
        chartTLS.setData(chartDataTLS);
    }

    /*
        Threats
     */

    private void updateThreats() {
        ArrayList<Threat> total = cumulateThreats();
        int totalThreats = 0;

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (Threat t : total) {
            entries.add(new PieEntry(t.count, t.getLabel(parent.getContext())) );
            colors.add(t.getColor(parent.getContext()));
            totalThreats += t.count;
        }

        @SuppressLint("DefaultLocale") String label = String.format("%d %s", totalThreats, parent.getString(R.string.threats));
        chartThreats.setCenterText(label);

        // create dataset
        PieDataSet dataSet = new PieDataSet(entries, "Threats");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setDrawIcons(false);
        chartDataThreats = new PieData(dataSet);

        // add to chart
        chartThreats.setData(chartDataThreats);
    }

    private ArrayList<Threat> cumulateThreats() {
        ArrayList<Threat> total = new ArrayList<>();
        int unknown = 0;

        // scroll stat
        for (ChartStat stat : stats) {
            unknown += stat.threats;

            // scroll threats
            for (Threat threat : stat.threatType) {
                unknown -= threat.count;

                boolean found = false;
                for (Threat actual : total) {
                    // verify if is already inside array
                    if (actual.key.equals(threat.key)) {
                        found = true;
                        actual.count += threat.count;
                    }
                }

                // add it if not
                if (!found) total.add(threat);
            }
        }

        if (unknown > 0) total.add(new Threat(unknown, "unknown"));
        return total;
    }

    /*
        Listener
     */

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
        //label.setText("");
    }
}


