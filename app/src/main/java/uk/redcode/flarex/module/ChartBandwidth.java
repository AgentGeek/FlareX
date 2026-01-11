/*
    ColdCloud - Module - Chart Bandwidth
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
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import uk.redcode.flarex.R;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.object.AppParameter;
import uk.redcode.flarex.object.ChartStat;
import uk.redcode.flarex.object.Parser;

public class ChartBandwidth extends Module implements OnChartValueSelectedListener {

    private ProgressBar progress;
    private TextView label;
    private TextView date;
    private Chip typeChip;

    private LineChart chart;
    private ArrayList<ChartStat> stats;
    private List<Entry> chartEntries;
    private int type = Parser.MB;
    private double max = 0;

    public ChartBandwidth(FragmentDashboard parent, ScrollView scrollView) {
        super(parent, scrollView);
    }

    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.module_chart_bandwidth, container, false);

        progress = view.findViewById(R.id.bandwidth_progress);
        chart = view.findViewById(R.id.bandwidth_chart);
        label = view.findViewById(R.id.bandwidth_label);
        date = view.findViewById(R.id.bandwidth_date);
        typeChip = view.findViewById(R.id.bandwidth_type);

        typeChip.setOnClickListener(v -> updateType());
        typeChip.setText(Parser.getByteLabel(type));

        chart.setOnChartValueSelectedListener(this);
        chart.setNoDataText(parent.getString(R.string.fetching_data));

        syncChartWithScroll(chart);
        container.addView(view);
    }

    private void updateType() {
        if (type >= Parser.GB) {
            type = Parser.BYTE;
        } else {
            type++;
        }


        typeChip.setText(Parser.getByteLabel(type));
        chart.clearValues();
        update(stats);
    }

    public void update(ArrayList<ChartStat> stats) {
        this.stats = stats;
        List<Entry> bandwidths = new ArrayList<>();
        List<Entry> cachedBandwidths = new ArrayList<>();
        max = 0;
        double maxCached = 0;
        int i = 0;

        for (ChartStat stat : stats) {
            // bandwidth - X = hours - Y = value
            double bw = Parser.parseByte(stat.bandwidth, type);
            double cachedBw = Parser.parseByte(stat.cachedBandwidth, type);

            if (bw >= max) max = bw;
            if (cachedBw >= maxCached) maxCached = cachedBw;
            //Log.d("HERE", "displayChart: "+String.format("%d %fMb max: %f", stat.bandwidth, bw , (float)Parser.parseByte(max, type)));
            bandwidths.add(new Entry(i, (float) bw));
            cachedBandwidths.add(new Entry(i++, (float) cachedBw));
        }
        chartEntries = bandwidths;

        // create the 'line'
        LineDataSet lineBandwidth = new LineDataSet(bandwidths, "Bandwidth");
        lineBandwidth.setLineWidth(2.5f);
        lineBandwidth.setColor(parent.getContext().getColor(R.color.primary));
        lineBandwidth.setFillColor(parent.getContext().getColor(R.color.primary));
        lineBandwidth.setValueTextColor(parent.getContext().getColor(R.color.white));
        lineBandwidth.setDrawFilled(true);
        lineBandwidth.setFillDrawable(AppCompatResources.getDrawable(parent.getContext(), R.drawable.chart_gradient));
        lineBandwidth.setFillAlpha(100);
        lineBandwidth.setDrawValues(false);
        lineBandwidth.setDrawCircles(false);
        lineBandwidth.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // create the cached 'line'
        LineDataSet lineCached = new LineDataSet(cachedBandwidths, "Cached");
        lineCached.setLineWidth(1.5f);
        lineCached.setColor(parent.getContext().getColor(R.color.purple_200));
        lineCached.setFillColor(parent.getContext().getColor(R.color.purple_200));
        lineCached.setValueTextColor(parent.getContext().getColor(R.color.white));
        lineCached.setDrawFilled(true);
        lineBandwidth.setFillDrawable(AppCompatResources.getDrawable(parent.getContext(), R.drawable.chart_gradient));
        lineCached.setFillAlpha(10);
        lineCached.setDrawValues(false);
        lineCached.setDrawCircles(false);
        lineCached.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        // add all the 'line' needed to a single chart
        LineData chartData = new LineData(lineBandwidth);
        chartData.addDataSet(lineCached);

        // style chart
        styleChart();

        // add to chart and invalidate
        if (bandwidths.size() > 0) chart.setData(chartData);
        if (bandwidths.size() == 0) chart.setNoDataText(parent.getString(R.string.not_enough_data));
        chart.notifyDataSetChanged();

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
        left.setAxisMinimum((float) -(max*0.25));
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
        //parent.moduleVisitors.setSelectedValue(e, h);
        parent.moduleVisitors.setSelectedValue(e.getX());
    }

    @SuppressLint("DefaultLocale")
    private void updateLabel(Entry e, Highlight h) {
        String c = h.getDataSetIndex() == 1 ? " Cached" : "";
        label.setText(String.format("%s %.2f %s", c, e.getY(), Parser.getByteLabel(type)));
        date.setText(stats.get((int) e.getX()).getDateLabel(parent.moduleTimeRange.selected));
    }

    @Override
    public void onNothingSelected() {
        label.setText("");
        date.setText("");
    }
}
