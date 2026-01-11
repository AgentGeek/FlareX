package uk.redcode.flarex.module;

import android.annotation.SuppressLint;
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
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import uk.redcode.flarex.R;
import uk.redcode.flarex.dialog.DialogChart;
import uk.redcode.flarex.fragment.FragmentDashboard;
import uk.redcode.flarex.object.ChartStat;
import uk.redcode.flarex.object.ContentType;

public class ChartPerformance extends Module {

    private ProgressBar progress;
    private Chip label;

    private PieChart chartCached;
    private PieChart chartType;
    private PieData chartDataTypes;
    private ArrayList<ChartStat> stats;
    private ArrayList<ContentType> contentTypes;
    private final TypedValue textColor;

    public ChartPerformance(FragmentDashboard parent, ScrollView scrollView) {
        super(parent, scrollView);
        textColor = new TypedValue();
        parent.requireContext().getTheme().resolveAttribute(R.attr.chartTextColor, textColor, true);
    }


    @Override
    public void onDraw(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.module_chart_performance, container, false);

        label = view.findViewById(R.id.performance_label);
        label.setVisibility(View.INVISIBLE);
        progress = view.findViewById(R.id.performance_progress);
        chartType = view.findViewById(R.id.security_chart_type);
        chartCached = view.findViewById(R.id.security_chart_cached);

        // style Cached
        chartCached.setOnTouchListener(null);
        chartCached.getDescription().setEnabled(false);
        chartCached.getLegend().setEnabled(false);
        chartCached.setHoleRadius(80);
        chartCached.setHoleColor(android.R.color.transparent);
        chartCached.setCenterTextColor(textColor.data);
        chartCached.setCenterTextSize(18);
        chartCached.setDrawEntryLabels(false);
        chartCached.setNoDataText(parent.getString(R.string.fetching_data));


        styleType(chartType, false);
        chartType.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                label.setText(contentTypes.get((int) h.getX()).key);
                label.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                label.setVisibility(View.INVISIBLE);
            }
        });
        chartType.setOnChartGestureListener(new OnChartGestureListener() {
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
                dialog.title = "Types";
                dialog.setListener(chart -> {
                    styleType(chart, true);
                    chart.setData(chartDataTypes);
                    chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override public void onValueSelected(Entry e, Highlight h) { dialog.setLabel(contentTypes.get((int) h.getX()).key); }
                        @Override public void onNothingSelected() {
                            dialog.setLabel(null);
                        }
                    });
                });
                dialog.show(parent.getParentFragmentManager(), "tag");
            }
        });

        container.addView(view);
    }

    private void styleType(PieChart chartType, boolean dialog) {
        chartType.setRotationEnabled(dialog);
        chartType.getDescription().setEnabled(false);
        chartType.getLegend().setEnabled(false);
        chartType.setHoleRadius(50);
        chartType.setHoleColor(android.R.color.transparent);
        chartType.setTransparentCircleRadius(0);
        chartType.setDrawEntryLabels(dialog);
        chartType.setNoDataText(parent.getString(R.string.fetching_data));
    }


    public void update(ArrayList<ChartStat> stats) {
        this.stats = stats;

        updateCached();
        updateTypes();

        // invalidate
        chartCached.invalidate();
        chartType.invalidate();
        progress.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("DefaultLocale")
    private void updateCached() {
        // compute cached
        long bytes = 0, cached = 0;
        for (ChartStat stat : stats) {
            bytes += stat.bandwidth;
            cached += stat.cachedBandwidth;
        }

        int[] colors = {
            parent.getContext().getColor(R.color.divider),
            parent.getContext().getColor(R.color.secondary)
        };

        // create entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(bytes-cached, "Not Cached"));
        entries.add(new PieEntry(cached, "Cached"));

        // create dataset
        PieDataSet dataSet = new PieDataSet(entries, "Cached Bandwidth");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setDrawIcons(false);

        PieData chartData = new PieData(dataSet);

        // add to chart
        chartCached.setData(chartData);

        // set the %
        long percent = bytes == 0 ? 0 : (cached* 100L /bytes);
        chartCached.setCenterText(String.format("%d %%\nCached", percent));
    }

    private void updateTypes() {
        contentTypes = new ArrayList<>();
        for (ChartStat stat : stats) {
            for (ContentType type : stat.contentTypes) {

                // found
                boolean found = false;
                for (ContentType actual: contentTypes) {
                    if (actual.key.equals(type.key)) {
                        actual.bytes += type.bytes;
                        actual.requests += type.requests;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    contentTypes.add(type);
                }
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (ContentType type : contentTypes) {
            colors.add(type.getColor(parent.getContext()));
            entries.add(new PieEntry(type.requests, type.key));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Content Type");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setDrawIcons(false);
        chartDataTypes = new PieData(dataSet);

        // add to chart
        chartType.setData(chartDataTypes);
    }

}
