/*
    ColdCloud - Module - Chart Visitor
    Author: Louis
    Version: 1.0
 */
package uk.redcode.flarex.module;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.ChartStat;

public class ChartVisitors extends Module implements OnChartValueSelectedListener {

    private ProgressBar progress;
    private TextView label;
    private TextView date;

    private LineChart chart;
    private ArrayList<ChartStat> stats;
    private List<Entry> chartEntries;
    private double max = 0;

    public ChartVisitors(FragmentDashboard parent, ScrollView scrollView) {
        super(parent, scrollView);
    }


    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.module_chart_visitor, container, false);

        progress = view.findViewById(R.id.visitors_progress);
        chart = view.findViewById(R.id.visitors_chart);
        label = view.findViewById(R.id.visitors_label);
        date = view.findViewById(R.id.visitors_date);

        chart.setOnChartValueSelectedListener(this);
        chart.setNoDataText(parent.getString(R.string.fetching_data));

        syncChartWithScroll(chart);
        container.addView(view);
    }

    public void update(ArrayList<ChartStat> stats) {
        this.stats = stats;
        List<Entry> visitors = new ArrayList<>();
        int i = 0; max = 0;

        for (ChartStat stat : stats) {
            // bandwidth - X = hours - Y = value
            if (stat.visitors >= max) max = stat.visitors;
            //Log.d("HERE", "displayChart: visitors: "+stat.visitors);
            visitors.add(new Entry(i++, (float) stat.visitors));
        }
        chartEntries = visitors;

        // create the 'line'
        LineDataSet lineVisitors = new LineDataSet(visitors, "Visitors");
        lineVisitors.setLineWidth(2.5f);
        lineVisitors.setColor(parent.getContext().getColor(R.color.primary));
        lineVisitors.setFillColor(parent.getContext().getColor(R.color.primary));
        lineVisitors.setValueTextColor(parent.getContext().getColor(R.color.white));
        lineVisitors.setFillDrawable(AppCompatResources.getDrawable(parent.getContext(), R.drawable.chart_gradient));
        lineVisitors.setDrawFilled(true);
        lineVisitors.setFillAlpha(100);
        lineVisitors.setDrawValues(false);
        lineVisitors.setDrawCircles(false);
        lineVisitors.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        // add all the 'line' needed to a single chart
        LineData chartData = new LineData(lineVisitors);

        // add to chart and invalidate
        if (visitors.size() > 0) chart.setData(chartData);
        if (visitors.size() == 0) chart.setNoDataText(parent.getString(R.string.not_enough_data));

        // style chart
        styleChart();

        // invalidate
        chart.invalidate();
        progress.setVisibility(View.INVISIBLE);
    }

    private void styleChart() {
        // axis
        YAxis left = chart.getAxisLeft();
        left.setDrawGridLines(false);
        left.setDrawLabels(false);
        left.setDrawAxisLine(false);
        left.setAxisMinimum(0);
        left.setAxisMaximum((float) (max*1.25));
        //left.setAxisMaximum((float) Parser.parseByte(max, Parser.MB));

        // disable right
        YAxis right = chart.getAxisRight();
        right.setEnabled(false);

        // style X
        XAxis x = chart.getXAxis();
        x.setDrawAxisLine(false);
        x.setDrawGridLines(false);
        x.setDrawLabels(false);

        // remove margin to fit screen
        //chart.setMinOffset(0f);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        chart.setPadding(0,0,0,0);

        // general styling
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);

        // reset zoom
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.resetZoom();
    }

    public void setSelectedValue(float position) {
        chart.setOnChartValueSelectedListener(null);
        chart.highlightValue(position, 0);
        updateLabel(chartEntries.get((int) position), new Highlight(0,0,0));
        chart.setOnChartValueSelectedListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        updateLabel(e, h);
        if (!AppParameter.getBoolean(parent.getContext(), AppParameter.SYNC_CHART, false)) return;
        //parent.moduleBandwidth.setSelectedValue(e, h);
        parent.moduleBandwidth.setSelectedValue(e.getX());
    }

    @SuppressLint("DefaultLocale")
    private void updateLabel(Entry e, Highlight h) {
        label.setText(String.format("%.0f %s", e.getY(), parent.getString(R.string.visitors)));
        date.setText(stats.get((int) e.getX()).getDateLabel(parent.moduleTimeRange.selected));
    }

    @Override
    public void onNothingSelected() {
        label.setText("");
        date.setText("");
    }
}
